package edu.sysu.pmglab.unifyIO.bgztools;

import bgzf4j.BGZFInputStream;
import bgzf4j.BGZFStreamConstants;
import edu.sysu.pmglab.check.Assert;
import edu.sysu.pmglab.easytools.ArrayUtils;
import edu.sysu.pmglab.easytools.MD5;
import edu.sysu.pmglab.unifyIO.FileStream;
import edu.sysu.pmglab.unifyIO.options.FileOptions;
import edu.sysu.pmglab.unifyIO.partwriter.BGZIPBlockWriter;
import edu.sysu.pmglab.unifyIO.pbgzip.ParallelBGZFOutputStream;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author suranyi
 * @description BGZIP 工具
 */

public class BGZTools {
    /**
     * 压缩指定的文件
     * @param inputFileName 输入文件名
     * @param outputFileName 输出文件名
     * @param level 压缩级别
     * @param nThreads 线程数
     */
    public static void compress(String inputFileName, String outputFileName, int level, int nThreads) throws IOException {
        Assert.that(!inputFileName.equals(outputFileName));

        ParallelBGZFOutputStream compressor = new ParallelBGZFOutputStream(outputFileName, nThreads, level);
        FileStream fIn = new FileStream(inputFileName, FileOptions.CHANNEL_READER);

        byte[] buffer = new byte[BGZFStreamConstants.DEFAULT_UNCOMPRESSED_BLOCK_SIZE << 2];
        int length;
        while ((length = fIn.read(buffer)) != -1) {
            compressor.write(buffer, 0, length);
        }
        fIn.close();
        compressor.close();
    }

    /**
     * 将 gz 格式的文件转换为 block gz 格式
     * @param inputFileName 输入文件名
     * @param outputFileName 输出文件名
     * @param level 压缩级别
     * @param nThreads 线程数
     */
    public static void convert(String inputFileName, String outputFileName, int level, int nThreads) throws IOException {
        Assert.that(!inputFileName.equals(outputFileName));

        FileStream decompressor = new FileStream(inputFileName, FileOptions.GZIP_READER);
        ParallelBGZFOutputStream reCompressor = new ParallelBGZFOutputStream(outputFileName, nThreads, level);

        byte[] buffer = new byte[8192];
        int length;
        while ((length = decompressor.read(buffer)) != -1) {
            reCompressor.write(buffer, 0, length);
        }
        decompressor.close();
        reCompressor.close();
    }

    /**
     * 解压文件
     * @param inputFileName 输入文件名
     * @param outputFileName 输出文件名
     */
    public static void decompress(String inputFileName, String outputFileName) throws IOException {
        Assert.that(!inputFileName.equals(outputFileName));

        FileStream decompressor = new FileStream(inputFileName, FileOptions.GZIP_READER);
        FileStream fOut = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);

        byte[] buffer = new byte[8192];
        int length;
        while ((length = decompressor.read(buffer)) != -1) {
            fOut.write(buffer, 0, length);
        }
        decompressor.close();
        fOut.close();
    }

    /**
     * 校验文件的 md5 码
     * @param inputFileName 输入文件名
     */
    public static String md5(String inputFileName) throws IOException {
        final MessageDigest md5 = MD5.getMd5();

        synchronized (MD5.getMd5()) {
            md5.reset();
            try (FileStream fileStream = new FileStream(inputFileName, FileOptions.GZIP_READER)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = (fileStream.read(buffer))) != -1) {
                    md5.update(buffer, 0, length);
                }
            }

            try {
                return new BigInteger(1, md5.digest()).toString(16);
            } finally {
                md5.reset();
            }
        }
    }

    /**
     * 提取 gz 文件的一部分数据
     * @param inputFileName 输入文件名
     * @param outFileName 输出文件名
     * @param level 压缩级别
     * @param nThreads 线程数
     */
    public static void extract(String inputFileName, String outFileName, long start, long end, int level, int nThreads) throws IOException {
        Assert.that(!inputFileName.equals(outFileName));
        Assert.that(start >= 0 && end >= start);

        BGZFInputStream bgzfInputStream = new BGZFInputStream(new File(inputFileName));
        ParallelBGZFOutputStream subFile = new ParallelBGZFOutputStream(outFileName, nThreads, level);

        // 调整起点指针
        long seek = start;
        byte[] cache = new byte[8192];
        int length;
        bgzfInputStream.seek(start);

        while (((length = bgzfInputStream.read(cache)) != -1) && seek <= end) {
            int dataToWrite = (int) Math.min(end - seek, length);
            subFile.write(cache, 0, dataToWrite);
            seek += dataToWrite;
        }

        bgzfInputStream.close();
        subFile.close();
    }

    /**
     * 连接多个 bgz 格式的文件
     * @param outputFileName 输出文件名
     * @param inputFileNames 多个待连接的文件名
     */
    public static void concat(String outputFileName, String... inputFileNames) throws IOException {
        Assert.NotEmpty(inputFileNames);

        for (String inputFileName : inputFileNames) {
            Assert.that(!outputFileName.equals(inputFileName));
        }

        FileStream fOut = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
        for (String inputFileName : inputFileNames) {
            FileStream fIn = new FileStream(inputFileName, FileOptions.CHANNEL_READER);
            byte[] cache = new byte[28];
            fIn.seek(fIn.size() - 28);
            fIn.read(cache);
            if (ArrayUtils.equal(cache, BGZIPBlockWriter.EMPTY_GZIP_BLOCK)) {
                fIn.writeTo(0, fIn.size() - 28, fOut.getChannel());
            } else {
                fIn.writeTo(0, fIn.size(), fOut.getChannel());
            }
            fIn.close();
        }

        fOut.write(BGZIPBlockWriter.EMPTY_GZIP_BLOCK);
        fOut.close();
    }
}