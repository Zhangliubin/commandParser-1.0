package edu.sysu.pmglab.unifyIO.partreader;

import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.check.Value;
import edu.sysu.pmglab.container.Pair;
import edu.sysu.pmglab.container.VolumeByteStream;
import edu.sysu.pmglab.easytools.ArrayUtils;
import edu.sysu.pmglab.easytools.ByteCode;
import edu.sysu.pmglab.easytools.FileUtils;
import edu.sysu.pmglab.unifyIO.BlockGunzipper;
import edu.sysu.pmglab.unifyIO.FileStream;
import edu.sysu.pmglab.unifyIO.options.FileOptions;

import java.io.IOException;

/**
 * @author suranyi
 * @description bgzip 分块读取器
 */

class BGZIPPartReader implements IPartReader {
    final String fileName;
    final FileStream file;
    final BlockGunzipper gunzipper = new BlockGunzipper();

    /**
     * 位置数据
     */
    Pair<Long, Long>[] blockPos;
    Pair<Integer, Integer>[] linePos;

    /**
     * block 解压数据缓冲区
     */
    public final byte[] uncompressedBlock = new byte[BGZFStreamConstants.DEFAULT_UNCOMPRESSED_BLOCK_SIZE];
    int uncompressedLength = 0;
    int uncompressedSeek = 0;
    int compressedLength = 0;
    private boolean EOF = false;

    public BGZIPPartReader(final String fileName) throws IOException {
        this.fileName = fileName;
        this.file = new FileStream(fileName, FileOptions.CHANNEL_READER);
    }

    @Override
    public FileStream[] part(int nThreads) throws IOException {
        nThreads = Value.of(nThreads, 1, 100);
        adjustBlockPos(nThreads);

        // 分块完成后，文件的 IO 权限交给子部分
        this.EOF = true;
        close();

        // 分块读取器个数等于块个数
        FileStream[] fileStreams = new FileStream[this.blockPos.length];

        // 初始化每个分块读取器
        for (int i = 0; i < this.blockPos.length; i++) {
            fileStreams[i] = new FileStream(new BGZIPBoundReader(this.fileName, this.blockPos[i], this.linePos[i]));
        }
        return fileStreams;
    }

    public byte read() throws IOException {
        if (uncompressedLength == uncompressedSeek) {
            if (this.EOF) {
                // 到达文件结尾
                throw new IOException("Pointer out of file size.");
            }
        }

        return uncompressedBlock[uncompressedSeek++];
    }

    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

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

    public int read(VolumeByteStream dst, int length) throws IOException {
        int totalByteWrite = 0;
        while (length > 0) {
            if (uncompressedLength == uncompressedSeek) {
                load();
                if (this.EOF) {
                    return totalByteWrite == 0 ? -1 : totalByteWrite;
                }
            }

            int byteToWrite = Math.min(length, uncompressedLength - uncompressedSeek);
            System.arraycopy(this.uncompressedBlock, this.uncompressedSeek, dst.getCache(), dst.size(), byteToWrite);
            this.uncompressedSeek += byteToWrite;
            length -= byteToWrite;
            dst.reset(dst.size() + byteToWrite);
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
                uncompressedLength = -1;
            }
        }
    }

    /**
     * 校正块大小
     */
    private void adjustBlockPos(int nThreads) throws IOException {
        long currentSeek = this.uncompressedSeek == this.uncompressedLength ? this.file.tell() : this.file.tell() - this.compressedLength;
        long resSize = FileUtils.sizeOf(this.fileName) - currentSeek;

        long[] pos = new long[nThreads + 1];

        // 将剩余的数据进行分块
        for (int i = 0; i < nThreads; i++) {
            pos[i] = currentSeek + ((resSize / nThreads) * i);
        }
        pos[nThreads] = currentSeek + resSize;

        // 如果原始数据非常小 (设定为 1 MB)，则不使用并行模式
        if (resSize < 1024 * 1024) {
            this.blockPos = new Pair[]{new Pair(pos[0], pos[pos.length - 1])};
            this.linePos = new Pair[]{new Pair(this.uncompressedSeek == this.uncompressedLength ? 0 : this.uncompressedSeek, -1)};
            return;
        }

        // 否则，进行文件指针的调整
        FileStream fs = new FileStream(this.fileName, FileOptions.CHANNEL_READER);

        // 读进最大块大小 + 4，一定会有一个块的头信息被包含进来
        byte[] buffer = new byte[BGZFStreamConstants.MAX_COMPRESSED_BLOCK_SIZE + 4];

        out:
        for (int i = 1; i < nThreads; i++) {
            fs.seek(pos[i]);

            int length = fs.read(buffer);
            for (int index = 0; index < length - 4; index++) {
                if (buffer[index] == 31 && buffer[index + 1] == -117 && buffer[index + 2] == 8 && buffer[index + 3] == 4 && buffer[index + 4] == 0) {
                    pos[i] += index;
                    continue out;
                }
            }

            // 如果从这里出去，说明该文件可能为 gzip 格式，而不是 bgzip，此时无法使用并行模式
            this.blockPos = new Pair[]{new Pair(pos[0], pos[pos.length - 1])};
            this.linePos = new Pair[]{new Pair(this.uncompressedSeek == this.uncompressedLength ? 0 : this.uncompressedSeek, -1)};
            fs.close();
            return;
        }

        fs.close();

        // 如果 blockPos 没有在上面被设置，则此处进行设置，此处对位置数据进行去重，避免有重叠的块
        pos = ArrayUtils.dropDuplicated(pos);

        this.blockPos = new Pair[pos.length - 1];
        for (int i = 0; i < this.blockPos.length; i++) {
            this.blockPos[i] = new Pair<>(pos[i], pos[i + 1]);
        }

        // 调整行位置信息
        this.linePos = new Pair[this.blockPos.length];
        this.linePos[0] = new Pair<>(this.uncompressedSeek == this.uncompressedLength ? 0 : this.uncompressedSeek, -1);

        int length;

        // 校正每个 block 的起点、终点位置
        finish:
        for (int blockIndex = 0; blockIndex < this.linePos.length - 1; blockIndex++) {
            seek(this.blockPos[blockIndex + 1].key);

            // 读取一个 cache 进来
            out:
            while (true) {
                load();
                length = this.uncompressedLength;
                if (length != -1) {
                    for (int i = 0; i < length; i++) {
                        if (this.uncompressedBlock[i] == ByteCode.NEWLINE) {
                            this.blockPos[blockIndex] = new Pair(this.blockPos[blockIndex].key, this.file.tell());
                            this.blockPos[blockIndex + 1] = new Pair(this.file.tell() - this.compressedLength, this.blockPos[blockIndex + 1].value);

                            this.linePos[blockIndex] = new Pair(this.linePos[blockIndex].key, i);
                            this.linePos[blockIndex + 1] = new Pair(i + 1, -1);
                            break out;
                        }
                    }

                } else {
                    // 后续所有数据都是一行，此时终止
                    Pair<Long, Long>[] newBlockPos = ArrayUtils.copyOfRange(this.blockPos, 0, blockIndex + 1);
                    newBlockPos[blockIndex] = new Pair<>(this.blockPos[blockIndex].key, this.blockPos[this.blockPos.length - 1].value);
                    this.blockPos = newBlockPos;
                    this.linePos = ArrayUtils.copyOfRange(this.linePos, 0, blockIndex + 1);
                    this.linePos[blockIndex] = new Pair<>(this.linePos[blockIndex].key, -1);
                    break finish;
                }
            }
        }

        // 将所有重复的 block 移除
        Pair<Long, Long> lastBlockPos = this.blockPos[this.blockPos.length - 1];
        for (int i = this.blockPos.length - 2; i >= 0; i--) {
            if (this.blockPos[i].key.equals(lastBlockPos.key)) {
                this.blockPos[i] = null;
                this.linePos[i] = null;
            } else {
                lastBlockPos = this.blockPos[i];
            }
        }
        this.blockPos = dropBlockPosNull(this.blockPos);
        this.linePos = dropLinePosNull(this.linePos);
    }

    Pair<Long, Long>[] dropBlockPosNull(Pair<Long, Long>[] objects) {
        int notNullNum = 0;
        for (Pair<Long, Long> object : objects) {
            if (object != null) {
                notNullNum += 1;
            }
        }

        int index = 0;
        Pair<Long, Long>[] newObjects = new Pair[notNullNum];
        for (Pair<Long, Long> object : objects) {
            if (object != null) {
                newObjects[index++] = object;
            }
        }
        return newObjects;
    }

    Pair<Integer, Integer>[] dropLinePosNull(Pair<Integer, Integer>[] objects) {
        int notNullNum = 0;
        for (Pair<Integer, Integer> object : objects) {
            if (object != null) {
                notNullNum += 1;
            }
        }

        int index = 0;
        Pair<Integer, Integer>[] newObjects = new Pair[notNullNum];
        for (Pair<Integer, Integer> object : objects) {
            if (object != null) {
                newObjects[index++] = object;
            }
        }
        return newObjects;
    }

    @Override
    public int readLine(VolumeByteStream dst) throws IOException {
        if (this.uncompressedSeek == this.uncompressedLength) {
            load();
            if (this.EOF) {
                // 没有写入任何数据
                return -1;
            }
        }

        for (int i = this.uncompressedSeek; i < this.uncompressedLength; i++) {
            if (this.uncompressedBlock[i] == ByteCode.NEWLINE) {
                int length0;

                // FIXME: 检查此处的逻辑
                if ((i > 0) && (this.uncompressedBlock[i - 1] == ByteCode.CARRIAGE_RETURN)) {
                    length0 = i - this.uncompressedSeek - 1;
                } else {
                    length0 = i - this.uncompressedSeek;
                }

                dst.writeSafety(this.uncompressedBlock, this.uncompressedSeek, length0);
                try {
                    return length0;
                } finally {
                    this.uncompressedSeek = i + 1;
                }
            }
        }

        // 没有找到，则继续下一次搜索
        int length0 = this.uncompressedLength - this.uncompressedSeek;
        dst.writeSafety(this.uncompressedBlock, this.uncompressedSeek, length0);
        this.uncompressedSeek = this.uncompressedLength;

        return length0 + readLine(dst);
    }

    public void close() throws IOException {
        gunzipper.close();
        this.EOF = true;
    }

    public Pair<Long, Integer> tell() throws IOException {
        return new Pair<>(this.file.tell() - this.compressedLength, this.uncompressedSeek);
    }

    public void seek(long pos) throws IOException {
        this.file.seek(pos);
        this.uncompressedSeek = this.uncompressedLength;
    }
}
