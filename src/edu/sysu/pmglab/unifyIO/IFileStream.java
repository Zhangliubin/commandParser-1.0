package edu.sysu.pmglab.unifyIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :抽象类
 */

public abstract class IFileStream {
    public byte read() throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public int read(byte[] dst) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public int read(byte[] dst, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void seek(long pos) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public long tell() throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public long size() throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void write(byte element) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void write(byte[] src) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void write(byte[] src, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void write(ByteBuffer buffer) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void writeTo(long position, long count, FileChannel channelWriter) throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void close() throws IOException{
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public boolean seekAvailable(){
        return false;
    }

    public FileChannel getChannel() {
        throw new UnsupportedOperationException("Invalid Exception");
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException("Invalid Exception");
    }
}
