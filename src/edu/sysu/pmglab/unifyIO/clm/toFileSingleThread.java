package edu.sysu.pmglab.unifyIO.clm;

import edu.sysu.pmglab.container.VolumeByteStream;
import edu.sysu.pmglab.unifyIO.BGZIPWriterStream;
import edu.sysu.pmglab.unifyIO.FileStream;
import edu.sysu.pmglab.unifyIO.options.FileOptions;
import edu.sysu.pmglab.unifyIO.partwriter.BGZOutputParam;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author suranyi
 */
class toFileSingleThread implements To {
    final FileStream file;

    public toFileSingleThread(String outputFileName) throws IOException {
        this.file = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
    }

    public toFileSingleThread(String outputFileName, BGZOutputParam bgzOutputParam) throws IOException {
        if (bgzOutputParam.toBGZF) {
            this.file = new FileStream(new BGZIPWriterStream(outputFileName, bgzOutputParam));
        } else {
            this.file = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
        }
    }

    @Override
    public void write(byte element) throws IOException {
        this.file.write(element);
    }

    @Override
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void write(VolumeByteStream src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void write(int contextId, byte element) throws IOException {
        this.file.write(element);
    }

    @Override
    public void write(int contextId, byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void write(int contextId, VolumeByteStream src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    @Override
    public void flush(int contextId) {

    }

    @Override
    public <T> T flush(int contextId, Callable<T> callable) throws Exception {
        if (callable != null) {
            return callable.call();
        } else {
            return null;
        }
    }

    @Override
    public void flush(int contextId, Runnable runnable) throws Exception {
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }
}