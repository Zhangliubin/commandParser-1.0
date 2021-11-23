package edu.sysu.pmglab.suranyi.unifyIO.partreader;

import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.suranyi.container.Pair;
import edu.sysu.pmglab.suranyi.unifyIO.BlockGunzipper;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.IFileStream;

import java.io.IOException;

/**
 * @author suranyi
 * @description 带有边界的 BGZIP 读取方法
 */

class BGZIPBoundReader extends IFileStream {
    /**
     * block 解压数据缓冲区
     */
    public final byte[] uncompressedBlock = new byte[BGZFStreamConstants.DEFAULT_UNCOMPRESSED_BLOCK_SIZE];
    int uncompressedLength = 0;
    int uncompressedSeek = 0;
    int compressedLength = 0;
    private boolean EOF = false;

    final Pair<Long, Long> pointer;
    final Pair<Integer, Integer> linePointer;
    final BlockGunzipper gunzipper = new BlockGunzipper();
    final FileStream file;
    long seek;

    public BGZIPBoundReader(String fileName, Pair<Long, Long> pointer, Pair<Integer, Integer> linePointer) throws IOException {
        this.file = new FileStream(fileName, FileOptions.CHANNEL_READER);
        this.file.seek(pointer.key);
        this.pointer = pointer;
        this.linePointer = linePointer;
        this.seek = pointer.key;

        // 载入第一个 block
        load();
        this.uncompressedSeek += this.linePointer.key;
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    @Override
    public int read(byte[] dst, int offset, int length) throws IOException {
        int totalByteWrite = 0;
        while (length > 0) {
            if (uncompressedLength == uncompressedSeek) {
                load();
                if (this.EOF) {
                    return totalByteWrite == 0 ? -1 : totalByteWrite;
                }
            }

            int byteToWrite = Math.min(length, uncompressedLength - uncompressedSeek);
            System.arraycopy(this.uncompressedBlock, this.uncompressedSeek, dst, offset, byteToWrite);
            this.uncompressedSeek += byteToWrite;
            length -= byteToWrite;
            offset += byteToWrite;
            totalByteWrite += byteToWrite;
        }

        return totalByteWrite;
    }

    @Override
    public long size() throws IOException {
        return this.pointer.value - this.pointer.key;
    }

    /**
     * 载入下一个 block
     */
    private void load() throws IOException {
        if (!this.EOF) {
            // 解压一个 block
            uncompressedLength = gunzipper.unzipBlock(uncompressedBlock, file);
            compressedLength = gunzipper.compressedBlock.size();
            uncompressedSeek = 0;

            seek += compressedLength;

            // 达到最后一个 block
            if (this.seek >= this.pointer.value) {
                if (this.linePointer.value != -1) {
                    // 为 -1 表示不设定限制
                    uncompressedLength = this.linePointer.value;
                }
                this.EOF = true;
            }

            if (uncompressedLength == 0) {
                this.EOF = true;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.file.close();
        this.gunzipper.close();
    }
}