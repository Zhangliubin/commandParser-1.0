package edu.sysu.pmglab.easytools;

import edu.sysu.pmglab.container.VolumeByteStream;
import edu.sysu.pmglab.unifyIO.FileStream;
import edu.sysu.pmglab.unifyIO.options.FileOptions;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * @author suranyi
 * @description MD5 校验
 */

public enum MD5 {
    /* 单例 */
    INSTANCE;

    final MessageDigest md5;

    /**
     * MD5 码缓冲区
     */
    private final HashMap<String, String> cache = new HashMap<>(16);

    MD5() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
            throw new UnsupportedOperationException("Invalid Exception");
        }
    }

    /**
     * 检验字节数组的 MD5 码
     * @param src 源数据
     * @return 指定字节数组的 MD5 校验结果
     */
    public static String check(byte[] src) {
        return check(src, 0, src.length);
    }

    /**
     * 检验字节数组的 MD5 码
     * @param src 源数据
     * @param offset 偏移量
     * @param length 校验数据长度
     */
    public static String check(byte[] src, int offset, int length) {
        synchronized (INSTANCE.md5) {
            INSTANCE.md5.reset();
            INSTANCE.md5.update(src, offset, length);

            return new BigInteger(1, INSTANCE.md5.digest()).toString(16);
        }
    }

    /**
     * 检验 ByteBuffer 的 MD5 码
     * @param src ByteBuffer 对象
     * @return 返回指定 ByteBuffer 校的 MD5 校验结果
     */
    public static String check(ByteBuffer src) {
        synchronized (INSTANCE.md5) {
            INSTANCE.md5.reset();
            INSTANCE.md5.update(src);

            return new BigInteger(1, INSTANCE.md5.digest()).toString(16);
        }
    }

    /**
     * 检验定容字节数组的 MD5 码
     * @param src 定容数组
     * @return 返回指定定容数组的 MD5 校验结果
     */
    public static String check(VolumeByteStream src) {
        synchronized (INSTANCE.md5) {
            INSTANCE.md5.reset();
            INSTANCE.md5.update(src.getCache(), 0, src.size());

            return new BigInteger(1, INSTANCE.md5.digest()).toString(16);
        }
    }

    /**
     * 校验文件的 MD5 码
     * @param fileName 检验文件
     * @return 返回指定文件的 MD5 校验结果
     */
    public static String check(String fileName) throws IOException {
        synchronized (INSTANCE.cache) {
            if (!INSTANCE.cache.containsKey(fileName)) {
                synchronized (INSTANCE.md5) {
                    INSTANCE.md5.reset();
                    FileStream file = new FileStream(fileName, FileOptions.CHANNEL_READER);

                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = (file.read(buffer))) != -1) {
                        INSTANCE.md5.update(buffer, 0, length);
                    }
                    file.close();

                    INSTANCE.cache.put(fileName, new BigInteger(1, INSTANCE.md5.digest()).toString(16));
                }
            }
            return INSTANCE.cache.get(fileName);
        }
    }

    /**
     * 校验多个文件的 md5 码
     * @param fileNames 检验的文件名列表
     * @return 返回多个文件的 MD5 校验结果
     */
    public static String[] check(String... fileNames) throws IOException {
        // 重设 md5 摘要器
        String[] md5s = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            md5s[i] = check(fileNames[i]);
        }
        return md5s;
    }

    /**
     * 清除缓冲区数据
     */
    public static void clear() {
        synchronized (INSTANCE.cache) {
            INSTANCE.cache.clear();
        }
    }

    /**
     * 获取 md5 计算实例
     */
    public static MessageDigest getMd5() {
        return INSTANCE.md5;
    }
}