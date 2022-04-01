package edu.sysu.pmglab.unifyIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.READ;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :管道读取
 */

class ChannelReaderStream extends IFileStream {
    protected FileChannel file;

    ChannelReaderStream(String fileName, OpenOption... options) throws IOException {
        this.file = FileChannel.open(Paths.get(fileName), options);
    }

    ChannelReaderStream(String fileName) throws IOException {
        this(fileName, READ);
    }

    @Override
    public void writeTo(long position, long count, FileChannel channelWriter) throws IOException {
        this.file.transferTo(position, count, channelWriter);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(dst, offset, length);
        return this.file.read(buf);
    }

    @Override
    public int read(byte[] dst) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(dst);
        return this.file.read(buf);
    }

    @Override
    public long size() throws IOException {
        return this.file.size();
    }

    @Override
    public long tell() throws IOException {
        return this.file.position();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.file.position(pos);
    }

    @Override
    public FileChannel getChannel() {
        return this.file;
    }

    @Override
    public void close() throws IOException {
        this.file.close();
        this.file = null;
    }

    @Override
    public boolean seekAvailable() {
        return true;
    }
}
