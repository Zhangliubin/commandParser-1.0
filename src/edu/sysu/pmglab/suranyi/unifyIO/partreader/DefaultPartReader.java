package edu.sysu.pmglab.suranyi.unifyIO.partreader;

import edu.sysu.pmglab.suranyi.check.Value;
import edu.sysu.pmglab.suranyi.container.Pair;
import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.easytools.ArrayUtils;
import edu.sysu.pmglab.suranyi.easytools.ByteCode;
import edu.sysu.pmglab.suranyi.easytools.FileUtils;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.IOException;

/**
 * @author suranyi
 * @description 文本分块读取器
 */

class DefaultPartReader implements IPartReader {
    final String fileName;
    final FileStream file;

    /**
     * 位置数据
     */
    Pair<Long, Long>[] blockPos;

    private boolean EOF = false;

    public DefaultPartReader(final String fileName) throws IOException {
        this.fileName = fileName;
        this.file = new FileStream(fileName, FileOptions.CHANNEL_READER);
    }

    @Override
    public FileStream[] part(int nThreads) throws IOException {
        // 防止指定的线程数量过多，导致内存过载
        nThreads = Value.of(nThreads, 1, 100);

        adjustBlockPos(nThreads);

        // 分块完成后，文件的 IO 权限交给子部分
        this.EOF = true;
        close();

        // 分块读取器个数等于块个数
        FileStream[] fileStreams = new FileStream[this.blockPos.length];

        // 初始化每个分块读取器
        for (int i = 0; i < this.blockPos.length; i++) {
            fileStreams[i] = new FileStream(new DefaultBoundReader(this.fileName, this.blockPos[i]));
        }

        return fileStreams;
    }

    public byte read() throws IOException {
        return this.file.read();
    }

    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    public int read(byte[] dst, int offset, int length) throws IOException {
        return this.file.read(dst, offset, length);
    }

    /**
     * 校正块大小
     */
    private void adjustBlockPos(int nThreads) throws IOException {
        long[] pos = new long[nThreads + 1];

        // 将剩余的数据进行分块
        long currentSeek = this.file.tell();
        long resSize = FileUtils.sizeOf(this.fileName) - currentSeek;
        for (int i = 0; i < nThreads; i++) {
            pos[i] = currentSeek + ((resSize / nThreads) * i);
        }
        pos[nThreads] = currentSeek + resSize;

        // 如果原始数据非常小 (设定为 10 MB)，则不使用并行模式
        if (resSize < 1024 * 1024 * 10) {
            this.blockPos = new Pair[]{new Pair(pos[0], pos[pos.length - 1])};
            return;
        }

        byte[] cache = new byte[8192];
        int length;

        // 如果 blockPos 没有在上面被设置，则此处进行设置
        this.blockPos = new Pair[nThreads];
        for (int i = 0; i < nThreads; i++) {
            this.blockPos[i] = new Pair<>(pos[i], pos[i + 1]);
        }

        // 校正每个 block 的起点、终点位置，最后一个不需要校正
        finish:
        for (int blockIndex = 0; blockIndex < this.blockPos.length - 1; blockIndex++) {
            seek(this.blockPos[blockIndex + 1].key);

            // 读取一个 cache 进来
            out:
            while (true) {
                if ((length = read(cache)) != -1) {
                    for (int i = 0; i < length; i++) {
                        if (cache[i] == ByteCode.NEWLINE) {
                            this.blockPos[blockIndex] = new Pair(this.blockPos[blockIndex].key, this.file.tell() - (length - i));
                            this.blockPos[blockIndex + 1] = new Pair(this.file.tell() - (length - i) + 1, Math.max(this.blockPos[blockIndex + 1].value, this.file.tell() - (length - i) + 1));
                            break out;
                        }
                    }
                } else {
                    // 后续所有数据都是一行，此时终止
                    Pair<Long, Long>[] newBlockPos = ArrayUtils.copyOfRange(this.blockPos, 0, blockIndex + 1);
                    newBlockPos[blockIndex] = new Pair<>(this.blockPos[blockIndex].key, this.blockPos[this.blockPos.length - 1].value);
                    this.blockPos = newBlockPos;
                    break finish;
                }
            }
        }

        // 将所有长度为 0 的 block 移除
        for (int i = 0; i < this.blockPos.length; i++) {
            if (this.blockPos[i].key >= this.blockPos[i].value) {
                this.blockPos[i] = null;
            }
        }

        this.blockPos = dropNull(this.blockPos);
    }

    @Override
    public int readLine(VolumeByteStream dst) throws IOException {
        return this.file.readLine(dst);
    }

    public void close() throws IOException {
        this.file.close();
        this.EOF = true;
    }

    public void seek(long pos) throws IOException {
        this.file.seek(pos);
    }

    /**
     * 删除非空指针
     */
    Pair<Long, Long>[] dropNull(Pair<Long, Long>[] objects) {
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
}
