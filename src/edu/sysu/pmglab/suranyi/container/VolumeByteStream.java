package edu.sysu.pmglab.suranyi.container;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.Value;
import edu.sysu.pmglab.suranyi.easytools.ArrayUtils;
import edu.sysu.pmglab.suranyi.easytools.Kmp;
import edu.sysu.pmglab.suranyi.easytools.ValueUtils;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author suranyi
 * @description 定容字节数组
 */

public class VolumeByteStream implements Cloneable, Iterable<Byte>, AutoCloseable, Closeable {
    private byte[] cache;
    private int seek;

    final static int DEFAULT_SIZE = 16;

    /**
     * 构造器方法
     */
    public VolumeByteStream() {
        this.cache = new byte[DEFAULT_SIZE];
    }

    /**
     * 构造器方法
     * @param capacity 初始容器大小
     */
    public VolumeByteStream(int capacity) {
        this.cache = new byte[capacity];
    }

    /**
     * 构造器方法，将源数据包装为定容字节数据
     * @param src 源数据
     */
    public VolumeByteStream(byte[] src) {
        wrap(src);
    }

    /**
     * 构造器方法，将源数据包装为定容字节数据
     * @param src 源数据
     * @param length 源数据有效数据长度
     */
    public VolumeByteStream(byte[] src, int length) {
        wrap(src, length);
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param element 写入的数据
     * @return 1
     */
    public int write(byte element) {
        this.cache[this.seek++] = element;
        return 1;
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param element 写入的数据
     * @return 1
     */
    public int write(int element) {
        return write((byte) element);
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int write(byte[] src) {
        return write(src, 0, src.length);
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int write(String src) {
        return write(src.getBytes());
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     * @return length
     */
    public int write(byte[] src, int offset, int length) {
        return write0(src, offset, length);
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int write(VolumeByteStream src) {
        return write(src.getCache(), 0, src.size());
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     * @return length
     */
    public int write(VolumeByteStream src, int offset, int length) {
        return write(src.getCache(), offset, length);
    }

    /**
     * [不安全] 写入数据，不进行容量检查
     * @param file 从文件中读取数据，并写入到缓冲区
     * @param length 读取数据的长度
     * @return length
     */
    public int write(FileStream file, int length) {
        try {
            return this.seek += file.read(this.cache, this.seek, length);
        } catch (IOException e) {
            throw new UnsupportedOperationException("IOException from " + file + ": " + e.getMessage());
        }
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
     * 写入一个布尔变量
     * @param value 写入的布尔变量
     * @return 1
     */
    public int writeBooleanValue(boolean value) {
        return write(value ? 1 : 0);
    }

    /**
     * 写入一个字节数据
     * @param value 写入的 byte 数据
     * @return 1
     */
    public int writeByteValue(byte value) {
        return write(value);
    }

    /**
     * 写入一个 short 数值
     * @param value 写入的 short 数据
     * @return 2
     */
    public int writeShortValue(short value) {
        return write(ValueUtils.shortValue2ByteArray(value));
    }

    /**
     * 写入一个 int 数值
     * @param value 写入的 int 数据
     * @return 4
     */
    public int writeIntegerValue(int value) {
        return write(ValueUtils.intValue2ByteArray(value));
    }

    /**
     * 写入数据，进行容量检查
     * @param element 写入的 byte 数据
     * @return 1
     */
    public int writeSafety(byte element) {
        checkCapacity(1);
        return write(element);
    }

    /**
     * 写入数据，进行容量检查
     * @param element 写入的 byte 数据
     * @return 1
     */
    public int writeSafety(int element) {
        return writeSafety((byte) element);
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int writeSafety(byte[] src) {
        return writeSafety(src, 0, src.length);
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int writeSafety(String src) {
        return writeSafety(src.getBytes());
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     * @return length
     */
    public int writeSafety(byte[] src, int offset, int length) {
        return writeSafety0(src, offset, length);
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @return 源数据的长度
     */
    public int writeSafety(VolumeByteStream src) {
        return writeSafety(src.getCache(), 0, src.size());
    }

    /**
     * 写入数据，进行容量检查
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据的长度
     * @return length
     */
    public int writeSafety(VolumeByteStream src, int offset, int length) {
        return writeSafety(src.getCache(), offset, length);
    }

    /**
     * 安全地写入数据，offset、length 必须是合法参数
     * @param src 源数据
     * @param offset 源数据偏移量
     * @param length 源数据拷贝长度
     * @return length
     */
    private int writeSafety0(byte[] src, int offset, int length) {
        checkCapacity(length);

        return write0(src, offset, length);
    }

    /**
     * 对外扩容方法
     * @param newSize 目标大小
     */
    public void expansionTo(int newSize) {
        Assert.that(newSize <= Integer.MAX_VALUE - 2);

        if (newSize > this.cache.length) {
            byte[] newCache = new byte[newSize];
            if (this.seek > 0) {
                System.arraycopy(this.cache, 0, newCache, 0, this.seek);
            }
            this.cache = newCache;
        }
    }

    /**
     * 确保容器
     */
    public void makeSureCapacity(int... sizes) {
        int maxSize = ValueUtils.max(sizes);
        Assert.valueRange(maxSize, 0, Integer.MAX_VALUE - 2);

        expansion(maxSize - remaining());
    }

    /**
     * 内部扩容方法
     * @param requestSize 比对大小
     */
    private void expansion(long requestSize) {
        if (requestSize > 0) {
            long newSize = requestSize + this.cache.length;

            // 验证新尺寸
            Assert.valueRange(newSize, 0, Integer.MAX_VALUE - 2);

            byte[] newCache;
            if (newSize < DEFAULT_SIZE) {
                newSize = DEFAULT_SIZE;
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
     * 读取数据，阅后即焚，SEEK = 0 返回 null，可复用容器
     * @return 返回读取到的数据
     */
    public byte[] takeOut() {
        if (this.seek > 0) {
            try {
                return ArrayUtils.copyOfRange(this.cache, 0, this.seek);
            } finally {
                reset(0);
            }
        } else {
            return new byte[]{};
        }
    }

    /**
     * 读取数据，阅后即焚，SEEK = 0 返回 null，可复用容器
     * @param offset 偏移量
     * @return 返回读取到的数据
     */
    public byte[] takeOut(int offset) {
        if (this.seek > 0) {
            try {
                return ArrayUtils.copyOfRange(this.cache, offset, this.seek);
            } finally {
                reset(0);
            }
        } else {
            return null;
        }
    }

    /**
     * 读取数据，阅后即焚，SEEK = 0 返回 null，可复用容器
     * @param offset 偏移量
     * @param length 数据长度
     * @return 返回读取到的数据
     */
    public byte[] takeOut(int offset, int length) {
        if (this.seek > 0) {
            try {
                return ArrayUtils.copyOfRange(this.cache, offset, offset + length);
            } finally {
                reset(0);
            }
        } else {
            return null;
        }
    }

    /**
     * 拷贝数据
     * @return 获取拷贝得到的数据
     */
    public byte[] copy() {
        if (this.seek > 0) {
            return ArrayUtils.copyOfRange(this.cache, 0, this.seek);
        } else {
            return new byte[]{};
        }
    }

    /**
     * 获取所有数据值，相当于 copy 方法
     * @return 获取拷贝得到的数据
     */
    public byte[] values() {
        return copy();
    }

    /**
     * 获取索引 index 对应的值
     * @return 获取索引对应的值
     */
    public byte valueOf(int index) {
        // index < 0 时解读为从后往前读
        if (index < 0) {
            index = this.seek + index;
        }

        if ((index >= 0) && (index < this.seek)) {
            return cacheOf(index);
        } else {
            // 没有正确返回时，报错
            throw new ArrayIndexOutOfBoundsException((-this.seek) + " <= index <= " + (this.seek - 1));
        }
    }

    /**
     * 获取 [start, end) 对应的值
     * @param start 起始位置
     * @param end 终点位置
     * @return [start, end] 对应的字节数组
     */
    public byte[] rangeOf(int start, int end) {
        // index < 0 时解读为从后往前读
        if (start < 0) {
            start = this.seek + start;
        }

        if (end < 0) {
            end = this.seek + end;
        }

        if ((start >= 0) && (end > start) && (end <= this.seek)) {
            return cacheOf(start, end);
        } else {
            // 否则返回空数组
            if ((end <= start) && (end >= 0) && (start <= this.seek)) {
                return new byte[]{};
            } else {
                // 没有正确返回时，报错
                throw new ArrayIndexOutOfBoundsException((-this.seek) + " <= start/end <= " + (this.seek));
            }
        }
    }

    /**
     * 将 cache 的数据写入到 dst 容器中
     * @param dst 目标容器
     * @return 有效写入长度
     */
    public int read(byte[] dst) {
        return read(dst, 0);
    }

    /**
     * 将 cache 的数据写入到 dst 容器中
     * @param dst 目标容器
     * @param offset 目标容器写入起点
     * @return 有效写入长度
     */
    public int read(byte[] dst, int offset) {
        return read(0, dst, offset, Math.min(dst.length, this.seek));
    }

    /**
     * 将 cache 的数据写入到 dst 容器中
     * @param dst 目标容器
     * @param offset 目标容器写入起点
     * @param length 写入数据的长度
     * @return 有效写入长度
     */
    public int read(byte[] dst, int offset, int length) {
        return read(0, dst, offset, length);
    }

    /**
     * 将 cache 的数据写入到 dst 容器中
     * @param from 从 cache 的 from 处开始读
     * @param length 读取的数据长度
     * @param dst 目标容器
     * @param offset 目标容器写入起点
     * @return 有效写入长度
     */
    public int read(int from, byte[] dst, int offset, int length) {
        System.arraycopy(this.cache, from, dst, offset, length);
        return length;
    }

    /**
     * 获取第一个 element 元素所在的 index
     * @param element 搜索的元素值
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte element) {
        return indexOf(element, 0, this.seek);
    }

    /**
     * 获取第一个子串所在的 index
     * @param pattern 搜索的子串
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte[] pattern) {
        return indexOf(pattern, 0, this.seek);
    }

    /**
     * 获取从 start 起第一个 element 元素所在的 index
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte element, int start) {
        return indexOf(element, start, this.seek);
    }

    /**
     * 获取从 start 起第一个子串所在的 index
     * @param pattern 搜索的子串
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte[] pattern, int start) {
        return indexOf(pattern, start, this.seek);
    }

    /**
     * 获取 [start, end) 范围的第一个 element 元素所在的 index
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param end 搜索终点
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte element, int start, int end) {
        for (int i = start; i < end; i++) {
            if (this.cache[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取 [start, end) 范围的第一个子串所在的 index
     * @param pattern 搜索的子串
     * @param start 搜索起点
     * @param end 搜索终点
     * @return 检索元素值所在的索引
     */
    public int indexOf(byte[] pattern, int start, int end) {
        return Kmp.indexOf(this.cache, start, end, pattern);
    }

    /**
     * 获取第 times 个 element 元素所在的 index (times >= 1)
     * @param element 搜索的元素值
     * @param times 搜索的次数
     * @return 检索元素值所在的索引
     */
    public int indexOfN(byte element, int times) {
        return indexOfN(element, 0, this.seek, times);
    }

    /**
     * 获取从 start 起获取第 times 个 element 元素所在的 index (times >= 1)
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param times 搜索的次数
     * @return 检索元素值所在的索引
     */
    public int indexOfN(byte element, int start, int times) {
        return indexOfN(element, start, this.seek, times);
    }

    /**
     * 获取从 offset 起获取第 times 个 element 元素所在的 index (times >= 1)
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param end 搜索终点
     * @param times 搜索的次数
     * @return 检索元素值所在的索引
     */
    public int indexOfN(byte element, int start, int end, int times) {
        for (int i = 0; (i < times) && (start != -1); i++) {
            start = indexOf(element, start + 1, end);
        }
        return start;
    }

    /**
     * 以 index 作为分隔符，获取第 index 个数据
     * @param separator 分隔符
     * @param index 索引值
     * @return 第 index 个数据
     */
    public byte[] getNBy(byte separator, int index) {
        return getNBy(separator, index, 0);
    }

    /**
     * 以 index 作为分隔符，从 offset 起获取第 index 个数据
     * @param separator 分隔符
     * @param index 索引值
     * @param offset 偏移量
     * @return 第 index 个数据
     */
    public byte[] getNBy(byte separator, int index, int offset) {
        int start;

        if (index < 0) {
            return new byte[]{};
        } else if (index == 0) {
            start = 0;
        } else {
            start = indexOfN(separator, offset, index) + 1;
            if (start == 0) {
                // 说明没有找到第 n 个分隔符
                return new byte[]{};
            }
        }

        int end = indexOf(separator, start);
        end = (end == -1) ? this.seek : end;
        return ArrayUtils.copyOfRange(this.cache, start, end);
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param prefix 前缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public boolean startWith(byte[] prefix) {
        return startWith(0, prefix);
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param prefix 前缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public boolean startWith(int offset, byte[] prefix) {
        // 如果 src 的长度比校验长度 extensions 还要小，那必定不可能是以它为开始的
        if (this.seek - offset < prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (this.cache[i + offset] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param prefix 前缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public boolean startWith(byte prefix) {
        // 如果 src 的长度比校验长度 extensions 还要小，那必定不可能是以它为开始的
        return ((this.seek > 0) && (this.cache[0] == prefix));
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param extension 后缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public boolean endWith(byte[] extension) {
        if (this.seek < extension.length) {
            return false;
        }

        int offset = this.seek - extension.length;
        for (int i = 0; i < extension.length; i++) {
            if (this.cache[offset + i] != extension[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param extension 后缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public boolean endWith(byte extension) {
        return (this.seek > 0) && this.cache[this.seek - 1] == extension;
    }

    /**
     * 相等元素/数组校验
     * @param src 源数据
     * @return 当前有效数据与 src 内容是否相同
     */
    public boolean equal(byte[] src) {
        return equal(0, this.seek, src, 0, src.length);
    }

    /**
     * 相等元素/数组校验
     * @param src 源数据
     * @return 当前有效数据与 src 内容是否相同
     */
    public boolean equal(VolumeByteStream src) {
        return equal(0, this.seek, src.getCache(), 0, src.size());
    }

    /**
     * 相等元素/数组校验
     * @param src 源数据
     * @param offset 源数据偏移量
     * @param length 源数据有效长度
     * @return 当前有效数据与 src 内容是否相同
     */
    public boolean equal(byte[] src, int offset, int length) {
        return equal(0, this.seek, src, offset, length);
    }

    /**
     * 相等元素/数组校验
     * @param cacheOffset 当前缓冲区偏移量
     * @param cacheLength 当前缓冲区有效数据长度
     * @param src 源数据
     * @param offset 源数据偏移量
     * @param length 源数据有效长度
     * @return 当前有效数据与 src 内容是否相同
     */
    public boolean equal(int cacheOffset, int cacheLength, byte[] src, int offset, int length) {
        if (cacheLength != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (this.cache[cacheOffset + i] != src[offset + i]) {
                return false;
            }
        }
        return true;
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
        this.seek = (src == null) ? -1 : Math.max(0, Math.min(length, src.length));
    }

    /**
     * 以另一个定容容器的数据替换本类数据
     * @param src 源数据
     */
    public void wrap(VolumeByteStream src) {
        Assert.NotNull(src);
        wrap(src.getCache(), src.size());
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
     * 赋予打印方法/转字符串方法
     */
    @Override
    public String toString() {
        return Arrays.toString(values());
    }

    /**
     * 转为字符串并按指定字符切割
     * @param regex 切割符
     */
    public String[] toStringArrayBy(String regex) {
        return toString().split(regex);
    }

    /**
     * 赋予克隆方法
     */
    @Override
    public VolumeByteStream clone() {
        return new VolumeByteStream(values());
    }

    /**
     * 检查是否需要扩容
     * @param writeSize 检查写入大小
     */
    private void checkCapacity(int writeSize) {
        expansion(writeSize - this.cache.length + seek);
    }

    /**
     * 赋予可迭代方法
     */
    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < seek;
            }

            @Override
            public Byte next() {
                return cache[index++];
            }
        };
    }

    /**
     * [不安去] 写入缓冲区
     * @param start 本容器写入数据的起点
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入数据长度
     * @return length
     */
    public int cacheWrite(int start, byte[] src, int offset, int length) {
        System.arraycopy(src, offset, this.cache, start, length);

        return length;
    }

    /**
     * [不安去] 写入缓冲区
     * @param index 本容器写入数据的起点
     * @param bite 写入的数据
     * @return 1
     */
    public int cacheWrite(int index, byte bite) {
        this.cache[index] = bite;

        return 1;
    }

    /**
     * [不安全] 读取单个字节，直接操作缓冲区，不安全访问
     * @param index 读取数据的索引
     * @return 索引值对应的缓冲区数据
     */
    public byte cacheOf(int index) {
        return this.cache[index];
    }

    /**
     * [不安全] 读取 [start, end) 的数据，不安全访问
     * @param start 缓冲数据起点
     * @param end 缓冲数据终点
     */
    public byte[] cacheOf(int start, int end) {
        return ArrayUtils.copyOfRange(this.cache, start, end);
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
