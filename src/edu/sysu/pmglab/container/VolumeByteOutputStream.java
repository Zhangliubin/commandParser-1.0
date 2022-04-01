package edu.sysu.pmglab.container;

import java.io.OutputStream;

/**
 * @author suranyi
 * @description 包装字节数组为 OutputStream，方便操作数据缓冲区，并且减少内存使用量
 */

public class VolumeByteOutputStream extends OutputStream {
    private byte[] cache;
    private int seek;

    /**
     * 构造器方法
     */
    public VolumeByteOutputStream() {
    }

    public VolumeByteOutputStream(int capacity) {
        this.cache = new byte[capacity];
    }


    public VolumeByteOutputStream(byte[] cache) {
        wrap(cache, 0);
    }

    public VolumeByteOutputStream(byte[] cache, int seek) {
        wrap(cache, seek);
    }

    /**
     * 写入数据，不进行容量检查
     * @param element 写入的 byte 数据
     */
    public void write(byte element) {
        this.cache[seek++] = element;
    }

    /**
     * 写入数据，进行容量检查
     * @param element 写入的 byte 数据
     */
    @Override
    public void write(int element) {
        write((byte) element);
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     */
    @Override
    public void write(byte[] src) {
        write(src, 0, src.length);
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     */
    @Override
    public void write(byte[] src, int offset, int length) {
        write0(src, offset, length);
    }

    /**
     * 不安全写入数据核心方法，offset、length 必须是合法参数
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     * @return length
     */
    private int write0(byte[] src, int offset, int length) {
        System.arraycopy(src, offset, this.cache, this.seek, length);
        this.seek += length;
        return length;
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
     * 获取定容容器的总容量, -1 代表当前容器不可写入
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

    /**
     * 关闭文件
     */
    @Override
    public void close() {
        this.cache = null;
        this.seek = -1;
    }
}
