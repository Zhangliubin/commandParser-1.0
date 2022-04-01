package edu.sysu.pmglab.unifyIO.partwriter;

import edu.sysu.pmglab.container.VolumeByteStream;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * @Data        :2021/05/31
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :
 */

public class DirectBlockWriter implements IBlockWriter<ByteBuffer>, AutoCloseable, Closeable {
    ByteBuffer cache;

    DirectBlockWriter(BGZOutputParam outputParam, int cacheSize) {
        this.cache = ByteBuffer.allocateDirect(cacheSize);
    }

    @Override
    public void write(byte[] bytes) {
        this.cache.put(bytes);
    }

    @Override
    public void write(byte code) {
        this.cache.put(code);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        this.cache.put(bytes, offset, length);
    }

    @Override
    public void write(VolumeByteStream byteStream) {
        this.cache.put(byteStream.getCache(), 0, byteStream.size());
    }

    @Override
    public void finish() {
        this.cache.flip();
    }

    @Override
    public void start() {
        this.cache.clear();
    }

    @Override
    public ByteBuffer getCache() {
        return this.cache;
    }

    @Override
    public int capacity() {
        return this.cache.capacity();
    }

    @Override
    public int remaining() {
        return this.cache.remaining();
    }

    @Override
    public void close() {
        this.cache.clear();
        this.cache = null;
    }
}