package edu.sysu.pmglab.suranyi.easytools;

import java.util.*;

/**
 * @author suranyi
 * @description 数组工具
 */

public class ArrayUtils {
    /**
     * 以 bits 分割的数据中，获取第 1 个位置的数据
     * @param src 源数据
     * @param bits 分隔符
     * @return 源数据的第一个分隔数据
     */
    public static byte[] getFirstBy(byte[] src, byte bits) {
        for (int i = 0; i < src.length; i++) {
            if (src[i] == bits) {
                return copyOfRange(src, 0, i);
            }
        }

        // 整个 src 都是第一个元素
        return copyOfRange(src, 0, src.length);
    }

    /**
     * 以 bits 分割的数据中，获取最后 1 个位置的数据，n 必须大于 1
     * @param src 源数据
     * @param bits 分隔符
     * @return 源数据的最后一个分隔数据
     */
    public static byte[] getLastBy(byte[] src, byte bits) {
        for (int i = src.length - 1; i >= 0; i--) {
            if (src[i] == bits) {
                return copyOfRange(src, i + 1, src.length);
            }
        }

        // 整个 src 都是最后一个元素
        return copyOfRange(src, 0, src.length);
    }

    /**
     * 以 \t 分割的数据中，获取第 n 个位置的数据，n 必须大于或等于 0
     * @param src 源数据
     * @param n 第 n 个数据
     * @return 第 n 个 TAB 分隔数据
     */
    public static byte[] getN(byte[] src, int n) {
        return getNBy(src, ByteCode.TAB, n);
    }

    /**
     * 以 bits 分割的数据中，获取第 n 个位置的数据，n 必须大于或等于 0
     * @param src 源数据
     * @param bits 分隔符
     * @param n 第 n 个数据
     * @return 第 n 个分隔数据
     */
    public static byte[] getNBy(byte[] src, byte bits, int n) {
        int start;

        if (n < 0) {
            return new byte[]{};
        } else if (n == 0) {
            start = 0;
        } else {
            start = indexOfN(src, bits, 0, n) + 1;
            if (start == 0) {
                // 说明没有找到第 n 个分隔符
                return new byte[]{};
            }
        }

        int end = indexOf(src, bits, start + 1);
        end = (end == -1) ? src.length : end;
        return copyOfRange(src, start, end);
    }

    /**
     * 元素去重
     * @param src 源数据
     * @return 利用 HashSet 进行去重的结果
     */
    public static byte[] dropDuplicated(byte[] src) {
        HashSet<Byte> set = new HashSet<>(src.length);
        ArrayList<Byte> list = new ArrayList<>(src.length);

        // 轮询每一个元素
        for (byte element : src) {
            if (!set.contains(element)) {
                set.add(element);
                list.add(element);
            }
        }

        // 返回最终结果
        if (src.length == list.size()) {
            return ArrayUtils.copyOfRange(src, 0, src.length);
        } else {
            byte[] copy = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                copy[i] = list.get(i);
            }
            return copy;
        }
    }

    /**
     * 元素去重
     * @param src 源数据
     * @return 利用 HashSet 进行去重的结果
     */
    public static int[] dropDuplicated(int[] src) {
        ArrayList<Integer> list = new ArrayList<>(src.length);
        HashSet<Integer> set = new HashSet<>(src.length);

        // 轮询每一个元素
        for (int element : src) {
            if (!set.contains(element)) {
                set.add(element);
                list.add(element);
            }
        }

        // 返回最终结果
        if (src.length == list.size()) {
            return ArrayUtils.copyOfRange(src, 0, src.length);
        } else {
            int[] copy = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                copy[i] = list.get(i);
            }
            return copy;
        }
    }

    /**
     * 元素去重
     * @param src 源数据
     * @return 利用 HashSet 进行去重的结果
     */
    public static long[] dropDuplicated(long[] src) {
        ArrayList<Long> list = new ArrayList<>(src.length);
        HashSet<Long> set = new HashSet<>(src.length);

        // 轮询每一个元素
        for (long element : src) {
            if (!set.contains(element)) {
                set.add(element);
                list.add(element);
            }
        }

        // 返回最终结果
        if (src.length == list.size()) {
            return ArrayUtils.copyOfRange(src, 0, src.length);
        } else {
            long[] copy = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                copy[i] = list.get(i);
            }
            return copy;
        }
    }

    /**
     * 元素去重
     * @param src 源数据
     * @return 利用 HashSet 进行去重的结果
     */
    public static String[] dropDuplicated(String[] src) {
        ArrayList<String> list = new ArrayList<>(src.length);
        HashSet<String> set = new HashSet<>(src.length);

        // 轮询每一个元素
        for (String t : src) {
            if (!set.contains(t)) {
                set.add(t);
                list.add(t);
            }
        }

        // 返回最终结果
        if (src.length == list.size()) {
            return ArrayUtils.copyOfRange(src, 0, src.length);
        } else {
            String[] copy = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                copy[i] = list.get(i);
            }
            return copy;
        }
    }

    /**
     * 删除字符串数组中的 null 对象
     * @param src 源数据
     * @return 移除 null 后的新字符串数组
     */
    public static String[] dropNull(String[] src) {
        int count = 0;
        for (String s : src) {
            if (s == null) {
                count += 1;
            }
        }
        if (count == 0) {
            return src;
        } else if (count == src.length) {
            return new String[]{};
        } else {
            String[] out = new String[src.length - count];
            for (int i = 0, index = 0; i < src.length; i++) {
                if (src[i] != null) {
                    out[index++] = src[i];
                }
            }
            return out;
        }
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static boolean[] copyOfRange(boolean[] src, int end) {
        return copyOfRange(src, 0, end);
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static byte[] copyOfRange(byte[] src, int end) {
        return copyOfRange(src, 0, end);
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static int[] copyOfRange(int[] src, int end) {
        return copyOfRange(src, 0, end);
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static String[] copyOfRange(String[] src, int end) {
        return copyOfRange(src, 0, end);
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static boolean[] copyOfRange(boolean[] src, int start, int end) {
        boolean[] copy = new boolean[end - start];
        System.arraycopy(src, start, copy, 0, copy.length);
        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static byte[] copyOfRange(byte[] src, int start, int end) {
        byte[] copy = new byte[end - start];
        System.arraycopy(src, start, copy, 0, copy.length);
        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static byte[][] copyOfRange(byte[][] src, int start, int end) {
        byte[][] copy = new byte[end - start][];
        System.arraycopy(src, start, copy, 0, copy.length);
        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static int[] copyOfRange(int[] src, int start, int end) {
        int[] copy = new int[end - start];
        System.arraycopy(src, start, copy, 0, copy.length);
        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static long[] copyOfRange(long[] src, int start, int end) {
        long[] copy = new long[end - start];
        System.arraycopy(src, start, copy, 0, copy.length);
        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static String[] copyOfRange(String[] src, int start, int end) {
        String[] copy = new String[end - start];
        System.arraycopy(src, start, copy, 0, copy.length);

        return copy;
    }

    /**
     * 局部拷贝
     * @param src 源数据
     * @param start 拷贝起点
     * @param end 拷贝终点 (左闭右开)
     * @return 局部拷贝后生成的子数组
     */
    public static <T> T[] copyOfRange(T[] src, int start, int end) {
        return Arrays.copyOfRange(src, start, end);
    }

    /**
     * 统计频数
     * @param src 布尔数组
     * @param target 统计的对象
     * @return target 在 src 中的频数
     */
    public static int valueCounts(boolean[] src, boolean target) {
        int count = 0;
        for (boolean element : src) {
            if (element == target) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计频数
     * @param src 字节数组
     * @param target 统计的对象
     * @return target 在 src 中的频数
     */
    public static int valueCounts(byte[] src, byte target) {
        int count = 0;
        for (byte element : src) {
            if (element == target) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计频数
     * @param src 整数数组
     * @param target 统计的对象
     * @return target 在 src 中的频数
     */
    public static int valueCounts(int[] src, int target) {
        int count = 0;
        for (int element : src) {
            if (element == target) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计频数
     * @param src 字符串数组
     * @param target 统计的对象
     * @return target 在 src 中的频数
     */
    public static int valueCounts(String[] src, String target) {
        int count = 0;
        for (String element : src) {
            if (element.equals(target)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计频数
     * @param src 字符串
     * @param target 统计的对象
     * @return target 在 src 中的频数
     */
    public static int valueCounts(String src, char target) {
        int count = 0;
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == target) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 统计频数
     * @param src 布尔数组
     * @return src 中所有元素的频数
     */
    public static HashMap<Boolean, Integer> valueCounts(boolean[] src) {
        HashMap<Boolean, Integer> dict = new HashMap<>(64);
        for (boolean element : src) {
            dict.put(element, dict.getOrDefault(element, 0) + 1);
        }
        return dict;
    }

    /**
     * 统计频数
     * @param src 字节数组
     * @return src 中所有元素的频数
     */
    public static HashMap<Byte, Integer> valueCounts(byte[] src) {
        HashMap<Byte, Integer> dict = new HashMap<>(64);
        for (byte element : src) {
            dict.put(element, dict.getOrDefault(element, 0) + 1);
        }
        return dict;
    }

    /**
     * 统计频数
     * @param src 整数数组
     * @return src 中所有元素的频数
     */
    public static HashMap<Integer, Integer> valueCounts(int[] src) {
        HashMap<Integer, Integer> dict = new HashMap<>(64);
        for (int element : src) {
            dict.put(element, dict.getOrDefault(element, 0) + 1);
        }
        return dict;
    }

    /**
     * 统计频数
     * @param src 字符串数组
     * @return src 中所有元素的频数
     */
    public static HashMap<String, Integer> valueCounts(String[] src) {
        HashMap<String, Integer> dict = new HashMap<>(64);
        for (String element : src) {
            dict.put(element, dict.getOrDefault(element, 0) + 1);
        }
        return dict;
    }

    /**
     * 获取从 start 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public static int indexOf(boolean[] src, boolean element, int start) {
        for (int i = start; i < src.length; i++) {
            if (src[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取从 start 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public static int indexOf(byte[] src, byte element, int start) {
        for (int i = start; i < src.length; i++) {
            if (src[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取从 offset 开始的第一个 pattern 元素所在的 index
     * @param src 源数据
     * @param offset 搜索起点
     * @param pattern 搜索的模式
     * @return 检索元素值所在的索引
     */
    public static int indexOf(byte[] src, int offset, byte[] pattern) {
        return Kmp.indexOf(src, offset, src.length, pattern);
    }

    /**
     * 获取从 start 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public static int indexOf(int[] src, int element, int start) {
        for (int i = start; i < src.length; i++) {
            if (src[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取从 start 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @return 检索元素值所在的索引
     */
    public static int indexOf(String[] src, String element, int start) {
        for (int i = start; i < src.length; i++) {
            if (src[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取从 0 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @return 检索元素值所在的索引
     */
    public static int indexOf(boolean[] src, boolean element) {
        return indexOf(src, element, 0);
    }

    /**
     * 获取从 0 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @return 检索元素值所在的索引
     */
    public static int indexOf(byte[] src, byte element) {
        return indexOf(src, element, 0);
    }

    /**
     * 获取从 start 开始的第一个 pattern 元素所在的 index
     * @param src 源数据
     * @param pattern 搜索的模式
     * @return 检索元素值所在的索引
     */
    public static int indexOf(byte[] src, byte[] pattern) {
        return Kmp.indexOf(src, pattern);
    }

    /**
     * 获取从 0 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @return 检索元素值所在的索引
     */
    public static int indexOf(int[] src, int element) {
        return indexOf(src, element, 0);
    }

    /**
     * 获取从 0 开始的第一个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @return 检索元素值所在的索引
     */
    public static int indexOf(String[] src, String element) {
        return indexOf(src, element, 0);
    }

    /**
     * 获取从 start 开始的第 times 个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param times 搜索次数
     * @return 检索元素值所在的索引
     */
    public static int indexOfN(boolean[] src, boolean element, int start, int times) {
        for (int i = 0; (i < times) && (start != -1); i++) {
            start = indexOf(src, element, start + 1);
        }
        return start;
    }

    /**
     * 获取从 start 开始的第 times 个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param times 搜索次数
     * @return 检索元素值所在的索引
     */
    public static int indexOfN(byte[] src, byte element, int start, int times) {
        for (int i = 0; (i < times) && (start != -1); i++) {
            start = indexOf(src, element, start + 1);
        }
        return start;
    }

    /**
     * 获取从 start 开始的第 times 个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param times 搜索次数
     * @return 检索元素值所在的索引
     */
    public static int indexOfN(int[] src, int element, int start, int times) {
        for (int i = 0; (i < times) && (start != -1); i++) {
            start = indexOf(src, element, start + 1);
        }
        return start;
    }

    /**
     * 获取从 start 开始的第 times 个 element 元素所在的 index
     * @param src 源数据
     * @param element 搜索的元素值
     * @param start 搜索起点
     * @param times 搜索次数
     * @return 检索元素值所在的索引
     */
    public static int indexOfN(String[] src, String element, int start, int times) {
        for (int i = 0; (i < times) && (start != -1); i++) {
            start = indexOf(src, element, start + 1);
        }
        return start;
    }

    /**
     * 是否包含指定元素 element
     * @param src 源数据
     * @param element 搜索的元素值
     * @return src 是否包含指定元素值 element
     */
    public static boolean contain(boolean[] src, boolean element) {
        return indexOf(src, element) != -1;
    }

    /**
     * 是否包含指定元素 element
     * @param src 源数据
     * @param element 搜索的元素值
     * @return src 是否包含指定元素值 element
     */
    public static boolean contain(byte[] src, byte element) {
        return indexOf(src, element) != -1;
    }

    /**
     * 是否包含指定元素 element
     * @param src 源数据
     * @param element 搜索的元素值
     * @return src 是否包含指定元素值 element
     */
    public static boolean contain(int[] src, int element) {
        return indexOf(src, element) != -1;
    }

    /**
     * 是否包含指定元素 element
     * @param src 源数据
     * @param element 搜索的元素值
     * @return src 是否包含指定元素值 element
     */
    public static boolean contain(String[] src, String element) {
        return indexOf(src, element) != -1;
    }

    /**
     * 取并集
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2 并去重
     */
    public static byte[] union(byte[] src1, byte[] src2) {
        return dropDuplicated(merge(src1, src2));
    }

    /**
     * 取并集
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2 并去重
     */
    public static int[] union(int[] src1, int[] src2) {
        return dropDuplicated(merge(src1, src2));
    }

    /**
     * 取并集
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2 并去重
     */
    public static String[] union(String[] src1, String[] src2) {
        return dropDuplicated(merge(src1, src2));
    }

    /**
     * 合并数组
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2
     */
    public static boolean[] merge(boolean[] src1, boolean[] src2) {
        boolean[] out = new boolean[src1.length + src2.length];
        System.arraycopy(src1, 0, out, 0, src1.length);
        System.arraycopy(src2, 0, out, src1.length, src2.length);
        return out;
    }

    /**
     * 合并数组
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2
     */
    public static byte[] merge(byte[] src1, byte[] src2) {
        byte[] out = new byte[src1.length + src2.length];
        System.arraycopy(src1, 0, out, 0, src1.length);
        System.arraycopy(src2, 0, out, src1.length, src2.length);
        return out;
    }

    /**
     * 合并数组
     * @param src 源数据
     * @return 合并 src
     */
    public static byte[] merge(byte[]... src) {
        int totalSize = 0;
        int cumsumSize = 0;
        for (int i = 0; i < src.length; i++) {
            totalSize += src[i].length;
        }

        byte[] out = new byte[totalSize];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, out, cumsumSize, src[i].length);
            cumsumSize += src[i].length;
        }
        return out;
    }

    /**
     * 合并数组
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2
     */
    public static int[] merge(int[] src1, int[] src2) {
        int[] out = new int[src1.length + src2.length];
        System.arraycopy(src1, 0, out, 0, src1.length);
        System.arraycopy(src2, 0, out, src1.length, src2.length);
        return out;
    }

    /**
     * 合并数组
     * @param src1 源数据1
     * @param src2 源数据2
     * @return 合并 src1 和 src2
     */
    public static String[] merge(String[] src1, String[] src2) {
        String[] out = new String[src1.length + src2.length];
        System.arraycopy(src1, 0, out, 0, src1.length);
        System.arraycopy(src2, 0, out, src1.length, src2.length);
        return out;
    }

    /**
     * 转为 HashSet
     * @param src 源数据
     * @return 将源数据转为 HashSet，便于进行包含性测试、获取独立元素个数
     */
    public static HashSet<Boolean> toSet(boolean[] src) {
        HashSet<Boolean> set = new HashSet<>(2);
        for (boolean element : src) {
            set.add(element);
        }
        return set;
    }

    /**
     * 转为 HashSet
     * @param src 源数据
     * @return 将源数据转为 HashSet，便于进行包含性测试、获取独立元素个数
     */
    public static HashSet<Byte> toSet(byte[] src) {
        HashSet<Byte> set = new HashSet<>(2);
        for (byte element : src) {
            set.add(element);
        }
        return set;
    }

    /**
     * 转为 HashSet
     * @param src 源数据
     * @return 将源数据转为 HashSet，便于进行包含性测试、获取独立元素个数
     */
    public static HashSet<Integer> toSet(int[] src) {
        HashSet<Integer> set = new HashSet<>(2);
        for (int element : src) {
            set.add(element);
        }
        return set;
    }

    /**
     * 转为 HashSet
     * @param src 源数据
     * @return 将源数据转为 HashSet，便于进行包含性测试、获取独立元素个数
     */
    public static HashSet<String> toSet(String[] src) {
        HashSet<String> set = new HashSet<>(2);
        set.addAll(Arrays.asList(src));
        return set;
    }

    /**
     * 转为包装类
     * @param booleans boolean 数组
     */
    public static Boolean[] wrap(boolean[] booleans) {
        Boolean[] outs = new Boolean[booleans.length];
        for (int i = 0; i < booleans.length; i++) {
            outs[i] = booleans[i];
        }

        return outs;
    }

    /**
     * 转为包装类
     * @param bytes byte 数组
     */
    public static Byte[] wrap(byte[] bytes) {
        Byte[] outs = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            outs[i] = bytes[i];
        }

        return outs;
    }

    /**
     * 转为包装类
     * @param shorts short 数组
     */
    public static Short[] wrap(short[] shorts) {
        Short[] outs = new Short[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            outs[i] = shorts[i];
        }

        return outs;
    }

    /**
     * 转为包装类
     * @param ints int 数组
     */
    public static Integer[] wrap(int[] ints) {
        Integer[] outs = new Integer[ints.length];
        for (int i = 0; i < ints.length; i++) {
            outs[i] = ints[i];
        }

        return outs;
    }

    /**
     * 转为包装类
     * @param longs long 数组
     */
    public static Long[] wrap(long[] longs) {
        Long[] outs = new Long[longs.length];
        for (int i = 0; i < longs.length; i++) {
            outs[i] = longs[i];
        }

        return outs;
    }

    /**
     * 转为 ArrayList
     * @param src 源数据
     * @return 将源数据转为 ArrayList
     */
    public static ArrayList<Boolean> toArrayList(boolean[] src) {
        ArrayList<Boolean> out = new ArrayList<>(src.length);
        for (boolean value : src) {
            out.add(value);
        }
        return out;
    }

    /**
     * 转为 ArrayList
     * @param src 源数据
     * @return 将源数据转为 ArrayList
     */
    public static ArrayList<Byte> toArrayList(byte[] src) {
        ArrayList<Byte> out = new ArrayList<>(src.length);
        for (byte value : src) {
            out.add(value);
        }
        return out;
    }

    /**
     * 转为 ArrayList
     * @param src 源数据
     * @return 将源数据转为 ArrayList
     */
    public static ArrayList<Integer> toArrayList(int[] src) {
        ArrayList<Integer> out = new ArrayList<>(src.length);
        for (int value : src) {
            out.add(value);
        }
        return out;
    }

    /**
     * 转为 ArrayList
     * @param src 源数据
     * @return 将源数据转为 ArrayList
     */
    public static ArrayList<String> toArrayList(String[] src) {
        ArrayList<String> out = new ArrayList<>(src.length);
        out.addAll(Arrays.asList(src));
        return out;
    }

    /**
     * 将 ArrayList 转为 数组
     * @param src 源数据
     * @return 将源数据转为 数组
     */
    public static String[] toStringArray(Collection<String> src) {
        return src.toArray(new String[]{});
    }

    /**
     * 将 ArrayList 转为 数组
     * @param src 源数据
     * @return 将源数据转为 数组
     */
    public static byte[] toByteArray(HashSet<Byte> src) {
        byte[] out = new byte[src.size()];
        int index = 0;
        for (byte v : src) {
            out[index++] = v;
        }
        return out;
    }

    /**
     * 将 ArrayList 转为 数组
     * @param src 源数据
     * @return 将源数据转为 数组
     */
    public static int[] toIntegerArray(Collection<Integer> src) {
        int[] out = new int[src.size()];
        int index = 0;
        for (int e: src) {
            out[index++] = e;
        }
        return out;
    }

    /**
     * 将 ArrayList 转为 数组
     * @param src 源数据
     * @return 将源数据转为 数组
     */
    public static long[] toLongArray(Collection<Long> src) {
        long[] out = new long[src.size()];
        int index = 0;
        for (long e: src) {
            out[index++] = e;
        }
        return out;
    }

    /**
     * 将 ArrayList 转为 数组
     * @param src 源数据
     * @return 将源数据转为 数组
     */
    public static String[] toStringArray(HashSet<String> src) {
        return src.toArray(new String[]{});
    }

    /**
     * 插入元素
     * @param src 源数据
     * @param index 插入索引值
     * @param element 插入的元素
     * @return 修改后的数组
     */
    public static boolean[] insert(boolean[] src, int index, boolean element) {
        if (index == src.length) {
            return append(src, element);
        } else if (index < 0 || index > src.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("index out of bounds (0 <= index:%d <= %d)", index, src.length));
        } else {
            boolean[] out = new boolean[src.length + 1];
            if (index == 0) {
                out[0] = element;
                System.arraycopy(src, 0, out, 1, src.length);
            } else {
                out[index] = element;
                System.arraycopy(src, 0, out, 0, index);
                System.arraycopy(src, index, out, index + 1, src.length - index);
            }
            return out;
        }
    }

    /**
     * 插入元素
     * @param src 源数据
     * @param index 插入索引值
     * @param element 插入的元素
     * @return 修改后的数组
     */
    public static byte[] insert(byte[] src, int index, byte element) {
        if (index == src.length) {
            return append(src, element);
        } else if (index < 0 || index > src.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length));
        } else {
            byte[] out = new byte[src.length + 1];
            if (index == 0) {
                out[0] = element;
                System.arraycopy(src, 0, out, 1, src.length);
            } else {
                out[index] = element;
                System.arraycopy(src, 0, out, 0, index);
                System.arraycopy(src, index, out, index + 1, src.length - index);
            }
            return out;
        }
    }

    /**
     * 插入元素
     * @param src 源数据
     * @param index 插入索引值
     * @param element 插入的元素
     * @return 修改后的数组
     */
    public static int[] insert(int[] src, int index, int element) {
        if (index == src.length) {
            return append(src, element);
        } else if (index < 0 || index > src.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length));
        } else {
            int[] out = new int[src.length + 1];
            if (index == 0) {
                out[0] = element;
                System.arraycopy(src, 0, out, 1, src.length);
            } else {
                out[index] = element;
                System.arraycopy(src, 0, out, 0, index);
                System.arraycopy(src, index, out, index + 1, src.length - index);
            }
            return out;
        }
    }

    /**
     * 插入元素
     * @param src 源数据
     * @param index 插入索引值
     * @param element 插入的元素
     * @return 修改后的数组
     */
    public static String[] insert(String[] src, int index, String element) {
        if (index == src.length) {
            return append(src, element);
        } else if (index < 0 || index > src.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length));
        } else {
            String[] out = new String[src.length + 1];
            if (index == 0) {
                out[0] = element;
                System.arraycopy(src, 0, out, 1, src.length);
            } else {
                out[index] = element;
                System.arraycopy(src, 0, out, 0, index);
                System.arraycopy(src, index, out, index + 1, src.length - index);
            }
            return out;
        }
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param element 追加的元素
     * @return 修改后的数组
     */
    public static boolean[] append(boolean[] src, boolean element) {
        boolean[] out = new boolean[src.length + 1];
        System.arraycopy(src, 0, out, 0, src.length);
        out[out.length - 1] = element;
        return out;
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param elements 追加的元素
     * @return 修改后的数组
     */
    public static boolean[] append(boolean[] src, boolean[] elements) {
        return merge(src, elements);
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param element 追加的元素
     * @return 修改后的数组
     */
    public static byte[] append(byte[] src, byte element) {
        byte[] out = new byte[src.length + 1];
        System.arraycopy(src, 0, out, 0, src.length);
        out[out.length - 1] = element;
        return out;
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param elements 追加的元素
     * @return 修改后的数组
     */
    public static byte[] append(byte[] src, byte[] elements) {
        return merge(src, elements);
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param element 追加的元素
     * @return 修改后的数组
     */
    public static int[] append(int[] src, int element) {
        int[] out = new int[src.length + 1];
        System.arraycopy(src, 0, out, 0, src.length);
        out[out.length - 1] = element;
        return out;
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param elements 追加的元素
     * @return 修改后的数组
     */
    public static int[] append(int[] src, int[] elements) {
        return merge(src, elements);
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param element 追加的元素
     * @return 修改后的数组
     */
    public static String[] append(String[] src, String element) {
        String[] out = new String[src.length + 1];
        System.arraycopy(src, 0, out, 0, src.length);
        out[out.length - 1] = element;
        return out;
    }

    /**
     * 追加元素
     * @param src 源数据
     * @param elements 追加的元素
     * @return 修改后的数组
     */
    public static String[] append(String[] src, String[] elements) {
        return merge(src, elements);
    }

    /**
     * 移除元素
     * @param src 源数据
     * @param index 移除数据的索引
     * @return 修改后的数组
     */
    public static boolean[] remove(boolean[] src, int index) {
        boolean[] out = new boolean[src.length - 1];
        if (index == 0) {
            // 移除第一个元素
            System.arraycopy(src, 1, out, 0, src.length);
        } else if (index == src.length - 1) {
            // 移除最后一个元素
            System.arraycopy(src, 0, out, 0, src.length - 1);
        } else if ((index > 0) && (index < src.length - 1)) {
            System.arraycopy(src, 0, out, 0, index);
            System.arraycopy(src, index + 1, out, index, src.length - index);
        } else {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length - 1));
        }

        return out;
    }

    /**
     * 移除元素
     * @param src 源数据
     * @param index 移除数据的索引
     * @return 修改后的数组
     */
    public static byte[] remove(byte[] src, int index) {
        byte[] out = new byte[src.length - 1];
        if (index == 0) {
            // 移除第一个元素
            System.arraycopy(src, 1, out, 0, src.length);
        } else if (index == src.length - 1) {
            // 移除最后一个元素
            System.arraycopy(src, 0, out, 0, src.length - 1);
        } else if ((index > 0) && (index < src.length - 1)) {
            System.arraycopy(src, 0, out, 0, index);
            System.arraycopy(src, index + 1, out, index, src.length - index);
        } else {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length - 1));
        }

        return out;
    }

    /**
     * 移除元素
     * @param src 源数据
     * @param index 移除数据的索引
     * @return 修改后的数组
     */
    public static int[] remove(int[] src, int index) {
        int[] out = new int[src.length - 1];
        if (index == 0) {
            // 移除第一个元素
            System.arraycopy(src, 1, out, 0, src.length);
        } else if (index == src.length - 1) {
            // 移除最后一个元素
            System.arraycopy(src, 0, out, 0, src.length - 1);
        } else if ((index > 0) && (index < src.length - 1)) {
            System.arraycopy(src, 0, out, 0, index);
            System.arraycopy(src, index + 1, out, index, src.length - index);
        } else {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length - 1));
        }

        return out;
    }

    /**
     * 移除元素
     * @param src 源数据
     * @param index 移除数据的索引
     * @return 修改后的数组
     */
    public static String[] remove(String[] src, int index) {
        String[] out = new String[src.length - 1];
        if (index == 0) {
            // 移除第一个元素
            System.arraycopy(src, 1, out, 0, src.length);
        } else if (index == src.length - 1) {
            // 移除最后一个元素
            System.arraycopy(src, 0, out, 0, src.length - 1);
        } else if ((index > 0) && (index < src.length - 1)) {
            System.arraycopy(src, 0, out, 0, index);
            System.arraycopy(src, index + 1, out, index, src.length - index);
        } else {
            throw new ArrayIndexOutOfBoundsException(String.format("Index out of bounds (0 <= index:%d <= %d).", index, src.length - 1));
        }

        return out;
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param values 配对值
     * @return 配对字典
     */
    public static HashMap<String, String> zip(String[] keys, String[] values) {
        int num = Math.min(keys.length, values.length);
        HashMap<String, String> map = new HashMap<>(num);

        // 进行配对
        for (int i = 0; i < num; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param values 配对值
     * @return 配对字典
     */
    public static HashMap<String, Integer> zip(String[] keys, int[] values) {
        HashMap<String, Integer> map = new HashMap<>(keys.length);

        // 进行配对
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param values 配对值
     * @return 配对字典
     */
    public static HashMap<Integer, String> zip(int[] keys, String[] values) {
        HashMap<Integer, String> map = new HashMap<>(keys.length);

        // 进行配对
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param values 配对值
     * @return 配对字典
     */
    public static HashMap<Integer, Integer> zip(int[] keys, int[] values) {
        HashMap<Integer, Integer> map = new HashMap<>(keys.length);

        // 进行配对
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param offset 以 [offset, keys.length + offset - 1] 作为配对值
     * @return 配对字典
     */
    public static HashMap<Integer, Integer> zip(int[] keys, int offset) {
        return zip(keys, range(offset, keys.length + offset - 1));
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @param offset 以 [offset, keys.length + offset - 1] 作为配对值
     * @return 配对字典
     */
    public static HashMap<String, Integer> zip(String[] keys, int offset) {
        return zip(keys, range(offset, keys.length + offset - 1));
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @return 配对字典
     */
    public static HashMap<String, Integer> zip(String[] keys) {
        return zip(keys, 0);
    }

    /**
     * 元素配对为 hashMap
     * @param keys 配对键
     * @return 配对字典
     */
    public static HashMap<Integer, Integer> zip(int[] keys) {
        return zip(keys, 0);
    }

    /**
     * 左闭右闭地产生序列数据
     * @param end 末尾值
     * @return 序列数据
     */
    public static int[] range(int end) {
        return range(0, end);
    }

    /**
     * 左闭右闭地产生序列数据
     * @param start 起始值
     * @param end 末尾值
     * @return 序列数据
     */
    public static int[] range(int start, int end) {
        return range(start, end, 1);
    }

    /**
     * 左闭右闭地产生序列数据
     * @param start 起始值
     * @param end 末尾值
     * @param step 步长
     * @return 序列数据
     */
    public static int[] range(int start, int end, int step) {
        if (step == 0) {
            throw new UnsupportedOperationException("Invalid Value: step = 0");
        }

        if (step > 0) {
            if (end >= start) {
                int[] out = new int[(end - start) / step + 1];
                for (int i = 0; i < out.length; i++) {
                    out[i] = start + i * step;
                }
                return out;
            } else {
                throw new UnsupportedOperationException("Invalid Value: step > 0 && end < start");
            }
        } else {
            // step < 0
            if (end <= start) {
                int[] out = new int[(start - end) / (-step) + 1];
                for (int i = 0; i < out.length; i++) {
                    out[i] = start + i * step;
                }
                return out;
            } else {
                throw new UnsupportedOperationException("Invalid Value: step > 0 && end < start");
            }
        }
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param target 第二个数组
     * @return 数组元素是否相同
     */
    public static boolean equal(byte[] origin, byte[] target) {
        return equal(origin, 0, origin.length, target, 0, target.length);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originOffset 第一个数组的偏移量
     * @param target 第二个数组
     * @param targetOffset 第二个数组的偏移量
     * @return 数组元素是否相同
     */
    public static boolean equal(byte[] origin, int originOffset, byte[] target, int targetOffset) {
        return equal(origin, originOffset, origin.length, target, targetOffset, target.length);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originStart 第一个数组的校验起点
     * @param originEnd 第一个数组的校验终点
     * @param target 第二个数组
     * @param targetStart 第二个数组的校验起点
     * @param targetEnd 第二个数组的校验终点
     * @return 数组元素是否相同
     */
    public static boolean equal(byte[] origin, int originStart, int originEnd, byte[] target, int targetStart, int targetEnd) {
        int length1 = originEnd - originStart;

        if ((length1 != (targetEnd - targetStart))) {
            return false;
        }

        return equal(origin, originStart, target, targetStart, length1);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originOffset 第一个数组的偏移量
     * @param target 第二个数组
     * @param targetOffset 第二个数组的偏移量
     * @param length 校验窗口大小
     * @return 数组元素是否相同
     */
    public static boolean equal(byte[] origin, int originOffset, byte[] target, int targetOffset, int length) {
        if ((originOffset + length > origin.length) || (targetOffset + length > target.length)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (origin[i + originOffset] != target[i + targetOffset]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param target 第二个数组
     * @return 数组元素是否相同
     */
    public static boolean equal(int[] origin, int[] target) {
        return equal(origin, 0, origin.length, target, 0, target.length);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originOffset 第一个数组的偏移量
     * @param target 第二个数组
     * @param targetOffset 第二个数组的偏移量
     * @return 数组元素是否相同
     */
    public static boolean equal(int[] origin, int originOffset, int[] target, int targetOffset) {
        return equal(origin, originOffset, origin.length, target, targetOffset, target.length);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originStart 第一个数组的校验起点
     * @param originEnd 第一个数组的校验终点
     * @param target 第二个数组
     * @param targetStart 第二个数组的校验起点
     * @param targetEnd 第二个数组的校验终点
     * @return 数组元素是否相同
     */
    public static boolean equal(int[] origin, int originStart, int originEnd, int[] target, int targetStart, int targetEnd) {
        int length1 = originEnd - originStart;

        if ((length1 != (targetEnd - targetStart))) {
            return false;
        }

        return equal(origin, originStart, target, targetStart, length1);
    }

    /**
     * 判断两个字节数组是否一致
     * @param origin 第一个数组
     * @param originOffset 第一个数组的偏移量
     * @param target 第二个数组
     * @param targetOffset 第二个数组的偏移量
     * @param length 校验窗口大小
     * @return 数组元素是否相同
     */
    public static boolean equal(int[] origin, int originOffset, int[] target, int targetOffset, int length) {
        if ((originOffset + length > origin.length) || (targetOffset + length > target.length)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (origin[i + originOffset] != target[i + targetOffset]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 校验字符串是否以 prefixes 作为开头
     * @param src 字符串
     * @param prefixes 前缀符
     * @return 字符串是否以 prefixes 作为开头
     */
    public static boolean startWiths(String src, String... prefixes) {
        for (String prefix : prefixes) {
            if (src.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验字符串是否以 prefixes 作为开头
     * @param src 字符串
     * @param prefixes 前缀符
     * @return 字符串是否以 prefixes 作为开头
     */
    public static boolean[] startWiths(String[] src, String... prefixes) {
        boolean[] out = new boolean[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = startWiths(src[i], prefixes);
        }
        return out;
    }

    /**
     * 校验字符串是否以 extensions 作为结尾
     * @param src 字符串
     * @param extensions 后缀符
     * @return 字符串是否以 extensions 作为结尾
     */
    public static boolean endWiths(String src, String... extensions) {
        for (String extension : extensions) {
            if (src.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验字符串是否以 extensions 作为结尾
     * @param src 字符串
     * @param extensions 后缀符
     * @return 字符串是否以 extensions 作为结尾
     */
    public static boolean[] endWiths(String[] src, String... extensions) {
        boolean[] out = new boolean[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = endWiths(src[i], extensions);
        }
        return out;
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param src 字节数组
     * @param prefix 前缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public static boolean startWiths(byte[] src, byte[] prefix) {
        // 如果 src 的长度比校验长度 extensions 还要小，那必定不可能是以它为开始的
        if (src.length < prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (src[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验字节数组是否以 prefix 作为起始信息
     * @param src 字节数组
     * @param prefix 前缀符
     * @return 字节数组是否以 prefix 作为起始信息
     */
    public static boolean startWiths(byte[] src, int offset, byte[] prefix) {
        // 如果 src 的长度比校验长度 extensions 还要小，那必定不可能是以它为开始的
        if (src.length - offset < prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (src[i + offset] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验字节数组是否以 extension 作为结尾
     * @param src 字节数组
     * @param extension 后缀符
     * @return 字节数组是否以 extension 作为结尾
     */
    public static boolean endWiths(byte[] src, byte[] extension) {
        if (src.length < extension.length) {
            return false;
        }

        int offset = src.length - extension.length;
        for (int i = 0; i < extension.length; i++) {
            if (src[offset + i] != extension[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取 hashmap 的键
     * @param maps 以字节值作为键的 HashMap
     * @return 该 HashMap 的键列表
     */
    public static <V> byte[] getByteKey(Map<Byte, V> maps) {
        byte[] keys = new byte[maps.size()];
        int index = 0;
        for (byte key : maps.keySet()) {
            keys[index++] = key;
        }

        Arrays.sort(keys);
        return keys;
    }

    /**
     * 获取 hashmap 的键
     * @param maps 以字节值作为键的 HashMap
     * @return 该 HashMap 的键列表
     */
    public static <V> int[] getIntegerKey(Map<Integer, V> maps) {
        int[] keys = new int[maps.size()];
        int index = 0;
        for (int key : maps.keySet()) {
            keys[index++] = key;
        }

        Arrays.sort(keys);
        return keys;
    }

    /**
     * 获取 map 的键
     * @param maps Map
     * @return 该 Map 的键列表
     */
    @SuppressWarnings("unchecked")
    public static <K, V> K[] getMapKey(Map<K, V> maps) {
        Object[] keys = new Object[maps.size()];
        int index = 0;
        for (K key : maps.keySet()) {
            keys[index++] = key;
        }

        Arrays.sort(keys);
        return (K[]) keys;
    }

    /**
     * 将 ArrayList 数据转为 HashMap
     * @param pairs 输入的三维整数数组（最外层是 arraylist）
     */
    public static HashMap<Integer, int[]> convertIntegerPairs(ArrayList<int[][]> pairs) {
        HashMap<Integer, int[]> pairsHashMap = new HashMap(pairs.size());

        for (int[][] pair : pairs) {
            // 长度为 1，删除染色体数据模式
            if (pair.length == 1) {
                for (int pairKey : pair[0]) {
                    pairsHashMap.put(pairKey, null);
                }
            } else {
                for (int pairKey : pair[0]) {
                    if (pairsHashMap.containsKey(pairKey)) {
                        if (pairsHashMap.get(pairKey) != null) {
                            pairsHashMap.put(pairKey, ArrayUtils.dropDuplicated(ArrayUtils.append(pairsHashMap.get(pairKey), pair[1])));
                        }
                    } else {
                        pairsHashMap.put(pairKey, pair[1]);
                    }
                }
            }
        }

        return pairsHashMap;
    }

    /**
     * 排序方法
     * @param src 源数据
     * @param comparator 比较器
     */
    public static <T> T[] sort(T[] src, Comparator<? super T> comparator) {
        Arrays.sort(src, comparator);
        return src;
    }

    /**
     * 排序方法
     * @param src 源数据
     * @param comparator 比较器
     */
    public static <T> T[] sort(T[] src, int start, int end, Comparator<? super T> comparator) {
        Arrays.sort(src, start, end, comparator);
        return src;
    }
}
