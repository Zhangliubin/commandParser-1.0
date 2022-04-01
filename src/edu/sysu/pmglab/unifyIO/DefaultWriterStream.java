package edu.sysu.pmglab.unifyIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :默认文件写入
 */

class DefaultWriterStream extends DefaultReaderStream {
    DefaultWriterStream() {
    }

    DefaultWriterStream(String fileName) throws IOException {
        // 删除已经存在的文件
        Files.deleteIfExists(Paths.get(fileName));
        this.file = new RandomAccessFile(fileName, "rw");
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
    public void write(ByteBuffer buffer) throws IOException {
        if (!buffer.isDirect()) {
            this.file.write(buffer.array(), buffer.position(), buffer.limit());
        } else {
            byte[] temp = new byte[8192];
            int numBytes = buffer.remaining();
            while (numBytes > 0) {
                int bytesToWrite = Math.min(numBytes, 8192);
                buffer.get(temp, 0, bytesToWrite);
                numBytes -= bytesToWrite;
                this.file.write(temp);
            }
        }
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public boolean seekAvailable() {
        return false;
    }
}
