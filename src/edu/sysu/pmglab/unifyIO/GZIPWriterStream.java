package edu.sysu.pmglab.unifyIO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

/**
 * @Data        :2021/09/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :GZIP 文件写出
 */

class GZIPWriterStream extends IFileStream {
    private GZIPOutputStream file;

    GZIPWriterStream(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        this.file = new GZIPOutputStream(new FileOutputStream(fileName));
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
    public void flush() throws IOException {
        this.file.flush();
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

