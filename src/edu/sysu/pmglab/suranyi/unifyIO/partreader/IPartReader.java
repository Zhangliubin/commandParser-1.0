package edu.sysu.pmglab.suranyi.unifyIO.partreader;

import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;

import java.io.IOException;

/**
 * @author suranyi
 * @description 分块读取器
 */

public interface IPartReader {
    /**
     * 通过文件扩展名获取实例
     * @param inputFileName 输入文件
     * @return 对应方法的分块阅读器
     */
    static IPartReader getInstance(String inputFileName) throws IOException {
        if (inputFileName.endsWith(".gz")) {
            return new BGZIPPartReader(inputFileName);
        } else {
            return new DefaultPartReader(inputFileName);
        }
    }

    /**
     * 按行读取数据
     * @param lineCache 行数据缓冲区
     */
    int readLine(VolumeByteStream lineCache) throws IOException;

    /**
     * 对文件进行分块，不一定产生 nThreads 个子文件
     * @param nThreads 分块线程数
     */
    FileStream[] part(int nThreads) throws IOException;
}