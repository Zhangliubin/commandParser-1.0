package edu.sysu.pmglab.container;

import edu.sysu.pmglab.easytools.ValueUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author suranyi
 * @description 包装字节数组为 InputStream，方便操作数据缓冲区，并且减少内存使用量
 */

public class VolumeByteInputStream extends InputStream {
    private byte[] cache;
    private int seek;

    /**
     * 构造器方法
     */
    public VolumeByteInputStream() {
    }

    public VolumeByteInputStream(byte[] cache) {
        wrap(cache, 0);
    }

    public VolumeByteInputStream(byte[] cache, int seek) {
        wrap(cache, seek);
    }

    @Override
    public int read() throws IOException {
        return this.cache[this.seek++] & 0xFF;
    }

    public byte[] read(int n) throws IOException {
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            out[i] = this.cache[this.seek++];
        }
        return out;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int byteToWrite = Math.min(len, remaining());
        System.arraycopy(this.cache, this.seek, b, off, byteToWrite);
        this.seek += byteToWrite;
        return byteToWrite;
    }

    public byte readByte() {
        return this.cache[this.seek++];
    }

    public short readShortValue() {
        return ValueUtils.byteArray2ShortValue(readByte(), readByte());
    }

    public int readInteger() {
        return ValueUtils.byteArray2IntegerValue(readByte(), readByte(), readByte(), readByte());
    }

    /**
     * 获取缓冲区段数据
     * @return 获取本定容容器缓冲区数据
     */
    public byte[] getCache() {
        return this.cache;
    }

    /**
     * 以另一个数组的数据替换本类数据
     * @param src 源数据
     */
    public void wrap(byte[] src) {
        this.cache = src;
        this.seek = (src == null) ? -1 : src.length;
    }

    /**
     * 以另一个数组的数据替换本类数据
     * @param src 源数据
     * @param length 该源数据有效数据长度
     */
    public void wrap(byte[] src, int length) {
        this.cache = src;
        this.seek = Math.min(length, src.length);
    }

    /**
     * 获取定容容器的总容量, -1 代表当前容器不可读取
     * @return 当前容器的容量
     */
    public int getCapacity() {
        if (null == this.cache) {
            return -1;
        }
        return this.cache.length;
    }

    /**
     * 当前容器有效数据长度
     * @return 有效数据长度，可以通过 reset 修改
     */
    public int size() {
        return this.seek;
    }

    /**
     * 当前容器剩余容量
     * @return 剩余容量
     */
    public int remaining() {
        return this.cache.length - this.seek;
    }

    /**
     * 重设容器内部指针为 0
     */
    @Override
    public void reset() {
        this.seek = 0;
    }

    /**
     * 重设文件指针
     * @param position 新指针
     */
    public void reset(int position) {
        this.seek = position;
    }

    @Override
    public long skip(long n) throws IOException {
        return this.seek + n;
    }

    @Override
    public int available() {
        return this.cache.length;
    }

    /**
     * 关闭文件
     */
    @Override
    public void close() {
        this.cache = null;
        this.seek = -1;
    }
}
