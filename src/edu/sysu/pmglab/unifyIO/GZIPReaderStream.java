package edu.sysu.pmglab.unifyIO;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @Data        :2021/09/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :GZF 文件读入
 */

public class GZIPReaderStream extends IFileStream {
    final GZIPInputStream file;

    public GZIPReaderStream(final String fileName) throws IOException {
        file = new GZIPInputStream(new BufferedInputStream(new FileInputStream(fileName)), 8192);
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        return file.read(dst, offset, length);
    }

    @Override
    public long size() throws IOException {
        return file.available();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    @Override
    public boolean seekAvailable() {
        return false;
    }
}
