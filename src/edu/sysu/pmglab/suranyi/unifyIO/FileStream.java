package edu.sysu.pmglab.suranyi.unifyIO;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.ioexception.IOExceptionOptions;
import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.easytools.ArrayUtils;
import edu.sysu.pmglab.suranyi.easytools.ByteCode;
import edu.sysu.pmglab.suranyi.easytools.FileUtils;
import edu.sysu.pmglab.suranyi.easytools.ValueUtils;
import edu.sysu.pmglab.suranyi.unifyIO.clm.PBGZIPWriterStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions.*;

/**
 * @author suranyi
 * @description 统一 IO 架构方法
 */

public class FileStream implements AutoCloseable, Closeable {
    /**
     * 读取文件类型，支持扩展设计
     */

    private IFileStream file;
    private FileOptions mode;
    private String fileName;
    private int seek = 0;
    private int length = 0;
    private byte[] cache;

    // 分隔符及是否切割末尾字符
    private boolean separatorTrim = false;

    // 文件状态
    private boolean closed;

    // 行缓冲区，不建议使用，仅用于向下兼容
    private VolumeByteStream lineCache;

    /**
     * 构造器方法
     * @param fileName 文件名
     */
    public FileStream(String fileName) throws IOException {
        this(fileName, DEFAULT_READER);
    }

    /**
     * 构造器方法
     */
    public FileStream(IFileStream file) throws IOException {
        this.file = file;
        this.mode = IFILESTREAM_ACCEPT;
        this.fileName = "build-in";

        if (mode.isNeedCache()) {
            this.cache = new byte[2 << 16];
        }
    }

    /**
     * 构造器方法
     * @param fileName 文件名
     * @param mode 打开模式，文件夹只能用 NO_OPEN 打开
     */
    public FileStream(String fileName, FileOptions mode) throws IOException {
        this.mode = mode;
        this.fileName = fileName;
        this.file = openFile(fileName, mode);

        if (mode.isNeedCache()) {
            this.cache = new byte[2 << 16];
        }
    }

    /**
     * 构造器方法
     * @param fileStream 文件流
     */
    public static FileStream of(IFileStream fileStream) throws IOException {
        return new FileStream(fileStream);
    }

    /**
     * 构造器方法
     * @param fileName 文件名
     */
    public static FileStream of(String fileName) throws IOException {
        return new FileStream(fileName);
    }

    /**
     * 构造器方法
     * @param fileName 文件名
     * @param mode 打开模式，文件夹只能用 NO_OPEN 打开
     */
    public static FileStream of(String fileName, FileOptions mode) throws IOException {
        return new FileStream(fileName, mode);
    }

    /**
     * 打开文件
     * @param fileName 文件名
     * @param mode 文件模式
     */
    private IFileStream openFile(String fileName, FileOptions mode) throws IOException {
        try {
            switch (mode) {
                case DEFAULT_READER: {
                    return new DefaultReaderStream(fileName);
                }
                case DEFAULT_APPEND: {
                    return new DefaultAppendStream(fileName);
                }
                case DEFAULT_WRITER: {
                    return new DefaultWriterStream(fileName);
                }
                case GZIP_READER: {
                    return new GZIPReaderStream(fileName);
                }
                case GZIP_WRITER: {
                    return new GZIPWriterStream(fileName);
                }
                case BGZIP_READER: {
                    return new BGZIPReaderStream(fileName);
                }
                case BGZIP_WRITER: {
                    return new BGZIPWriterStream(fileName);
                }
                case CHANNEL_READER: {
                    return new ChannelReaderStream(fileName);
                }
                case CHANNEL_WRITER: {
                    return new ChannelWriterStream(fileName);
                }
                case CHANNEL_APPEND: {
                    return new ChannelAppendStream(fileName);
                }
                default: {
                    for (int i = 1; i < getParallelBgzipWriters().length; i++) {
                        if (mode.equals(getParallelBgzipWriter(i + 1))) {
                            return new PBGZIPWriterStream(fileName, i + 1);
                        }
                    }
                    throw new IOException("Invalid mode: " + mode);
                }
            }
        } finally {
            this.closed = false;
        }
    }

    /**
     * 重设缓冲区
     */
    public void initCache(int size) {
        byte[] newCache = new byte[size];
        if (this.length > 0) {
            System.arraycopy(cache, seek, newCache, 0, length - seek);
        }
        this.cache = newCache;
        this.length = length - seek;
        this.seek = 0;
    }

    /**
     * 填充缓冲区
     * - 1. length = 0, seek = 0, 说明无数据，一般位于最开始
     * - 2. length = -1, 已经到达文件最末尾，不再加载缓冲数据
     */
    private int fillCache() throws IOException {
        // 长度不为 -1 时，请求填充缓冲区
        if (this.length != -1) {
            this.length = this.file.read(this.cache);
            this.seek = 0;
        }

        return this.length;
    }

    /**
     * 读取一个字节
     */
    public byte read() throws IOException {
        // 缓冲区已经读取完毕，加载下一个缓冲区
        if (this.length == this.seek) {
            fillCache();
        }

        // 已经无法读取数据，报错
        Assert.that(this.length != -1, IOExceptionOptions.IOException, "pointer out of file size");

        // 否则，从缓冲区中调出数据，并移动指针
        return this.cache[this.seek++];
    }

    /**
     * 读取 n 个字节
     * @param n 一次性读取的字节数
     * @return <= n 个字节长度的数组，无数据读取则返回 null
     */
    public byte[] read(int n) throws IOException {
        byte[] out = new byte[n];

        int length0 = read(out);

        if (length0 < n) {
            if (length0 >= 0) {
                return ArrayUtils.copyOfRange(out, 0, length0);
            } else {
                return null;
            }
        } else {
            return out;
        }
    }

    /**
     * 填充目标数组
     * @param dst 目标数组
     * @return 填充字节数
     */
    public int read(byte[] dst) throws IOException {
        return read(dst, 0, dst.length);
    }

    /**
     * 填充目标数组
     * @param dst 目标数组
     * @param offset 目标数组偏移量
     * @param length 目标数组写入长度
     * @return 填充字节数
     */
    public int read(byte[] dst, int offset, int length) throws IOException {
        if (this.seek == this.length) {
            // 缓冲区中无数据
            return this.file.read(dst, offset, length);
        } else {
            // 缓冲区中有数据，优先拷贝缓冲区数据
            if ((this.length - this.seek) >= length) {
                System.arraycopy(this.cache, this.seek, dst, offset, length);
                this.seek += length;
                return length;
            } else {
                int length0 = this.length - this.seek;
                System.arraycopy(this.cache, this.seek, dst, offset, length0);
                this.seek = this.length;
                return length0 + this.file.read(dst, offset + length0, length - length0);
            }
        }
    }

    /**
     * 读取长度为 length 的数据，并写入 dst 中
     * @param dst 目标数组
     * @param length 目标数组写入长度
     */
    public int read(VolumeByteStream dst, int length) throws IOException {
        int length0 = read(dst.getCache(), dst.size(), length);
        dst.reset(dst.size() + length0);
        return length0;
    }

    /**
     * 读取 1byte 并转为 byte
     */
    public int readByteValue() throws IOException {
        return read() & 0xFF;
    }

    /**
     * 读取 2 byte 并转为 short 类型数据
     */
    public int readShort() throws IOException {
        byte[] value = read(2);

        return ValueUtils.byteArray2ShortValue(value);
    }

    /**
     * 读取 4 byte 并转为整数
     */
    public int readIntegerValue() throws IOException {
        byte[] value = read(4);

        return ValueUtils.byteArray2IntegerValue(value);
    }

    /**
     * 写入 1 字节
     * @param element 写入的元素
     */
    public void write(byte element) throws IOException {
        this.file.write(element);
    }

    /**
     * 将源数据 src 的数据写入文件
     * @param src 源数据
     */
    public void write(byte[] src) throws IOException {
        this.file.write(src);
    }

    /**
     * 将源数据 ByteBuffer 的数据写入文件。若使用 Buffer.allocate 创建缓冲区，并使用 put 添加数据，则需要先使用 buffer.flip() 重置
     * @param buffer 源数据
     */
    public void write(ByteBuffer buffer) throws IOException {
        this.file.write(buffer);
    }

    /**
     * 将源数据 src 的数据，从 offset 开始的 length 长度数据写入文件
     * @param src 源数据
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(byte[] src, int offset, int length) throws IOException {
        this.file.write(src, offset, length);
    }

    /**
     * 将字符串形式的源数据转为字节数组，并写入文件
     * @param src 源数据，字符串形式
     */
    public void write(String src) throws IOException {
        this.file.write(src.getBytes());
    }

    /**
     * 将源数据定容数组写入文件
     * @param src 源数据，定容数组
     */
    public void write(VolumeByteStream src) throws IOException {
        this.file.write(src.getCache(), 0, src.size());
    }

    /**
     * 将源数据定容数组从 offset 开始 连续的 length 长度写入文件
     * @param src 源数据，定容数组
     * @param offset 偏移量
     * @param length 写入的长度
     */
    public void write(VolumeByteStream src, int offset, int length) throws IOException {
        this.file.write(src.getCache(), offset, length);
    }

    /**
     * 将本文件从 position 开始的 count 字节写入另一个文件
     * @param position 开始写入的指针
     * @param count 写入的长度
     * @param channelWriter 另一个文件
     */
    public void writeTo(long position, long count, FileChannel channelWriter) throws IOException {
        this.file.writeTo(position, count, channelWriter);
    }

    /**
     * 写入一个 short 变量
     */
    public void writeShortValue(short value) throws IOException {
        this.file.write(ValueUtils.shortValue2ByteArray(value));
    }

    /**
     * 写入一个 int 变量
     */
    public void writeIntegerValue(int value) throws IOException {
        this.file.write(ValueUtils.intValue2ByteArray(value));
    }

    /**
     * 设定文件指针
     * @param pos 新文件指针的位置
     */
    public void seek(long pos) throws IOException {
        // 记录当前指针
        this.file.seek(pos);
        this.length = 0;
        this.seek = 0;
    }

    /**
     * 返回当前文件指针的位置
     */
    public long tell() throws IOException {
        // 发生了按行读取时，需要进行校准
        long position = this.file.tell();
        if (this.length == -1) {
            return position;
        } else {
            return position - this.length + this.seek;
        }
    }

    /**
     * 计算文件大小，文件夹重定向到 FileUtils 使用 sizeOf 计算
     */
    public long size() throws IOException {
        // 如果该文件路径本身是真实存在的
        if (this.mode.isPathOpera()) {
            return FileUtils.sizeOf(this.fileName);
        } else {
            return this.file.size();
        }
    }

    /**
     * 获取文件名
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * 获取文件管道
     */
    public FileChannel getChannel() {
        return this.file.getChannel();
    }

    /**
     * 按行读取，该方法不建议使用。频繁操作数据时会有内存溢出风险
     */
    public byte[] readLine() throws IOException {
        if (lineCache == null) {
            lineCache = new VolumeByteStream(2 << 16);
        }

        if (readLine(lineCache) != -1) {
            return lineCache.takeOut();
        } else {
            return null;
        }
    }

    /**
     * 按行读取，该方法不建议使用。频繁操作数据时会有内存溢出风险
     */
    public String readLineToString() throws IOException {
        byte[] line = readLine();

        if (line == null) {
            return null;
        } else {
            return new String(line);
        }
    }

    /**
     * 按行读取
     * @param dst 写入缓冲区
     * @return 写入的数据长度
     */
    public int readLine(VolumeByteStream dst) throws IOException {
        int length0;

        if (this.seek < this.length) {
            for (int i = this.seek; i < this.length; i++) {
                if (this.cache[i] == ByteCode.NEWLINE) {
                    // 遇到 \n 时，调整指针，并输出结果
                    length0 = i - this.seek;

                    try {
                        if ((length0 >= 1) && (this.cache[i - 1] == ByteCode.CARRIAGE_RETURN)) {
                            length0 -= 1;
                        }

                        dst.writeSafety(this.cache, this.seek, length0);
                        return length0;
                    } finally {
                        this.seek = i + 1;
                    }
                }
            }

            // 该 buffer 结尾仍然没有遇到 \n，则保存当前结果，并进行下一次搜索
            dst.writeSafety(this.cache, this.seek, this.length - this.seek);
        }

        // seek 达到末尾时，重新填充缓冲区
        length0 = this.length - this.seek;
        if (this.fillCache() != -1) {
            // 此时肯定还有数据，不至于变成 -1
            int byteToWrite = readLine(dst);

            if ((byteToWrite == 0) && (length0 > 0) && dst.endWith(ByteCode.CARRIAGE_RETURN)) {
                dst.reset(dst.size() - 1);
                length0--;
            }

            if (length0 == 0) {
                return byteToWrite;
            } else {
                if (byteToWrite <= 0) {
                    // -1 或 0
                    return length0;
                } else {
                    return length0 + byteToWrite;
                }
            }
        } else {
            // -1 表示没有数据可以继续写入了
            return length0 == 0 ? -1 : length0;
        }
    }

    /**
     * readAll 读取全部数据
     */
    public byte[] readAll() throws IOException {
        if (this.mode != BGZIP_READER) {
            long resSize = size() - tell();
            Assert.that(resSize <= Integer.MAX_VALUE - 2, IOExceptionOptions.IOException, "out of memory");
            return read((int) resSize);
        } else {
            VolumeByteStream out = new VolumeByteStream();
            byte[] buffer;
            while ((buffer = read(8192)) != null) {
                out.writeSafety(buffer);
            }
            return out.values();
        }
    }

    /**
     * 关闭文件
     */
    @Override
    public void close() throws IOException {
        Assert.that(!this.closed, IOExceptionOptions.IOException, "current file is closed");

        this.file.close();
        this.file = null;
        this.cache = null;
        this.seek = 0;
        this.length = 0;

        if (lineCache != null) {
            this.lineCache.close();
            this.lineCache = null;
        }

        this.closed = true;
    }

    /**
     * 重新打开文件
     */
    public void reOpen() throws IOException {
        reOpen(this.mode);
    }

    /**
     * 重新打开文件
     * @param mode 按照指定的新模式
     */
    public void reOpen(FileOptions mode) throws IOException {
        Assert.that(this.closed, IOExceptionOptions.IOException, "current file is not closed");
        Assert.that(mode.isSupportReopen(), IOExceptionOptions.IOException, "current filestream doesn't supported reopen");

        if (this.mode.isNeedCache() && this.cache == null) {
            this.cache = new byte[2 << 16];
        }

        this.file = openFile(this.fileName, mode);
        this.mode = mode;
        this.seek = 0;
        this.length = 0;

        this.closed = false;
    }

    /**
     * 查看文件是否被关闭
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * 刷新缓存
     */
    public void flush() throws IOException {
        this.file.flush();
    }

    /**
     * 删除文件，需要先关闭文件资源
     */
    public boolean delete() throws IOException {
        if (this.mode.isPathOpera()) {
            if (!this.closed) {
                close();
            }
            return FileUtils.delete(this.fileName);
        } else {
            return false;
        }
    }

    /**
     * 修改文件名，会导致本文件被关闭
     * @param newName 目标文件名
     */
    public boolean rename(String newName) throws IOException {
        if (this.mode.isPathOpera()) {
            // 关闭文件资源
            if (!this.closed) {
                close();
            }

            // 修改文件名
            File fileOld = new File(this.fileName);
            File fileNew = new File(newName);

            // 修改本类指定的文件名
            this.fileName = newName;
            return fileOld.renameTo(fileNew);
        } else {
            return false;
        }
    }

    /**
     * 文件是否可以进行指针跳转, 取决于 filestream 本身是否实现
     */
    public boolean seekAvailable() {
        return file.seekAvailable();
    }

    /**
     * 判断文件是否为文件夹，文件夹只能使用打开 NO_OPEN 模式打开
     */
    public boolean isDir() {
        if (this.mode.isPathOpera()) {
            return FileUtils.isDirectory(this.fileName);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "FileStream{" +
                "file=" + this.fileName +
                ", mode=" + this.mode +
                ", closed=" + this.closed +
                '}';
    }
}