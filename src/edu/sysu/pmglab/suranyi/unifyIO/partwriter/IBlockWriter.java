package edu.sysu.pmglab.suranyi.unifyIO.partwriter;

import edu.sysu.pmglab.suranyi.container.VolumeByteStream;

import java.nio.ByteBuffer;

/**
 * @author suranyi
 */

public interface IBlockWriter<T>{
    /**
     * 构造器，初始化压缩器
     * @param outputParam 输出参数
     * @return 根据是否压缩获取对应的压缩器
     */
    static IBlockWriter<ByteBuffer> getByteBufferInstance(BGZOutputParam outputParam, int cacheSize) {
        return outputParam.toBGZF ? new BGZIPBlockWriter(outputParam, cacheSize) : new DirectBlockWriter(outputParam, cacheSize);
    }

    /**
     * 构造器，初始化压缩器
     * @param outputParam 输出参数
     * @return 根据是否压缩获取对应的压缩器
     */
    static IBlockWriter<VolumeByteStream> getVolumeByteStreamInstance(BGZOutputParam outputParam, int cacheSize) {
        return outputParam.toBGZF ? new VolumeByteStreamBGZIPBlockBlockWriter(outputParam, cacheSize) : new VolumeByteStreamDirectBlockWriter(outputParam, cacheSize);
    }

    /**
     * 构造器，初始化压缩器
     * @param outputParam 输出参数
     * @return 根据是否压缩获取对应的压缩器
     */
    static IBlockWriter<VolumeByteStream> getVolumeByteStreamInstance(BGZOutputParam outputParam) {
        return getVolumeByteStreamInstance(outputParam, 2<< 20);
    }


    /**
     * 添加数据
     * @param bytes 字节数组
     */
    void write(byte[] bytes);

    /**
     * 添加数据
     * @param code 字节数组
     */
    void write(byte code);

    /**
     * 添加数据
     * @param bytes 字节数组
     * @param offset 偏移量
     * @param length 有效数据长度
     */
    void write(byte[] bytes, int offset, int length);

    /**
     * 添加数据
     * @param byteStream 定容字节数组
     */
    void write(VolumeByteStream byteStream);

    /**
     * 完成写入，转为读取数据模式
     */
    void finish();

    /**
     * 进入写入数据模式
     */
    void start();

    /**
     * 获取输出缓冲区
     * @return 输出缓冲区
     */
    T getCache();

    /**
     * 获取容器大小
     * @return 容器大小，int 类型
     */
    int capacity();

    /**
     * 获取剩余可用空间
     * @return 剩余可用空间
     */
    int remaining();

    /**
     * 清除缓冲区
     */
    void close();
}