package edu.sysu.pmglab.suranyi.container;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.Value;

import java.io.OutputStream;

/**
 * @author suranyi
 * @description 包装字节数组为 OutputStream，方便操作数据缓冲区，并且减少内存使用量
 */

public class VolumeByteSafeOutputStream extends OutputStream {
    private byte[] cache;
    private int seek;

    /**
     * 构造器方法
     */
    public VolumeByteSafeOutputStream() {
    }

    public VolumeByteSafeOutputStream(int capacity) {
        this.cache = new byte[capacity];
    }

    /**
     * 写入数据，进行容量检查
     * @param element 写入的 byte 数据
     */
    public void write(byte element) {
        checkCapacity(1);
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
        checkCapacity(length);

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
     * 检查是否需要扩容
     * @param writeSize 检查写入大小
     */
    private void checkCapacity(int writeSize) {
        expansion(writeSize - this.cache.length + seek);
    }

    /**
     * 内部扩容方法
     * @param requestSize 比对大小
     */
    private void expansion(int requestSize) {
        if (requestSize > 0) {
            long newSize = requestSize + this.seek;

            // 验证新尺寸
            Assert.valueRange(newSize, 0, Integer.MAX_VALUE - 2);

            byte[] newCache;
            if (newSize < 16) {
                newSize = 16;
            } else if (newSize <= (Integer.MAX_VALUE >> 4)) {
                // 128 MB 以下翻倍扩容
                newSize = newSize << 1;
            } else {
                // 128 MB 以上 1.5 倍扩容
                newSize = Value.of(newSize + (newSize >> 1), 0, Integer.MAX_VALUE - 2);
            }

            newCache = new byte[(int) newSize];
            System.arraycopy(this.cache, 0, newCache, 0, seek);
            this.cache = newCache;
        }
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
