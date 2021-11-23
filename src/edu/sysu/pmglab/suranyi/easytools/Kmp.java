package edu.sysu.pmglab.suranyi.easytools;

/**
 * @author suranyi
 * @description 标准 KMP 搜索算法
 */

public class Kmp {
    public static int indexOf(byte[] src, int start, int end, byte[] pattern) {
        /* 在字节数组中搜索第一个匹配模式的位置 */
        int[] failure = computeFailure(pattern);

        int j = 0;

        for (int i = start; i < end; i++) {
            while (j > 0 && pattern[j] != src[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == src[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    public static int indexOf(byte[] src, byte[] pattern) {
        return indexOf(src, 0, src.length, pattern);
    }

    private static int[] computeFailure(byte[] pattern) {
        // 计算自匹配部分
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
}