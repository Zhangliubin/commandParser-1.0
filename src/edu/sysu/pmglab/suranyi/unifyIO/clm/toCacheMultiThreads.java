package edu.sysu.pmglab.suranyi.unifyIO.clm;

import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author suranyi
 */

class toCacheMultiThreads implements To {
    final FileStream file;
    final VolumeByteStream[] caches;
    final Semaphore[] semaphores;
    final AtomicBoolean finishAssigned = new AtomicBoolean(false);

    public toCacheMultiThreads(String outputFileName, int nThreads) throws IOException {
        this.file = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
        this.caches = new VolumeByteStream[nThreads];
        for (int i = 0; i < nThreads; i++) {
            this.caches[i] = new VolumeByteStream(2 << 20);
        }
        this.semaphores = new Semaphore[nThreads];
        for (int i = 0; i < semaphores.length; i++) {
            semaphores[i] = new Semaphore(0);
        }
    }

    @Override
    public void write(byte element) throws IOException {
        this.file.write(element);
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void write(VolumeByteStream src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    /**
     * 写入 1 字节
     * @param element 写入的元素
     */
    @Override
    public void write(int contextId, byte element) throws IOException {
        caches[contextId].writeSafety(element);
    }

    /**
     * 将源数据 src 的数据，从 offset 开始的 length 长度数据写入文件
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入的长度
     */
    @Override
    public void write(int contextId, byte[] src, int offset, int length) throws IOException {
        caches[contextId].writeSafety(src, offset, length);
    }

    /**
     * 将源数据定容数组从 offset 开始 连续的 length 长度写入文件
     * @param src 源数据，定容数组
     * @param offset 偏移量
     * @param length 写入的长度
     */
    @Override
    public void write(int contextId, VolumeByteStream src, int offset, int length) throws IOException {
        caches[contextId].writeSafety(src.getCache(), offset, length);
    }

    @Override
    public void flush(int contextId) throws InterruptedException, IOException {
        this.semaphores[contextId].acquire();
        if (caches[contextId].size() > 0) {
            this.file.write(caches[contextId]);
            caches[contextId].reset();
        }
        this.semaphores[(contextId == this.semaphores.length - 1) ? 0 : contextId + 1].release();
    }

    @Override
    public void flush(int contextId, Runnable runnable) throws InterruptedException, IOException {
        this.semaphores[contextId].acquire();
        if (caches[contextId].size() > 0) {
            this.file.write(caches[contextId]);
            caches[contextId].reset();
        }

        try {
            if (runnable != null) {
                runnable.run();
            }
        } finally {
            this.semaphores[(contextId == this.semaphores.length - 1) ? 0 : contextId + 1].release();
        }
    }


    @Override
    public <T> T flush(int contextId, Callable<T> callable) throws Exception {
        this.semaphores[contextId].acquire();
        if (caches[contextId].size() > 0) {
            this.file.write(caches[contextId]);
            caches[contextId].reset();
        }

        try {
            if (callable != null) {
                return callable.call();
            } else {
                return null;
            }
        } finally {
            this.semaphores[(contextId == this.semaphores.length - 1) ? 0 : contextId + 1].release();
        }
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }

    @Override
    public void startWriting() {
        this.semaphores[0].release();
    }
}