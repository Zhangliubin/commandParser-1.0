package edu.sysu.pmglab.suranyi.unifyIO.pbgzip;

import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.suranyi.container.Pair;
import edu.sysu.pmglab.suranyi.threadPool.Block;
import edu.sysu.pmglab.suranyi.threadPool.DynamicPipeline;
import edu.sysu.pmglab.suranyi.threadPool.ThreadPool;
import edu.sysu.pmglab.suranyi.unifyIO.clm.MultiThreadsWriter;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZOutputParam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

public class ParallelBGZFOutputStream extends OutputStream {
    private final MultiThreadsWriter writer;
    private UncompressedBGZBlock block;
    private final LinkedBlockingDeque<UncompressedBGZBlock> compressedPipLine;
    private final DynamicPipeline<Boolean, UncompressedBGZBlock> uncompressedPipLine;
    private final ThreadPool threadPool;

    public ParallelBGZFOutputStream(String file, int nThreads, int compressionLevel) throws IOException {
        this.writer = new MultiThreadsWriter(file, new BGZOutputParam(true, compressionLevel), nThreads);
        this.uncompressedPipLine = new DynamicPipeline<>(nThreads);
        this.compressedPipLine = new LinkedBlockingDeque<>(nThreads);

        try {
            for (int i = 0; i < nThreads; i++) {
                this.compressedPipLine.put(new UncompressedBGZBlock());
            }
        } catch (Exception ignored) {
        }

        // 用于进行数据压缩的线程池
        this.threadPool = new ThreadPool(nThreads);
        this.threadPool.submit(() -> {
            try {
                Pair<Integer, Block<Boolean, UncompressedBGZBlock>> initInfo = writer.getContextId(uncompressedPipLine::get);
                int localId = initInfo.key;
                Block<Boolean, UncompressedBGZBlock> block = initInfo.value;

                if (block.getStatus()) {
                    do {
                        // 写入数据
                        writer.write(localId, block.getData().uncompressedBuffer, 0, block.getData().seek);

                        // 数据完成，打印
                        this.compressedPipLine.put(block.getData().finish());

                        // 提取下一部分数据
                        block = writer.flush(localId, uncompressedPipLine::get);
                    } while (block.getStatus());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, nThreads);
    }

    public ParallelBGZFOutputStream(String file, int nThreads) throws IOException {
        this(file, nThreads, BGZOutputParam.DEFAULT_LEVEL);
    }

    public ParallelBGZFOutputStream(String file, int nThreads, BGZOutputParam param) throws IOException {
        this(file, nThreads, param.level);
    }

    public int getThreadsNum() {
        return this.writer.getThreadsNum();
    }

    @Override
    public void write(final int bite) throws IOException {
        try {
            if (this.block == null) {
                this.block = this.compressedPipLine.take();
            }

            this.block.uncompressedBuffer[this.block.seek++] = (byte) bite;

            if (this.block.seek == this.block.uncompressedBuffer.length) {
                this.uncompressedPipLine.put(true, this.block);
                this.block = null;
            }
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    public void write(final ByteBuffer bytes) throws IOException {
        write(bytes.array(), bytes.position(), bytes.limit());
    }

    @Override
    public void write(final byte[] bytes, int startIndex, int numBytes) throws IOException {
        try {
            while (numBytes > 0) {
                if (this.block == null) {
                    this.block = this.compressedPipLine.take();
                }

                final int bytesToWrite = Math.min(this.block.uncompressedBuffer.length - this.block.seek, numBytes);
                System.arraycopy(bytes, startIndex, this.block.uncompressedBuffer, this.block.seek, bytesToWrite);
                this.block.seek += bytesToWrite;
                startIndex += bytesToWrite;
                numBytes -= bytesToWrite;

                if (this.block.seek == this.block.uncompressedBuffer.length) {
                    this.uncompressedPipLine.put(true, this.block);
                    this.block = null;
                }
            }
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        // 刷入缓冲数据
        if (this.block != null && this.block.seek > 0) {
            try {
                this.uncompressedPipLine.put(true, this.block);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }

            this.block = null;
        }


        // 发出关闭信号
        try {
            this.uncompressedPipLine.putStatus(this.writer.getThreadsNum(), false);
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }

        threadPool.close();
        writer.close();
    }
}


class UncompressedBGZBlock {
    final static int RARE = 2;
    final byte[] uncompressedBuffer = new byte[BGZFStreamConstants.DEFAULT_UNCOMPRESSED_BLOCK_SIZE << RARE];
    int seek = 0;

    UncompressedBGZBlock() {
    }

    public UncompressedBGZBlock finish() {
        this.seek = 0;
        return this;
    }
}