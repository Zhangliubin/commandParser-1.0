package edu.sysu.pmglab.unifyIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :默认文件读取
 */

class DefaultReaderStream extends IFileStream {
    protected RandomAccessFile file;

    DefaultReaderStream() {
    }

    DefaultReaderStream(String fileName) throws IOException {
        file = new RandomAccessFile(fileName, "r");
    }

    @Override
    public byte read() throws IOException {
        return (byte) file.read();
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return file.read(dst);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        return file.read(dst, offset, length);
    }

    @Override
    public void seek(long pos) throws IOException {
        file.seek(pos);
    }

    @Override
    public long tell() throws IOException {
        return file.getFilePointer();
    }

    @Override
    public long size() throws IOException {
        return file.length();
    }

    @Override
    public void close() throws IOException {
        file.close();
        file = null;
    }

    @Override
    public FileChannel getChannel() {
        return file.getChannel();
    }

    @Override
    public boolean seekAvailable() {
        return true;
    }
}
