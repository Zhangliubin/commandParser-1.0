package edu.sysu.pmglab.suranyi.unifyIO;

import bgzf4j.BGZFException;
import bgzf4j.BGZFFormatException;
import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.easytools.ArrayUtils;
import edu.sysu.pmglab.suranyi.easytools.ValueUtils;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @Data        :2021/06/11
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :BGZIP 子块解压器
 */

public class BlockGunzipper {
    private final Inflater inflater = new Inflater(true);
    public final VolumeByteStream compressedBlock = new VolumeByteStream(BGZFStreamConstants.MAX_COMPRESSED_BLOCK_SIZE);

    /**
     * 解压 gzip 子块数据
     * @param uncompressedBlock 写入未压缩数据区
     * @param compressedBlock 压缩数据
     * @param compressedLength 压缩长度
     * @return 未压缩数据区有效数据长度
     */
    int unzipBlock(byte[] uncompressedBlock, byte[] compressedBlock, int compressedLength) {
        try {
            // 验证 bgzip 头部信息
            if (!ArrayUtils.equal(compressedBlock, 0, 4, BGZFStreamConstants.MARGIN_CODE, 0, 4) ||
                    !ArrayUtils.equal(compressedBlock, 10, 12, BGZFStreamConstants.MARGIN_CODE, 10, 12)) {
                throw new BGZFFormatException("Invalid GZIP header");
            }

            // 读取压缩块总长度
            final int totalBlockSize = (compressedBlock[16] & 0xFF) + ((compressedBlock[17] & 0xFF) << 8) + 1;
            if (totalBlockSize != compressedLength) {
                throw new BGZFFormatException("GZIP blocksize disagreement");
            }

            // 读取预期解压大小
            final int deflatedSize = compressedLength - BGZFStreamConstants.BLOCK_HEADER_LENGTH - BGZFStreamConstants.BLOCK_FOOTER_LENGTH;
            int uncompressedSize = ValueUtils.byteArray2IntegerValue(compressedBlock[22 + deflatedSize], compressedBlock[23 + deflatedSize], compressedBlock[24 + deflatedSize], compressedBlock[25 + deflatedSize]);
            inflater.reset();

            // 解压数据
            inflater.setInput(compressedBlock, BGZFStreamConstants.BLOCK_HEADER_LENGTH, deflatedSize);
            final int inflatedBytes = inflater.inflate(uncompressedBlock, 0, uncompressedSize);
            if (inflatedBytes != uncompressedSize) {
                throw new BGZFFormatException("Did not inflate expected amount");
            }

            return uncompressedSize;

        } catch (DataFormatException e) {
            throw new BGZFException(e);
        }
    }

    /**
     * 解压 gzip 子块数据
     * @param uncompressedBlock 写入未压缩数据区
     * @param compressedBlock 压缩数据
     * @return 未压缩数据区有效数据长度
     */
    int unzipBlock(byte[] uncompressedBlock, byte[] compressedBlock) {
        // 读取压缩块总长度
        final int totalBlockSize = compressedBlock[16] + (compressedBlock[17] << 8) + 1;
        return unzipBlock(uncompressedBlock, compressedBlock, totalBlockSize);
    }

    /**
     * 解压 gzip 子块数据
     * @param uncompressedBlock 写入未压缩数据区
     * @return 未压缩数据区有效数据长度
     */
    public int unzipBlock(byte[] uncompressedBlock, FileStream fs) throws IOException {
        this.compressedBlock.reset();

        // 读取压缩块总长度
        fs.read(this.compressedBlock, 18);
        final int totalBlockSize = (this.compressedBlock.cacheOf(16) & 0xFF) + ((this.compressedBlock.cacheOf(17) & 0xFF) << 8) + 1;
        fs.read(this.compressedBlock, totalBlockSize - 18);
        return unzipBlock(uncompressedBlock, this.compressedBlock.getCache(), totalBlockSize);
    }

    public void close() {
        compressedBlock.close();
    }
}
