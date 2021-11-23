package edu.sysu.pmglab.suranyi.unifyIO;

import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.IOException;

/**
 * @Data        :2021/06/18
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :BGZF 文件读入
 */

public class BGZIPReaderStream extends IFileStream {
    final FileStream file;
    final BlockGunzipper gunzipper = new BlockGunzipper();

    /**
     * block 解压数据缓冲区
     */
    public final byte[] uncompressedBlock = new byte[BGZFStreamConstants.DEFAULT_UNCOMPRESSED_BLOCK_SIZE];
    int uncompressedLength = 0;
    int uncompressedSeek = 0;
    int compressedLength = 0;
    private boolean EOF = false;

    public BGZIPReaderStream(final String fileName) throws IOException {
        this.file = new FileStream(fileName, FileOptions.CHANNEL_READER);
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


    /**
     * 载入下一个 block
     */
    private void load() throws IOException {
        if (!this.EOF) {
            // 解压一个 block
            uncompressedLength = gunzipper.unzipBlock(uncompressedBlock, file);
            compressedLength = gunzipper.compressedBlock.size();
            uncompressedSeek = 0;

            if (uncompressedLength == 0) {
                this.EOF = true;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.gunzipper.close();
        this.file.close();
        this.EOF = true;
    }

    @Override
    public void seek(long pos) throws IOException {
        this.file.seek(pos);
        this.EOF = false;
        this.uncompressedSeek = this.uncompressedLength;
    }
}
