package edu.sysu.pmglab.suranyi.container;

/**
 * @author suranyi
 * @description K, V 成对包装对象
 */

public class Pair<K, V> {
    public final K key;
    public final V value;

    public Pair(K key, V value){
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
