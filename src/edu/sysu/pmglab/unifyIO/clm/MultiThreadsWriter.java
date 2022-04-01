package edu.sysu.pmglab.unifyIO.clm;

import edu.sysu.pmglab.check.Assert;
import edu.sysu.pmglab.container.Pair;
import edu.sysu.pmglab.container.VolumeByteStream;
import edu.sysu.pmglab.threadPool.ThreadPool;
import edu.sysu.pmglab.unifyIO.partwriter.BGZOutputParam;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author suranyi
 * @description 多线程并行写入数据方法 (该方法基于 Cyclic Locking Mechanism, CLM 算法进行)
 */

public class MultiThreadsWriter {
    /**
     * 多线程 IO 方法
     * 该方法需要结合线程 ID 使用
     *
     * 请注意: 多线程 bgzip 写入模式着重优化并行写入方法。而直接刷入文件的方法没有进行优化，因此应当尽量避免写入少量字节！
     */
    final To toMethod;
    final AtomicInteger contextId = new AtomicInteger(0);
    final int nThreads;

    public MultiThreadsWriter(String outputFileName, int nThreads) throws IOException {
        this.nThreads = nThreads;
        Assert.that(nThreads >= 1, "nThreads < 1");

        if (nThreads > 1) {
            toMethod = new toCacheMultiThreads(outputFileName, nThreads);
        } else {
            // 单线程时，不启用缓冲区，而是直接写入到文件
            toMethod = new toFileSingleThread(outputFileName);
        }
    }

    public int getThreadsNum() {
        return this.nThreads;
    }

    public MultiThreadsWriter(String outputFileName, BGZOutputParam bgzOutputParam, int nThreads) throws IOException {
        this.nThreads = nThreads;
        Assert.that(nThreads >= 1, "nThreads < 1");

        if (nThreads > 1) {
            if (bgzOutputParam.toBGZF) {
                toMethod = new toCacheBGZIPMultiThreads(outputFileName, bgzOutputParam, nThreads);
            } else {
                toMethod = new toCacheMultiThreads(outputFileName, nThreads);
            }
        } else {
            // 单线程时，不启用缓冲区，而是直接写入到文件
            toMethod = new toFileSingleThread(outputFileName, bgzOutputParam);
        }
    }

    /**
     * 写入 1 字节
     * @param element 写入的元素
     */
    public void write(byte element) throws IOException {
        toMethod.write(element);
    }

    /**
     * 将源数据 src 的数据写入文件
     * @param src 源数据
     */
    public void write(byte[] src) throws IOException {
        toMethod.write(src, 0, src.length);
    }

    /**
     * 将源数据 src 的数据，从 offset 开始的 length 长度数据写入文件
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(byte[] src, int offset, int length) throws IOException {
        toMethod.write(src, offset, length);
    }

    /**
     * 写入 1 字节
     * @param contextId 线程 ID
     * @param element 写入的元素
     */
    public void write(int contextId, byte element) throws IOException {
        toMethod.write(contextId, element);
    }

    /**
     * 将源数据 src 的数据写入文件
     * @param contextId 线程 ID
     * @param src 源数据
     */
    public void write(int contextId, byte[] src) throws IOException {
        toMethod.write(contextId, src, 0, src.length);
    }

    /**
     * 将源数据 src 的数据，从 offset 开始的 length 长度数据写入文件
     * @param contextId 线程 ID
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(int contextId, byte[] src, int offset, int length) throws IOException {
        toMethod.write(contextId, src, offset, length);
    }

    /**
     * 将源数据定容数组写入文件
     * @param contextId 线程 ID
     * @param src 源数据，定容数组
     */
    public void write(int contextId, VolumeByteStream src) throws IOException {
        toMethod.write(contextId, src.getCache(), 0, src.size());
    }

    /**
     * 将源数据定容数组写入文件
     * @param src 源数据，定容数组
     */
    public void write(VolumeByteStream src) throws IOException {
        toMethod.write(src.getCache(), 0, src.size());
    }

    /**
     * 将源数据定容数组从 offset 开始写入文件
     * @param src 源数据，定容数组
     * @param offset 偏移量
     */
    public void write(VolumeByteStream src, int offset) throws IOException {
        toMethod.write(src.getCache(), offset, src.size() - offset);
    }

    /**
     * 将源数据定容数组从 offset 开始 连续的 length 长度写入文件
     * @param contextId 线程 ID
     * @param src 源数据，定容数组
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(int contextId, VolumeByteStream src, int offset, int length) throws IOException {
        toMethod.write(contextId, src.getCache(), offset, length);
    }

    /**
     * 将源数据定容数组从 offset 开始 连续的 length 长度写入文件
     * @param src 源数据，定容数组
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(VolumeByteStream src, int offset, int length) throws IOException {
        toMethod.write(src.getCache(), offset, length);
    }

    /**
     * 刷新缓冲区数据
     * @param contextId 线程 ID
     */
    public void flush(int contextId) throws IOException, InterruptedException {
        toMethod.flush(contextId);
    }

    /**
     * 刷新缓冲区数据
     * @param contextId 线程 ID
     */
    public <T> T flush(int contextId, Callable<T> runnable) throws Exception {
        return toMethod.flush(contextId, runnable);
    }

    /**
     * 刷新缓冲区数据
     * @param contextId 线程 ID
     */
    public void flush(int contextId, Runnable runnable) throws Exception {
        toMethod.flush(contextId, runnable);
    }

    /**
     * 关闭
     */
    public void close() throws IOException {
        toMethod.close();
    }

    /**
     * 获取线程 ID
     * @return ID 编号
     */
    public int getContextId() throws IOException {
        synchronized (contextId) {
            if (contextId.get() == nThreads) {
                throw new IOException("Exceeds the specified number of threads.");
            }

            try {
                return contextId.getAndAdd(1);
            } finally {
                if (contextId.get() == nThreads) {
                    toMethod.startWriting();
                }
            }
        }
    }

    /**
     * 获取线程 ID
     * @param runnable 获取 ID 的同时需要执行的方法
     * @return ID 编号
     */
    public int getContextId(Runnable runnable) throws IOException {
        synchronized (contextId) {
            if (contextId.get() == nThreads) {
                throw new IOException("Exceeds the specified number of threads.");
            }

            try {
                return contextId.getAndAdd(1);
            } finally {
                runnable.run();
                if (contextId.get() == nThreads) {
                    toMethod.startWriting();
                }
            }
        }
    }

    /**
     * 获取线程 ID
     * @param callable 获取 ID 的同时需要执行并产生回调值的方法
     * @return <ID 编号, 运行返回值>
     */
    public <Out> Pair<Integer, Out> getContextId(Callable<Out> callable) throws Exception {
        synchronized (contextId) {
            if (contextId.get() == nThreads) {
                throw new IOException("Exceeds the specified number of threads.");
            }

            try {
                return new Pair(contextId.getAndAdd(1), callable.call());
            } finally {
                if (contextId.get() == nThreads) {
                    toMethod.startWriting();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // 创建并行写入器
        MultiThreadsWriter writer = new MultiThreadsWriter("test.vcf.gz", new BGZOutputParam(true, 5), 12);
        // 写入信息 (该信息是不需要通过并行产生的部分，因此调用全局的 IO 方法)
        writer.write("global Info".getBytes());
        // 创建并行处理线程, 这个多线程包也是重新设计的...
        ThreadPool pool = new ThreadPool(12);
        // 提交多线程任务
        pool.submit(() -> {
            try {
                /**
                 * 分配线程 ID, 该步骤是必要的。当线程 ID 个数满足先前传入的 nThreads 时，自动释放第一个线程的锁。而超过时报错、不足时无法写入文件。
                 * 还提供以下两种方法，表示在获得锁的同时需要一并执行的任务。常用于通过管道 IO 输入数据的情形。此时可以保证线程 i 一定分配到第 i 个任务。
                 * int getContextId(Runnable runnable)
                 * <Out> Pair<Integer, Out> getContextId(Callable<Out> callable)
                 */
                int localId = writer.getContextId();

                // TODO: 线程处理任务, 并产生数据 (此处可以将计算任务存放在一个线性数组中，通过 localId 进行分配。或将计算任务通过管道形式输入)
                byte[] data = new byte[8192];

                // 写入数据 (当 nThreads=1 时，数据将直接写入文件；当 nThreads>1 时, 数据先被写入每一个 localId 的缓冲区)
                // 请注意，此时需要携带线程 ID 编号，以表示将数据写入指定的线程缓冲区
                writer.write(localId, data);

                /**
                 * 刷入磁盘 (此时会进入等待状态，按照 localId 的顺序将数据写入文件)
                 * 还提供以下两种方法，表示在写入磁盘的同时一并执行的任务
                 * void flush(int contextId, Runnable runnable)
                 * <T> T flush(int contextId, Callable<T> runnable)
                 */
                writer.flush(localId);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, 12);
        pool.close();

        // 关闭文件
        writer.close();
    }
}

