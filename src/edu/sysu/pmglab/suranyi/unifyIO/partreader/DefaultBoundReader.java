package edu.sysu.pmglab.suranyi.unifyIO.partreader;

import edu.sysu.pmglab.suranyi.container.Pair;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.IFileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.IOException;

/**
 * @author suranyi
 * @description 带有边界的文本读取方法
 */

class DefaultBoundReader extends IFileStream {
    /**
     * block 解压数据缓冲区
     */
    private boolean EOF = false;

    final Pair<Long, Long> pointer;
    final FileStream file;
    long seek;

    public DefaultBoundReader(String fileName, Pair<Long, Long> pointer) throws IOException {
        this.file = new FileStream(fileName, FileOptions.CHANNEL_READER);
        this.file.seek(pointer.key);
        this.pointer = pointer;
        this.seek = pointer.key;
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        // 真实写入长度
        if (!EOF) {
            int length0;
            if (this.seek + length > this.pointer.value) {
                length0 = this.file.read(dst, offset, Math.max(0, (int) (this.pointer.value - this.seek)));
                this.EOF = true;
            } else {
                length0 = this.file.read(dst, offset, length);
            }
            this.seek += length0;
            return length0;
        } else {
            return -1;
        }
    }

    @Override
    public long size() throws IOException {
        return this.pointer.value - this.pointer.key;
    }

    @Override
    public long tell() throws IOException {
        return this.file.tell();
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }
}