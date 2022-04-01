package edu.sysu.pmglab.unifyIO.partwriter;

import edu.sysu.pmglab.container.VolumeByteStream;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * @author suranyi
 */

public class BGZIPBlockWriter implements IBlockWriter<ByteBuffer>, AutoCloseable, Closeable {
    /**
     * 压缩器、校验和函数
     */
    private final Deflater deflater;
    private final Deflater noCompressionDeflater = new Deflater(0, true);
    private final CRC32 crc32 = new CRC32();

    /**
     * 未压缩、压缩缓冲区
     */
    private final byte[] uncompressedBuffer = new byte[65498];
    private final byte[] compressedBuffer = new byte[65518];
    private int numUncompressedBytes = 0;

    /**
     * 常量
     */
    public static final byte[] MARGIN_CODE = new byte[]{31, (byte) 139, 8, 4, 0, 0, 0, 0, 0, (byte) 255, 6, 0, 66, 67, 2, 0};
    public static final byte[] EMPTY_GZIP_BLOCK = new byte[]{31, -117, 8, 4, 0, 0, 0, 0, 0, -1, 6, 0, 66, 67, 2, 0, 27, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * 字节缓冲区
     */
    byte[] crcValue = new byte[4];
    byte[] uncompressedSize = new byte[4];

    /**
     * 输出缓冲区
     */
    ByteBuffer cache;

    BGZIPBlockWriter(BGZOutputParam outputParam, int cacheSize) {
        this.deflater = new Deflater(outputParam.level, true);
        this.cache = ByteBuffer.allocateDirect(cacheSize);
    }

    @Override
    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte code) {
        uncompressedBuffer[numUncompressedBytes++] = code;

        if (numUncompressedBytes == uncompressedBuffer.length) {
            deflateBlock();
        }
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        while (length != 0) {
            final int bytesToWrite = Math.min(uncompressedBuffer.length - numUncompressedBytes, length);
            System.arraycopy(bytes, offset, uncompressedBuffer, numUncompressedBytes, bytesToWrite);
            numUncompressedBytes += bytesToWrite;
            offset += bytesToWrite;
            length -= bytesToWrite;
            if (numUncompressedBytes == uncompressedBuffer.length) {
                deflateBlock();
            }
        }
    }

    @Override
    public void write(VolumeByteStream byteStream) {
        write(byteStream.getCache(), 0, byteStream.size());
    }

    public ByteBuffer deflateBlock() {
        // 压缩输入数据
        deflater.reset();
        deflater.setInput(uncompressedBuffer, 0, numUncompressedBytes);
        deflater.finish();

        // 记录压缩数据大小
        int compressedSize = deflater.deflate(compressedBuffer, 0, compressedBuffer.length);
        if (!deflater.finished()) {
            noCompressionDeflater.reset();
            noCompressionDeflater.setInput(uncompressedBuffer, 0, numUncompressedBytes);
            noCompressionDeflater.finish();
            compressedSize = noCompressionDeflater.deflate(compressedBuffer, 0, compressedBuffer.length);
            if (!noCompressionDeflater.finished()) {
                throw new IllegalStateException("unpossible");
            }
        }

        // 重置校验和计算函数
        crc32.reset();
        crc32.update(uncompressedBuffer, 0, numUncompressedBytes);

        // 写入 bgzip block
        writeGzipBlock(compressedSize, numUncompressedBytes, (int) crc32.getValue());

        // 清除未压缩数据段长度标记
        numUncompressedBytes = 0;
        return null;
    }

    private void writeGzipBlock(final int compressedSize, final int uncompressedSize, final int crc) {
        // 初始化 gzip 块头信息
        this.cache.put(MARGIN_CODE);

        // 块总大小
        final int totalBlockSize = compressedSize + 25;

        // 写入块大小信息
        this.cache.put((byte) (totalBlockSize & 0xFF));
        this.cache.put((byte) ((totalBlockSize >> 8) & 0xFF));

        // 写入块实体数据
        this.cache.put(compressedBuffer, 0, compressedSize);

        // 写入校验和
        this.crcValue[0] = (byte) (crc & 0xFF);
        this.crcValue[1] = (byte) ((crc >> 8) & 0xFF);
        this.crcValue[2] = (byte) ((crc >> 16) & 0xFF);
        this.crcValue[3] = (byte) ((crc >> 24) & 0xFF);
        this.cache.put(this.crcValue);

        // 写入未压缩前数据段大小
        this.uncompressedSize[0] = (byte) (uncompressedSize & 0xFF);
        this.uncompressedSize[1] = (byte) ((uncompressedSize >> 8) & 0xFF);
        this.uncompressedSize[2] = (byte) ((uncompressedSize >> 16) & 0xFF);
        this.uncompressedSize[3] = (byte) ((uncompressedSize >> 24) & 0xFF);
        this.cache.put(this.uncompressedSize);
    }

    @Override
    public void finish() {
        if (numUncompressedBytes > 0) {
            deflateBlock();
        }

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