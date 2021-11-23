package edu.sysu.pmglab.suranyi.unifyIO.clm;

import edu.sysu.pmglab.suranyi.unifyIO.IFileStream;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZOutputParam;
import edu.sysu.pmglab.suranyi.unifyIO.pbgzip.ParallelBGZFOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author suranyi
 */

public class PBGZIPWriterStream extends IFileStream {
    /**
     * 并行 bgzip 写入方法
     */
    private ParallelBGZFOutputStream file;

    public PBGZIPWriterStream(String fileName) throws IOException {
        this(fileName, 3, BGZOutputParam.DEFAULT_LEVEL);
    }

    public PBGZIPWriterStream(String fileName, int threads) throws IOException {
        this(fileName, threads, BGZOutputParam.DEFAULT_LEVEL);
    }

    public PBGZIPWriterStream(String fileName, int threads, int compressionLevel) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        this.file = new ParallelBGZFOutputStream(fileName, threads, compressionLevel);
    }

    @Override
    public void write(byte bite) throws IOException {
        this.file.write(bite);
    }

    @Override
    public void write(byte[] src) throws IOException {
        this.file.write(src);
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        this.file.write(buffer);
    }

    @Override
    public void close() throws IOException {
        this.file.close();
        this.file = null;
    }

    @Override
    public boolean seekAvailable() {
        return false;
    }
}

