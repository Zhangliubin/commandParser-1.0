package edu.sysu.pmglab.unifyIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;

import static java.nio.file.StandardOpenOption.*;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :管道写入
 */

class ChannelWriterStream extends ChannelReaderStream {
    ChannelWriterStream(String fileName, OpenOption... options) throws IOException {
        super(fileName, options);
    }

    ChannelWriterStream(String fileName) throws IOException {
        super(fileName, READ, WRITE, CREATE, TRUNCATE_EXISTING);
    }

    @Override
    public void write(byte bite) throws IOException {
        write(new byte[]{bite});
    }

    @Override
    public void write(byte[] src) throws IOException {
        this.file.write(ByteBuffer.wrap(src));
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(ByteBuffer.wrap(src, offset, length));
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        this.file.write(buffer);
    }
}
