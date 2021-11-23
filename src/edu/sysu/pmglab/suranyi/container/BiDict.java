package edu.sysu.pmglab.suranyi.container;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.exception.RuntimeExceptionOptions;

import java.util.HashMap;

/**
 * @author suranyi
 * @description 双向字典
 */

public class BiDict<K, V> {
    private final HashMap<K, V> keys;
    private final HashMap<V, K> values;

    /**
     * 构造器方法
     * @param capacity 双向字典初始容量
     */
    public BiDict(int capacity) {
        this.keys = new HashMap<>(capacity);
        this.values = new HashMap<>(capacity);
    }

    /**
     * 构造器方法
     * @param keys 键字典
     * @param values 值字典
     * @throws UnsupportedOperationException 当双向字典不是一一对应时，该构造是无效的
     */
    public BiDict(HashMap<K, V> keys, HashMap<V, K> values) {
        Assert.that(keys.size() == values.size(), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values have different sizes");

        for (K key : keys.keySet()) {
            Assert.that(key.equals(values.get(keys.get(key))), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values are not bijective relationships");
        }

        this.keys = keys;
        this.values = values;
    }

    /**
     * 构造器方法
     * @param keys 键字典
     * @param values 值字典
     * @throws UnsupportedOperationException 当双向字典不是一一对应时，该构造是无效的
     */
    public BiDict(K[] keys, V[] values) {
        Assert.that(keys.length == values.length, RuntimeExceptionOptions.UnsupportedOperationException, "keys and values have different sizes");

        this.keys = new HashMap<>(keys.length);
        this.values = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            Assert.that(!this.keys.containsKey(keys[i]), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values are not bijective relationships");
            Assert.that(!this.values.containsKey(values[i]), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values are not bijective relationships");

            this.keys.put(keys[i], values[i]);
            this.values.put(values[i], keys[i]);
        }
    }

    /**
     * 构造器方法
     * @param keys 键字典
     * @throws UnsupportedOperationException 当双向字典不是一一对应时，该构造是无效的
     */
    public BiDict(HashMap<K, V> keys) {
        this.keys = keys;
        this.values = new HashMap<>(keys.size());
        for (K key : keys.keySet()) {
            this.values.put(keys.get(key), key);
        }

        Assert.that(keys.size() == this.values.size(), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values are not bijective relationships");
    }

    /**
     * 根据键获得值
     * @param key 键
     * @return 获取键 key 对应的值
     */
    public V valueOf(K key) {
        return keys.get(key);
    }

    /**
     * 根据值获得键
     * @param value 值
     * @return 获取值 value 对应的键
     */
    public K keyOf(V value) {
        return values.get(value);
    }

    /**
     * 根据键获得值，不存在时则使用默认值替代
     * @param key 键
     * @param defaultValue 不存在该键对应的值时，使用的返回值
     * @return 获取值 key 对应的值
     */
    public V valueOfOrDefault(K key, V defaultValue) {
        return keys.getOrDefault(key, defaultValue);
    }

    /**
     * 根据值获得键，不存在时则使用默认值替代
     * @param value 值
     * @param defaultValue 不存在该值对应的键时，使用的返回值
     * @return 获取值 value 对应的键
     */
    public K keyOfOrDefault(V value, K defaultValue) {
        return values.getOrDefault(value, defaultValue);
    }

    /**
     * 添加数据
     * @param key 添加的键
     * @param value 添加的值
     * @throws UnsupportedOperationException 容器中存在此键值对时，拒绝添加行为
     */
    public void put(K key, V value) {
        Assert.that(!this.keys.containsKey(key) && !this.values.containsKey(value), RuntimeExceptionOptions.UnsupportedOperationException, "keys and values are not bijective relationships");

        this.keys.put(key, value);
        this.values.put(value, key);
    }

    /**
     * 通过键移除数据
     * @param key 移除的键
     */
    public void removeKey(K key) {
        if (this.keys.containsKey(key)) {
            V value = keys.get(key);

            this.keys.remove(key);
            this.values.remove(value);
        }
    }

    /**
     * 通过值移除数据
     * @param value 移除的值
     */
    public void removeValue(V value) {
        if (values.containsKey(value)) {
            K key = values.get(value);

            this.keys.remove(key);
            this.values.remove(value);
        }
    }

    /**
     * 包含性测试
     * @param key 是否包含该键
     */
    public boolean containKey(K key) {
        return this.keys.containsKey(key);
    }

    /**
     * 包含性测试
     * @param value 是否包含该值
     */
    public boolean containValue(V value) {
        return this.values.containsKey(value);
    }

    /**
     * 清除双向字典数据
     */
    public void clear() {
        this.keys.clear();
        this.values.clear();
    }

    /**
     * 获取当前双向字典大小
     * @return 当前双向字典的大小
     */
    public int size() {
        return this.keys.size();
    }

    /**
     * 获取键字典
     */
    public HashMap<K, V> getKeys() {
        return this.keys;
    }

    /**
     * 获取值字典
     */
    public HashMap<V, K> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        return "BiDict{" +
                "keys:" + keys +
                ", values:" + values +
                '}';
    }

    /**
     * 静态生成器方法
     * @param keys 键数据列表
     * @return 以 keys 的数据作为键，其索引值作为值，构建双向字典
     */
    public static <T> BiDict<T, Integer> of(T[] keys) {
        HashMap<T, Integer> keysMap = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            keysMap.put(keys[i], i);
        }

        Assert.that(keys.length == keysMap.size(), RuntimeExceptionOptions.UnsupportedOperationException, "keys has duplicate values");
        return new BiDict<>(keysMap);
    }
}
