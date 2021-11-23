package edu.sysu.pmglab.suranyi.unifyIO;

import bgzf4j.BGZFOutputStream;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZOutputParam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author suranyi
 * @description bgz 文件写出方法
 */

public class BGZIPWriterStream extends IFileStream {
    private BGZFOutputStream file;

    public BGZIPWriterStream(String fileName) throws IOException {
        this(fileName, BGZOutputParam.DEFAULT_LEVEL);
    }

    public BGZIPWriterStream(String fileName, BGZOutputParam param) throws IOException {
        this(fileName, param.level);
    }

    public BGZIPWriterStream(String fileName, int compressionLevel) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        this.file = new BGZFOutputStream(fileName, compressionLevel);
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
        this.file.write(buffer.array(), buffer.position(), buffer.limit() - buffer.position());
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

