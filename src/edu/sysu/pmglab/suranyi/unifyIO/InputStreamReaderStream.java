package edu.sysu.pmglab.suranyi.unifyIO;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Data        :2021/06/18
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :inputstream 数据读入
 */

public class InputStreamReaderStream extends IFileStream {
    final InputStream inputStream;
    long pointer;

    public InputStreamReaderStream(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.pointer = 0;
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        int length0 = this.inputStream.read(dst, offset, length);
        pointer += length0;
        return length0;
    }

    @Override
    public long size() throws IOException {
        return inputStream.available();
    }

    @Override
    public long tell() throws IOException {
        return pointer;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
