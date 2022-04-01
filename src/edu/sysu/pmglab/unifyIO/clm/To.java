package edu.sysu.pmglab.unifyIO.clm;

import edu.sysu.pmglab.container.VolumeByteStream;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author suranyi
 */
interface To {
    /**
     * 抽象方法
     */

    void write(byte element) throws IOException;

    void write(byte[] src, int offset, int length) throws IOException;

    void write(VolumeByteStream src, int offset, int length) throws IOException;

    void write(int contextId, byte element) throws IOException;

    void write(int contextId, byte[] src, int offset, int length) throws IOException;

    void write(int contextId, VolumeByteStream src, int offset, int length) throws IOException;

    void flush(int contextId) throws InterruptedException, IOException;

    <T> T flush(int contextId, Callable<T> callable) throws Exception;

    void flush(int contextId, Runnable callable) throws Exception;

    void close() throws IOException;

    default void startWriting() {
    }
}