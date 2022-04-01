package edu.sysu.pmglab.unifyIO.partwriter;

import edu.sysu.pmglab.container.VolumeByteStream;

import java.io.Closeable;

/**
 * @author suranyi
 */

public class VolumeByteStreamDirectBlockWriter implements IBlockWriter<VolumeByteStream>, AutoCloseable, Closeable {
    VolumeByteStream cache;

    VolumeByteStreamDirectBlockWriter(BGZOutputParam outputParam, int cacheSize) {
        this.cache = new VolumeByteStream(cacheSize);
    }

    @Override
    public void write(byte[] bytes) {
        this.cache.writeSafety(bytes);
    }

    @Override
    public void write(byte code) {
        this.cache.writeSafety(code);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        this.cache.writeSafety(bytes, offset, length);
    }

    @Override
    public void write(VolumeByteStream byteStream) {
        this.cache.writeSafety(byteStream.getCache(), 0, byteStream.size());
    }

    @Override
    public void finish() {
    }

    @Override
    public void start() {
        this.cache.reset();
    }

    @Override
    public VolumeByteStream getCache() {
        return this.cache;
    }

    @Override
    public int capacity() {
        return this.cache.getCapacity();
    }

    @Override
    public int remaining() {
        return this.cache.remaining();
    }

    @Override
    public void close() {
        this.cache.reset();
        this.cache = null;
    }
}