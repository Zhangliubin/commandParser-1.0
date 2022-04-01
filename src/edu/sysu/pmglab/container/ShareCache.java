package edu.sysu.pmglab.container;

import java.util.Iterator;

/**
 * @author suranyi
 * @description 多个定容字节数组组成的共享缓冲区
 */

public class ShareCache implements Iterable<VolumeByteStream> {
    Array<VolumeByteStream> caches;

    public ShareCache() {
        this.caches = new Array<>(true);
    }

    public ShareCache(VolumeByteStream... caches) {
        this.caches = new Array<>(caches);
    }

    /**
     * 获取缓冲区索引
     *
     * @param index 索引值
     */
    public VolumeByteStream getCache(int index) {
        return caches.get(index);
    }

    /**
     * 请求分配一个长度为 cacheSize 的缓冲区
     *
     * @param cacheSize 缓冲区大小
     */
    public VolumeByteStream alloc(int cacheSize) {
        VolumeByteStream cache = new VolumeByteStream(cacheSize);
        this.caches.add(cache);
        return cache;
    }

    /**
     * 释放储存空间
     */
    public void freeMemory() {
        for (VolumeByteStream vbs : caches) {
            vbs.close();
        }
        this.caches.close();
        this.caches = null;
    }

    /**
     * 缓冲区大小
     */
    public int size() {
        if (this.caches == null) {
            return -1;
        } else {
            return this.caches.size();
        }
    }

    /**
     * 该缓冲区是否可用
     */
    public boolean enable() {
        return this.caches != null;
    }

    @Override
    public Iterator<VolumeByteStream> iterator() {
        return this.caches.iterator();
    }
}