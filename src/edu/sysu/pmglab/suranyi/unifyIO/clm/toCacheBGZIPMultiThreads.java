package edu.sysu.pmglab.suranyi.unifyIO.clm;

import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZIPBlockWriter;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZOutputParam;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.IBlockWriter;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author suranyi
 */
class toCacheBGZIPMultiThreads implements To {
    final FileStream file;
    final IBlockWriter<VolumeByteStream>[] caches;
    final Semaphore[] semaphores;
    final BGZOutputParam bgzOutputParam;
    final AtomicBoolean finishAssigned = new AtomicBoolean(false);

    public toCacheBGZIPMultiThreads(String outputFileName, final BGZOutputParam bgzOutputParam, int nThreads) throws IOException {
        this.bgzOutputParam = bgzOutputParam;
        this.file = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
        this.caches = new IBlockWriter[nThreads];
        this.semaphores = new Semaphore[nThreads];
        for (int i = 0; i < semaphores.length; i++) {
            this.semaphores[i] = new Semaphore(0);
            this.caches[i] = IBlockWriter.getVolumeByteStreamInstance(this.bgzOutputParam);
            this.caches[i].start();
        }
    }

    @Override
    public void write(byte element) throws IOException {
        write(new byte[]{element}, 0, 1);
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        IBlockWriter<VolumeByteStream> outputStream = IBlockWriter.getVolumeByteStreamInstance(this.bgzOutputParam, length);
        outputStream.write(src, offset, length);
        outputStream.finish();
        this.file.write(outputStream.getCache());
        outputStream.close();
    }

    @Override
    public void write(VolumeByteStream src, int offset, int length) throws IOException {
        write(src.getCache(), offset, length);
    }

    /**
     * 写入 1 字节
     * @param element 写入的元素
     */
    @Override
    public void write(int contextId, byte element) throws IOException {
        caches[contextId].write(element);
    }

    /**
     * 将源数据 src 的数据，从 offset 开始的 length 长度数据写入文件
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入的长度
     */
    @Override
    public void write(int contextId, byte[] src, int offset, int length) throws IOException {
        caches[contextId].write(src, offset, length);
    }

    /**
     * 将源数据定容数组从 offset 开始 连续的 length 长度写入文件
     * @param src 源数据，定容数组
     * @param offset 偏移量
     * @param length 写入的长度
     */
    @Override
    public void write(int contextId, VolumeByteStream src, int offset, int length) throws IOException {
        caches[contextId].write(src.getCache(), offset, length);
    }

    @Override
    public void flush(int contextId) throws InterruptedException, IOException {
        caches[contextId].finish();
        VolumeByteStream out = caches[contextId].getCache();
        this.semaphores[contextId].acquire();
        if (out.size() > 0) {
            this.file.write(out);
            caches[contextId].start();
        }
        this.semaphores[(contextId == this.semaphores.length - 1) ? 0 : contextId + 1].release();
    }

    @Override
    public void flush(int contextId, Runnable runnable) throws InterruptedException, IOException {
        caches[contextId].finish();
        VolumeByteStream out = caches[contextId].getCache();
        this.semaphores[contextId].acquire();
        if (out.size() > 0) {
            this.file.write(out);
            out.reset();
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
        caches[contextId].finish();
        VolumeByteStream out = caches[contextId].getCache();
        this.semaphores[contextId].acquire();
        if (out.size() > 0) {
            this.file.write(out);
            out.reset();
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
        this.file.write(BGZIPBlockWriter.EMPTY_GZIP_BLOCK);
        this.file.close();
    }

    @Override
    public void startWriting() {
        this.semaphores[0].release();
    }
}