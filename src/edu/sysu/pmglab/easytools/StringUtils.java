package edu.sysu.pmglab.easytools;

import java.util.Collection;

/**
 * @author suranyi
 * @description 字符串工具
 */

public class StringUtils {
    /**
     * 将字符串 src 复制 number 次
     * @param src 字符穿数据
     * @param times 复制次数
     */
    public static String copyN(String src, int times) {
        StringBuilder builder = new StringBuilder(src.length() * times);
        for (int i = 0; i < times; i++){
            builder.append(src);
        }
        return builder.toString();
    }

    /**
     * 连接多个字符串
     */
    public static String concat(String... srcs) {
        return String.join(" ", srcs);
    }

    /**
     * 连接多个字符串
     */
    public static Collection<String> addAll(Collection<String> target, String... srcs) {
        for (String src: srcs) {
            if (src.length() > 0) {
                target.add(src);
            }
        }
        return target;
    }
}
