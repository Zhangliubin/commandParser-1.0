package edu.sysu.pmglab.suranyi.container;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.Value;
import edu.sysu.pmglab.suranyi.check.exception.RuntimeExceptionOptions;
import edu.sysu.pmglab.suranyi.easytools.ArrayUtils;

import java.util.*;

/**
 * @author suranyi
 * @description 循环队列 (非常适用于经常从前面或者后面添加、删除数据的情形)
 */

public class SmartList<T> implements Collection<T>, RandomAccess {
    Object[] cache;
    int start = 0;
    int end = 0;
    boolean autoExpansion;

    final static int DEFAULT_SIZE = 16;

    /**
     * 创建一个循环列表
     */
    public SmartList() {
        this(DEFAULT_SIZE, true);
    }

    /**
     * 创建一个循环列表
     * @param autoExpansion 是否可自动扩容
     */
    public SmartList(boolean autoExpansion) {
        this(DEFAULT_SIZE, autoExpansion);
    }

    /**
     * 创建一个循环列表
     * @param size 缓冲区大小
     */
    public SmartList(int size) {
        this(size, false);
    }

    /**
     * 创建一个循环列表
     * @param size 缓冲区大小
     * @param autoExpansion 是否可自动扩容
     */
    public SmartList(int size, boolean autoExpansion) {
        Assert.valueRange(size, 0, Integer.MAX_VALUE - 2);
        this.cache = new Object[size];

        this.autoExpansion = autoExpansion;
    }

    /**
     * 包装基础数据类型
     * @param src 源数据
     */
    public SmartList(boolean[] src) {
        this((T[]) ArrayUtils.wrap(src));
    }

    /**
     * 包装基础数据类型
     * @param src 源数据
     */
    public SmartList(byte[] src) {
        this((T[]) ArrayUtils.wrap(src));
    }

    /**
     * 包装基础数据类型
     * @param src 源数据
     */
    public SmartList(short[] src) {
        this((T[]) ArrayUtils.wrap(src));
    }

    /**
     * 包装基础数据类型
     * @param src 源数据
     */
    public SmartList(int[] src) {
        this((T[]) ArrayUtils.wrap(src));
    }

    /**
     * 包装基础数据类型
     * @param src 源数据
     */
    public SmartList(long[] src) {
        this((T[]) ArrayUtils.wrap(src));
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     */
    public SmartList(T[] cache) {
        this(cache, 0, cache.length);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     * @param autoExpansion 是否可自动扩容
     */
    public SmartList(T[] cache, boolean autoExpansion) {
        this(cache, 0, cache.length, autoExpansion);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     * @param length 有效数据长度
     */
    public SmartList(T[] cache, int length) {
        this(cache, 0, length);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     * @param length 有效数据长度
     */
    public SmartList(T[] cache, int length, boolean autoExpansion) {
        this(cache, 0, length, autoExpansion);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     * @param offset 偏移量
     * @param length 有效数据长度
     */
    public SmartList(T[] cache, int offset, int length) {
        this(cache, offset, length, false);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     * @param offset 偏移量
     * @param length 有效数据长度
     */
    public SmartList(T[] cache, int offset, int length, boolean autoExpansion) {
        this.cache = Assert.NotNull(cache);
        Assert.that(offset >= 0 && length >= 0, RuntimeExceptionOptions.NegativeValueException, "offset < 0 or length < 0");

        this.start = offset;
        this.end = offset + length;

        Assert.that(this.start <= cache.length, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        Assert.that(this.end <= cache.length, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");

        this.autoExpansion = autoExpansion;
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     */
    public SmartList(Collection<T> cache) {
        this(cache, false);
    }

    /**
     * 创建一个循环列表
     * @param cache 将缓冲数据包装为循环列表
     */
    public SmartList(Collection<T> cache, boolean autoExpansion) {
        this.cache = Assert.NotNull(cache.toArray());
        this.start = 0;
        this.end = this.cache.length;
        this.autoExpansion = autoExpansion;
    }

    /**
     * 设置可自动扩容
     * @param autoExpansion 自动扩容
     */
    public SmartList<T> setAutoExpansion(boolean autoExpansion) {
        synchronized (this) {
            this.autoExpansion = autoExpansion;
        }
        return this;
    }

    /**
     * 获取当前有效数据数
     * @return 有效数据数
     */
    @Override
    public int size() {
        return this.end - this.start;
    }

    /**
     * 队列是否为空
     */
    @Override
    public boolean isEmpty() {
        return end == start;
    }

    /**
     * 获取总容量 (可扩容容器的容量为 2 GB - 2)
     * @return 缓冲区长度
     */
    public int getCapacity() {
        if (autoExpansion) {
            return Integer.MAX_VALUE - 2;
        } else {
            return this.cache.length;
        }
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) {
            for (int i = start; i < end; i++) {
                if (this.cache[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (element.equals(this.cache[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> elements) {
        for (Object e : elements) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从左往右查找数据
     * @param element 待查找的元素
     * @return -1 代表不存在，其余值代表其表内索引
     */
    public int indexOf(Object element) {
        if (element == null) {
            for (int i = start; i < end; i++) {
                if (this.cache[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (element.equals(this.cache[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 从右往左查找数据
     * @param element 待查找的元素
     * @return -1 代表不存在，其余值代表其表内索引
     */
    public int lastIndexOf(Object element) {
        if (element == null) {
            for (int i = end - 1; i >= start; i--) {
                if (this.cache[i] == null) {
                    return i - start;
                }
            }
        } else {
            for (int i = end - 1; i >= start; i--) {
                if (element.equals(this.cache[i])) {
                    return i - start;
                }
            }
        }

        return -1;
    }

    /**
     * 添加元素
     * @return 是否成功添加
     */
    public boolean addNull() {
        makeSureCapacity(1);

        this.cache[end++] = null;
        return true;
    }

    /**
     * 添加元素
     * @param element 添加元素
     * @return 是否成功添加
     */
    @Override
    public boolean add(T element) {
        makeSureCapacity(1);

        this.cache[end++] = element;
        return true;
    }

    /**
     * 添加元素
     * @param elements 添加元素
     */
    public boolean add(T[] elements) {
        return addAll(elements);
    }

    /**
     * 添加元素
     * @param elements 添加元素
     */
    public boolean add(T[] elements, int offset, int length) {
        return add(new SmartList<>(elements, offset, length));
    }

    /**
     * 添加元素
     */
    public boolean add(SmartList<T> otherQueue) {
        Assert.NotNull(otherQueue);
        int otherLength = otherQueue.size();

        if (otherLength > 0) {
            // 确保容量是充足的
            makeSureCapacity(otherLength);

            System.arraycopy(otherQueue.cache, otherQueue.start, this.cache, this.end, otherLength);
            this.end += otherLength;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        Assert.NotNull(elements);

        int otherLength = elements.size();

        if (otherLength > 0) {
            // 确保容量是充足的
            makeSureCapacity(otherLength);

            for (Object element : elements) {
                this.cache[this.end++] = element;
            }
        }

        return true;
    }

    /**
     * 添加元素
     */
    public void addAll(SmartList<T> otherQueue) {
        add(otherQueue);
    }

    /**
     * 添加元素
     * 将该数组包装为循环列表，并添加元素
     */
    public boolean addAll(T[] otherQueue) {
        return add(new SmartList<>(otherQueue));
    }

    /**
     * dropNull 的逻辑不太一样
     */
    public void dropNull() {
        int count = 0;

        for (int i = start; i < end; i++) {
            Object temp = this.cache[i];
            this.cache[i] = null;

            if (temp != null) {
                this.cache[count++] = temp;
            }
        }

        this.start = 0;
        this.end = count;
    }

    /**
     * 移除指定元素
     * @param element 指定元素
     * @return 是否成功执行了移除操作
     */
    @Override
    public boolean remove(Object element) {
        if (this.start == this.end) {
            // 没有元素可以删除
            return false;
        }

        if (element == null) {
            if (this.cache[start] == null) {
                start++;
                return true;
            } else if (this.cache[end - 1] == null) {
                end--;
                return true;
            } else {
                // 在中间
                for (int i = start + 1; i < end - 1; i++) {
                    if (this.cache[i] == null) {
                        System.arraycopy(this.cache, i + 1, this.cache, i, end - i - 1);
                        end--;
                        return true;
                    }
                }

                return false;
            }
        } else {
            if (element.equals(this.cache[start])) {
                this.cache[start++] = null;
                return true;
            } else if (element.equals(this.cache[end - 1])) {
                this.cache[--end] = null;
                return true;
            } else {
                // 在中间
                for (int i = start + 1; i < end - 1; i++) {
                    if (element.equals(this.cache[i])) {
                        System.arraycopy(this.cache, i + 1, this.cache, i, end - i - 1);
                        this.cache[--end] = null;
                        return true;
                    }
                }

                return false;
            }
        }
    }

    @Override
    public boolean removeAll(Collection<?> elements) {
        if (this.start == this.end || elements == null) {
            // 没有元素可以删除
            return false;
        }

        int count = 0;

        for (int i = start; i < end; i++) {
            Object temp = this.cache[i];
            this.cache[i] = null;

            if (!elements.contains(temp)) {
                this.cache[count++] = temp;
            }
        }

        this.start = 0;
        this.end = count;
        return true;
    }

    public boolean removeAll(T element) {
        if (element == null) {
            int beforeSize = size();
            dropNull();

            return size() != beforeSize;
        }

        if (this.start == this.end) {
            // 没有元素可以删除
            return false;
        }

        int count = 0;

        for (int i = start; i < end; i++) {
            Object temp = this.cache[i];
            this.cache[i] = null;

            if (!element.equals(temp)) {
                this.cache[count++] = temp;
            }
        }

        this.start = 0;
        this.end = count;
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> elements) {
        if (this.start == this.end || elements == null || elements.size() == 0) {
            // 没有元素可以删除
            return false;
        }

        int count = 0;

        for (int i = start; i < end; i++) {
            Object temp = this.cache[i];
            this.cache[i] = null;

            if (elements.contains(temp)) {
                this.cache[count++] = temp;
            }
        }

        this.start = 0;
        this.end = count;
        return true;
    }

    /**
     * 刷新缓冲区
     */
    public void flush() {
        if (start > 0) {
            // 前端有空闲数据，进行数据整理
            int length0 = size();
            System.arraycopy(this.cache, this.start, this.cache, 0, length0);

            this.start = 0;
            this.end = length0;
        }
    }

    /**
     * 弹出最开始的元素
     * @return 第一个元素
     */
    @SuppressWarnings("unchecked")
    public T popFirst() {
        Assert.that(!isEmpty(), RuntimeExceptionOptions.EmptyCollectionException, "empty queue");

        try {
            return (T) this.cache[this.start];
        } finally {
            this.cache[start] = null;
            this.start++;
        }
    }

    /**
     * 弹出最开始的元素
     * @param size 弹出的元素个数
     * @return 弹出最开始的 size 个元素
     */
    public T[] popFirst(int size) {
        try {
            return toArray(0, size);
        } finally {
            for (int i = this.start; i < this.start + size; i++) {
                this.cache[i] = null;
            }
            this.start += size;
        }
    }

    /**
     * 弹出最末尾的元素
     * @return 最后一个元素
     */
    @SuppressWarnings("unchecked")
    public T popLast() {
        Assert.that(!isEmpty(), RuntimeExceptionOptions.EmptyCollectionException, "empty queue");

        try {
            return (T) this.cache[this.end - 1];
        } finally {
            this.cache[--end] = null;
        }
    }

    /**
     * 弹出最末尾的元素
     * @param size  弹出的元素个数
     * @return 弹出最末尾的 size 个元素
     */
    public T[] popLast(int size) {
        try {
            return toArray(this.end - size, size);
        } finally {
            for (int i = this.end - 1; i >= this.end - size; i--) {
                this.cache[i] = null;
            }
            this.end -= size;
        }
    }

    /**
     * 获取指定索引值元素
     * @param index 索引
     * @return 该索引对应的元素值
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0) {
            index = size() + index;

            Assert.that(index >= 0, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        }

        Assert.that(this.start + index < this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        return (T) this.cache[this.start + index];
    }

    /**
     * 获取指定索引值元素
     * @param index 索引
     */
    public void set(int index, T newValue) {
        if (index < 0) {
            index = size() + index;

            Assert.that(index >= 0, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        }

        Assert.that(this.start + index < this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        this.cache[this.start + index] = newValue;
    }

    /**
     * 插入元素
     * @param index 插入位置索引
     * @param value 插入值
     */
    public void insert(int index, T value) {
        if (index < 0) {
            index = size() + index;

            Assert.that(index >= 0, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        }

        // 容量检查
        makeSureCapacity(1);

        if (index == 0) {
            if (this.start > 0) {
                this.cache[--this.start] = value;
            } else {
                int length0 = size();
                System.arraycopy(this.cache, this.start, this.cache, 1, length0);
                this.cache[0] = value;
                this.start = 0;
                this.end = length0 + 1;
            }
        } else if (this.start + index == this.end) {
            this.cache[this.end++] = value;
        } else if (this.start + index < this.end) {
            System.arraycopy(this.cache, this.start + index, this.cache, this.start + index + 1, size() - index);
            this.cache[this.start + index] = value;
            this.end++;
        } else {
            Assert.throwException(RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        }
    }

    /**
     * 获取指定索引值范围的元素
     * @param offset 偏移值
     * @param length 提取数据长度
     * @return 指定范围 (offset, offset + length) 的元素
     */
    @SuppressWarnings("unchecked")
    public SmartList<T> get(int offset, int length) {
        Assert.that(offset >= 0 && length >= 0, RuntimeExceptionOptions.NegativeValueException, "offset < 0 or length < 0");
        Assert.that(this.start + offset <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        Assert.that(this.start + offset + length <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");

        return new SmartList<>((T[]) ArrayUtils.copyOfRange(this.cache, this.start + offset, this.start + offset + length), autoExpansion);
    }

    /**
     * 获取指定索引值范围的子列表 （区别在于它是关联 cache 的，主 cache 被改变，子 cache 也一样被改变）
     * @param offset 偏移值
     * @param length 提取数据长度
     * @return 指定范围 (offset, offset + length) 的元素
     */
    @SuppressWarnings("unchecked")
    public SmartList<T> subList(int offset, int length) {
        Assert.that(offset >= 0 && length >= 0, RuntimeExceptionOptions.NegativeValueException, "offset < 0 or length < 0");
        Assert.that(this.start + offset <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        Assert.that(this.start + offset + length <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");

        return new SmartList<>((T[]) this.cache, this.start + offset, this.end + offset + length, autoExpansion);
    }

    /**
     * 转为数组
     */
    @SuppressWarnings("unchecked")
    @Override
    public T[] toArray() {
        if ((start == 0) && (end == this.cache.length)) {
            return (T[]) cache;
        } else {
            return (T[]) ArrayUtils.copyOfRange(cache, start, end);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K> K[] toArray(K[] target) {
        if (target == null || target.length < size()) {
            return (K[]) Arrays.copyOfRange(this.cache, start, end, target.getClass());
        } else {
            // 有效长度取决于 size()
            System.arraycopy(this.cache, start, target, 0, size());
            return target;
        }
    }

    public boolean[] toBooleanArray() {
        boolean[] booleanArray = new boolean[size()];
        for (int i = this.start; i < this.end; i++) {
            booleanArray[i] = (Boolean) this.cache[i];
        }
        return booleanArray;
    }

    public byte[] toByteArray() {
        byte[] byteArray = new byte[size()];
        for (int i = this.start; i < this.end; i++) {
            byteArray[i] = (Byte) this.cache[i];
        }
        return byteArray;
    }

    public short[] toShortArray() {
        short[] shortArray = new short[size()];
        for (int i = this.start; i < this.end; i++) {
            shortArray[i] = (Short) this.cache[i];
        }
        return shortArray;
    }

    public String[] toStringArray() {
        String[] stringArray = new String[size()];
        for (int i = this.start; i < this.end; i++) {
            stringArray[i] = (String) this.cache[i];
        }
        return stringArray;
    }

    public int[] toIntegerArray() {
        int[] intArray = new int[size()];
        for (int i = this.start; i < this.end; i++) {
            intArray[i] = (Integer) this.cache[i];
        }
        return intArray;
    }

    public long[] toLongArray() {
        long[] longArray = new long[size()];
        for (int i = this.start; i < this.end; i++) {
            longArray[i] = (Long) this.cache[i];
        }
        return longArray;
    }

    public double[] toDoubleArray() {
        double[] doubleArray = new double[size()];
        for (int i = this.start; i < this.end; i++) {
            doubleArray[i] = (Double) this.cache[i];
        }
        return doubleArray;
    }

    public float[] toFloatArray() {
        float[] floatArray = new float[size()];
        for (int i = this.start; i < this.end; i++) {
            floatArray[i] = (Float) this.cache[i];
        }
        return floatArray;
    }

    /**
     * 转为数组
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(int offset, int length) {
        Assert.that(offset >= 0 && length >= 0, RuntimeExceptionOptions.NegativeValueException, "offset < 0 or length < 0");
        Assert.that(this.start + offset <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        Assert.that(this.start + offset + length <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");

        return (T[]) ArrayUtils.copyOfRange(this.cache, this.start + offset, this.start + offset + length);
    }

    /**
     * 清除所有数据
     */
    @Override
    public void clear() {
        Arrays.fill(cache, null);
        this.start = 0;
        this.end = 0;
    }

    /**
     * 关闭队列
     */
    public void close() {
        clear();
        this.cache = null;
    }

    /**
     * 排序方法
     */
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> comparator) {
        Assert.NotNull(comparator);
        Arrays.sort((T[]) this.cache, this.start, this.end, comparator);
    }

    /**
     * 排序方法
     */
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> comparator, int offset, int length) {
        Assert.NotNull(comparator);
        Assert.that(offset >= 0 && length >= 0, RuntimeExceptionOptions.NegativeValueException, "offset < 0 or length < 0");
        Assert.that(this.start + offset <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");
        Assert.that(this.start + offset + length <= this.end, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "index out of bounds");

        Arrays.sort((T[]) this.cache, this.start + offset, this.start + offset + length, comparator);
    }

    /**
     * 确保容量充足
     * @param requestSize 需求的大小
     */
    private void makeSureCapacity(int requestSize) {
        // 真实的需求大小
        int expanSize = size() + requestSize - cache.length;

        if (expanSize <= 0) {
            if (requestSize + this.end > cache.length) {
                flush();
            }
        } else {
            Assert.that(autoExpansion, RuntimeExceptionOptions.ArrayIndexOutOfBoundsException, "add elements to a full queue that does not support expansion");

            long newSize = expanSize + this.cache.length;

            // 新尺寸必须是正数
            Assert.valueRange(newSize, 0, Integer.MAX_VALUE - 2);

            Object[] newCache;
            if (newSize < DEFAULT_SIZE) {
                newSize = DEFAULT_SIZE;
            } else if (newSize <= 134217728) {
                // 128 MB 以下翻倍扩容
                newSize = newSize << 1;
            } else {
                // 128 MB 以上 1.5 倍扩容
                newSize = Value.of(newSize + (newSize >> 1), 0, Integer.MAX_VALUE - 2);
            }

            newCache = new Object[(int) newSize];
            System.arraycopy(this.cache, this.start, newCache, 0, size());
            this.cache = newCache;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int seek = 0;
            int length = end - start;

            @Override
            public boolean hasNext() {
                return seek < length;
            }

            @Override
            public T next() {
                try {
                    return (T) cache[start + seek];
                } finally {
                    seek++;
                }
            }
        };
    }

    @Override
    public String toString() {
        if (this.cache == null) {
            return "null";
        }

        int iMax = size() - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < iMax + 1; i++) {
            b.append(this.cache[start + i]);
            if (i == iMax) {
                b.append(']');
            } else {
                b.append(", ");
            }
        }
        return b.toString();
    }

    /**
     * 输出至多 maxNums 个元素
     * @param maxNums 至多输出的元素个数
     */
    public String toString(int maxNums) {
        if (this.cache == null) {
            return "null";
        }

        Assert.valueRange(maxNums, 0, Integer.MAX_VALUE - 2);

        int cacheSize = size();
        if (cacheSize == 0) {
            return "[]";
        }

        if (maxNums == 0) {
            return "[...]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');

        if (maxNums >= cacheSize) {
            for (int i = 0; i < cacheSize; i++) {
                b.append(this.cache[start + i]);

                if (i == cacheSize - 1) {
                    b.append(']');
                } else {
                    b.append(", ");
                }
            }
        } else {
            for (int i = 0; i < cacheSize; i++) {
                b.append(this.cache[start + i]);

                if (i + 1 == maxNums) {
                    b.append(", ...]");
                    return b.toString();
                }

                b.append(", ");
            }
        }

        return b.toString();
    }

    /**
     * 转为常用数据类型 —— ArrayList
     */
    public ArrayList<T> toArrayList() {
        return new ArrayList<>(this);
    }

    /**
     * 转为常用数据类型 —— HashSet
     */
    public Set<T> toHashSet() {
        return new HashSet<>(this);
    }

    /**
     * 元素去重
     */
    public void dropDuplicated() {
        if (this.start == this.end) {
            // 没有元素可以删除
            return;
        }

        Set<Object> sets = new HashSet<>();
        int count = 0;

        for (int i = start; i < end; i++) {
            Object temp = this.cache[i];
            this.cache[i] = null;

            if (!sets.contains(temp)) {
                this.cache[count++] = temp;
                sets.add(temp);
            }
        }

        this.start = 0;
        this.end = count;
    }
}