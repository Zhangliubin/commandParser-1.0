package edu.sysu.pmglab.suranyi.easytools;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 数值格式化工具
 */

public class ValueUtils {
    /**
     * 获取多个整数值中的最大值
     * @param values 传入多个整数值
     * @return values 中的最大值
     */
    public static int max(int... values) {
        if (values.length > 0) {
            int maxValue = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] > maxValue) {
                    maxValue = values[i];
                }
            }
            return maxValue;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个整数值中的最大值的索引
     * @param values 传入多个整数值
     * @return values 中的最大值的索引
     */
    public static int argmax(int... values) {
        if (values.length > 0) {
            int maxValue = values[0];
            int maxValueIndex = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] > maxValue) {
                    maxValue = values[i];
                    maxValueIndex = i;
                }
            }
            return maxValueIndex;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个double值中的最大值
     * @param values 传入多个double值
     * @return values 中的最大值
     */
    public static double max(double... values) {
        if (values.length > 0) {
            double maxValue = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] > maxValue) {
                    maxValue = values[i];
                }
            }
            return maxValue;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个double值中的最大值的索引
     * @param values 传入多个double值
     * @return values 中的最大值的索引
     */
    public static int argmax(double... values) {
        if (values.length > 0) {
            double maxValue = values[0];
            int maxValueIndex = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] > maxValue) {
                    maxValue = values[i];
                    maxValueIndex = i;
                }
            }
            return maxValueIndex;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个整数值中的最小值
     * @param values 传入多个整数值
     * @return values 中的最小值
     */
    public static int min(int... values) {
        if (values.length > 0) {
            int minValue = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                }
            }
            return minValue;
        } else {
            throw new UnsupportedOperationException("min expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个整数值中的最小值的索引
     * @param values 传入多个整数值
     * @return values 中的最小值的索引
     */
    public static int argmin(int... values) {
        if (values.length > 0) {
            int minValue = values[0];
            int minValueIndex = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                    minValueIndex = i;
                }
            }
            return minValueIndex;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个整数值中的最小值
     * @param values 传入多个整数值
     * @return values 中的最小值
     */
    public static long min(long... values) {
        if (values.length > 0) {
            long minValue = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                }
            }
            return minValue;
        } else {
            throw new UnsupportedOperationException("min expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个整数值中的最小值的索引
     * @param values 传入多个整数值
     * @return values 中的最小值的索引
     */
    public static long argmin(long... values) {
        if (values.length > 0) {
            long minValue = values[0];
            long minValueIndex = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                    minValueIndex = i;
                }
            }
            return minValueIndex;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个double值中的最小值
     * @param values 传入多个double值
     * @return values 中的最小值
     */
    public static double min(double... values) {
        if (values.length > 0) {
            double minValue = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                }
            }
            return minValue;
        } else {
            throw new UnsupportedOperationException("min expected at least 1 argument, got 0.");
        }
    }

    /**
     * 获取多个double值中的最小值的索引
     * @param values 传入多个double值
     * @return values 中的最小值的索引
     */
    public static int argmin(double... values) {
        if (values.length > 0) {
            double minValue = values[0];
            int minValueIndex = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minValue) {
                    minValue = values[i];
                    minValueIndex = i;
                }
            }
            return minValueIndex;
        } else {
            throw new UnsupportedOperationException("max expected at least 1 argument, got 0.");
        }
    }

    /**
     * 求和
     * @param src 整数数组
     * @return 求和值
     */
    public static int sum(int... src) {
        int sum = 0;
        for (int value : src) {
            sum += value;
        }
        return sum;
    }

    /**
     * 求和
     * @param src 整数数组
     * @return 求和值
     */
    public static long sum(long... src) {
        long sum = 0;
        for (long value : src) {
            sum += value;
        }
        return sum;
    }

    /**
     * 求和
     * @param src 浮点数数组
     * @return 求和值
     */
    public static float sum(float... src) {
        float sum = 0;
        for (float value : src) {
            sum += value;
        }
        return sum;
    }

    /**
     * 求和
     * @param src 双精度浮点数数组
     * @return 求和值
     */
    public static double sum(double... src) {
        double sum = 0;
        for (double value : src) {
            sum += value;
        }
        return sum;
    }

    /**
     * 将 value 值转为其字节数组表示法
     * @param value 数值
     * @return value 值的字节表示法 (数值内存模型)
     */
    public static byte[] booleanValue2ByteArray(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }

    /**
     * 将 value 值转为其字节数组表示法
     * @param value 数值
     * @return value 值的字节表示法 (数值内存模型)
     */
    public static byte[] shortValue2ByteArray(short value) {
        return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF)};
    }

    /**
     * 将 value 值转为其字节数组表示法
     * @param value 数值
     * @return value 值的字节表示法 (数值内存模型)
     */
    public static byte[] intValue2ByteArray(int value) {
        return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF), (byte) ((value >> 24) & 0xFF)};
    }

    /**
     * 将 value 值转为其字节数组表示法
     * @param value 数值
     * @param length 指定保存的字节数组长度
     * @return value 值的字节表示法 (数值内存模型)
     */
    public static byte[] value2ByteArray(long value, int length) {
        byte[] out = new byte[length];
        for (int i = 0; i < length; i++) {
            out[i] = (byte) ((value >> (i * 8)) & 0xFF);
        }
        return out;
    }

    /**
     * 将字节数组表示法的值转为其 数值
     * @param value 数值
     * @return 用于计算的 value 数值
     */
    public static boolean byteArray2BooleanValue(byte value) {
        if (value == 0) {
            return false;
        } else if (value == 1) {
            return true;
        } else {
            throw new UnsupportedOperationException("Invalid Value: " + value);
        }
    }

    /**
     * 将字节数组表示法的值转为其 数值
     * @param value 数值
     * @return 用于计算的 value 数值
     */
    public static short byteArray2ShortValue(byte[] value) {
        if (value.length == 2) {
            return (short) ((value[0] & 0xFF) + ((value[1] & 0xFF) << 8));
        } else {
            throw new UnsupportedOperationException("Invalid Value: " + Arrays.toString(value));
        }
    }

    /**
     * 将字节数组表示法的值转为其 数值
     * @param value 数值
     * @return 用于计算的 value 数值
     */
    public static int byteArray2IntegerValue(byte[] value) {
        if (value.length == 4) {
            return (value[0] & 0xFF) + ((value[1] & 0xFF) << 8) + ((value[2] & 0xFF) << 16) + ((value[3] & 0xFF) << 24);
        } else {
            throw new UnsupportedOperationException("Invalid Value: " + Arrays.toString(value));
        }
    }

    /**
     * 将字节数组表示法的逐元素转为其 数值
     * @param value1 从高往低第一个 byte 值
     * @param value2 从高往低第二个 byte 值
     * @return 用于计算的 value 数值
     */
    public static short byteArray2ShortValue(byte value1, byte value2) {
        return (short) ((value1 & 0xFF) + ((value2 & 0xFF) << 8));
    }

    /**
     * 将字节数组表示法的逐元素转为其 数值
     * @param value1 从高往低第一个 byte 值
     * @param value2 从高往低第二个 byte 值
     * @param value3 从高往低第三个 byte 值
     * @param value4 从高往低第四个 byte 值
     * @return 用于计算的 value 数值
     */
    public static int byteArray2IntegerValue(byte value1, byte value2, byte value3, byte value4) {
        return (value1 & 0xFF) + ((value2 & 0xFF) << 8) + ((value3 & 0xFF) << 16) + ((value4 & 0xFF) << 24);
    }

    /**
     * 将字节数组表示法的逐元素转为其 数值
     * @param src 内存模型表示法的字节数组
     * @return 用于计算的 value 数值
     */
    public static long byteArray2Value(byte[] src) {
        long value = 0;
        for (int i = 0; i < src.length; i++) {
            value += ((long) (src[i] & 0xFF) << (i * 8));
        }
        return value;
    }

    /**
     * 将字节数组形式的整数转危为 int 类型，等价于 Integer.valueOf(new String(src))，但速度更快
     */
    public static int integerValueOf(byte[] src) {
        int value = 0;
        for (byte v : src) {
            value = value * 10 + (v - 48);
        }

        return value;
    }

    /**
     * 将 int 类型整数转为其字符串形式的字节数组，等价于 String.valueOf().getBytes()
     * @param resNum 数值
     * @return String.valueOf(resNum).getBytes()，速度更快
     */
    public static byte[] stringValueOfAndGetBytes(int resNum) {
        if (resNum == 0) {
            return new byte[]{ByteCode.ZERO};
        } else if (resNum > 0) {
            byte[] bytes = new byte[(int) (Math.floor(Math.log10(resNum)) + 1)];
            int index = bytes.length - 1;
            while (resNum > 0) {
                bytes[index--] = ByteCode.NUMBER[resNum % 10];
                resNum = resNum / 10;
            }
            return bytes;
        } else {
            resNum = -resNum;
            byte[] bytes = new byte[(int) (Math.floor(Math.log10(resNum)) + 2)];
            int index = bytes.length - 1;
            while (resNum > 0) {
                bytes[index--] = ByteCode.NUMBER[resNum % 10];
                resNum = resNum / 10;
            }
            bytes[0] = ByteCode.MINUS;
            return bytes;
        }
    }

    /**
     * 将 int 类型整数转为其字符串形式的字节数组的长度，等价于 String.valueOf().getBytes().length
     * @param resNum 数值
     * @return String.valueOf(resNum).getBytes().length，速度更快
     */
    public static int byteArrayOfValueLength(int resNum) {
        if (resNum == 0) {
            return 1;
        } else if (resNum > 0) {
            return (int) (Math.floor(Math.log10(resNum)) + 1);
        } else {
            return (int) (Math.floor(Math.log10(resNum)) + 2);
        }
    }

    /**
     * 将 double 类型的数据转为其字符串形式的字节数组，等价于 String.format(...)
     * 拆解成整数 + 小数形式
     * 针对 0-1 范围进行优化
     * @param number 转换的浮点数值
     * @param length 小数部分的长度
     */
    public static byte[] stringValueOfAndGetBytes(double number, int length) {
        if (number == 0) {
            byte[] bytes = new byte[length + 2];
            bytes[0] = ByteCode.ZERO;
            bytes[1] = ByteCode.PERIOD;

            for (int i = 2; i < length + 2; i++) {
                bytes[i] = ByteCode.ZERO;
            }
            return bytes;
        } else if (number > 0) {
            if (number == 1) {
                byte[] bytes = new byte[length + 2];
                bytes[0] = ByteCode.ONE;
                bytes[1] = ByteCode.PERIOD;

                for (int i = 2; i < length + 2; i++) {
                    bytes[i] = ByteCode.ZERO;
                }
                return bytes;
            } else if (number < 1) {
                // 0.xxxxxx
                byte[] bytes = new byte[length + 2];

                bytes[0] = ByteCode.ZERO;
                bytes[1] = ByteCode.PERIOD;

                for (int i = 2; i < length + 2; i++) {
                    number *= 10;
                    bytes[i] = ByteCode.NUMBER[(int) (number % 10)];
                }
                return bytes;
            } else {
                // xxxx.xxxx 先解析整数，再拼接小数
                byte[] intBytes = stringValueOfAndGetBytes((int) number);
                byte[] resBytes = stringValueOfAndGetBytes(number % 1, length);
                byte[] bytes = new byte[intBytes.length + resBytes.length - 1];
                System.arraycopy(intBytes, 0, bytes, 0, intBytes.length);
                System.arraycopy(resBytes, 1, bytes, intBytes.length, resBytes.length - 1);
                return bytes;
            }
        } else {
            // number < 0
            number = -number;
            byte[] value = stringValueOfAndGetBytes(number, length);
            byte[] bytes = new byte[value.length + 1];
            bytes[0] = ByteCode.MINUS;
            System.arraycopy(value, 0, bytes, 1, value.length);
            return bytes;
        }
    }

    /**
     * 判断两个区间是否相交
     * @param lowerBoundA A 区间的下界
     * @param upBoundA A 区间的上界
     * @param lowerBoundB B 区间的下界
     * @param upBoundB B 区间的上界
     * @return 区间 A 和 区间 B 是否相交
     */
    public static boolean intersect(int lowerBoundA, int upBoundA, int lowerBoundB, int upBoundB) {
        return Math.max(lowerBoundA, lowerBoundB) <= Math.min(upBoundA, upBoundB);
    }

    /**
     * 判断两个区间是否相交
     * @param lowerBoundA A 区间的下界
     * @param upBoundA A 区间的上界
     * @param lowerBoundB B 区间的下界
     * @param upBoundB B 区间的上界
     * @return 区间 A 和 区间 B 是否相交
     */
    public static boolean intersect(double lowerBoundA, double upBoundA, double lowerBoundB, double upBoundB) {
        return Math.max(lowerBoundA, lowerBoundB) <= Math.min(upBoundA, upBoundB);
    }

    /**
     * 判断两个区间是否相交
     * @param lowerBoundA A 区间的下界
     * @param upBoundA A 区间的上界
     * @param lowerBoundB B 区间的下界
     * @param upBoundB B 区间的上界
     * @return 区间 A 和 区间 B 是否相交
     */
    public static boolean intersect(float lowerBoundA, float upBoundA, float lowerBoundB, float upBoundB) {
        return Math.max(lowerBoundA, lowerBoundB) <= Math.min(upBoundA, upBoundB);
    }

    /**
     * 两个整数值的汉明距离，定义为两个整数值在位向量中的距离
     * @param x 整数值 1
     * @param y 整数值 2
     * @return 整数值的汉明距离
     */
    public static int hammingDistance(int x, int y) {
        int hamming = x ^ y;
        int cnt = 0;
        while (hamming > 0) {
            hamming = hamming & (hamming - 1);
            cnt++;
        }
        return cnt;
    }

    /**
     * 获取数值的位向量
     * @param code 数值
     * @return 数值 code 对应的位向量码 (每个位置都是 0/1)
     */
    public static byte[] getBitCode(byte code) {
        return getBitCode(code, false);
    }

    public static byte[] getBitCode(byte code, boolean reverse) {
        byte[] bitArray = new byte[8];
        if (reverse) {
            for (int i = 0; i < 8; i++) {
                bitArray[7 - i] = (byte) ((code >> (7 - i)) & 0x1);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                bitArray[7 - i] = (byte) ((code >> i) & 0x1);
            }
        }
        return bitArray;
    }

    /**
     * 获取数值的位向量
     * @param code 数值
     * @return 数值 code 对应的位向量码 (每个位置都是 0/1)
     */
    public static byte[] getBitCode(short code) {
        return getBitCode(code, false);
    }

    /**
     * 获取数值的位向量
     * @param code 数值
     * @param reverse 反转
     * @return 数值 code 对应的位向量码 (每个位置都是 0/1)
     */
    public static byte[] getBitCode(short code, boolean reverse) {
        byte[] bitArray = new byte[16];
        if (reverse) {
            for (int i = 0; i < 16; i++) {
                bitArray[15 - i] = (byte) ((code >> (15 - i)) & 0x1);
            }
        } else {
            for (int i = 0; i < 16; i++) {
                bitArray[15 - i] = (byte) ((code >> i) & 0x1);
            }
        }
        return bitArray;
    }

    /**
     * 获取数值的位向量
     * @param code 数值
     * @return 数值 code 对应的位向量码 (每个位置都是 0/1)
     */
    public static byte[] getBitCode(int code) {
        return getBitCode(code, false);
    }

    /**
     * 获取数值的位向量
     * @param code 数值
     * @param reverse 反转
     * @return 数值 code 对应的位向量码 (每个位置都是 0/1)
     */
    public static byte[] getBitCode(int code, boolean reverse) {
        byte[] bitArray = new byte[32];
        if (reverse) {
            for (int i = 0; i < 32; i++) {
                bitArray[31 - i] = (byte) ((code >> (31 - i)) & 0x1);
            }
        } else {
            for (int i = 0; i < 32; i++) {
                bitArray[31 - i] = (byte) ((code >> i) & 0x1);
            }
        }
        return bitArray;
    }

    /**
     * 将字符串数值格式化为整数
     * @param intValue 字符串整数值
     * @return 将该整数字符串值格式化为整数
     */
    public static int matchInteger(String intValue) {
        return Integer.parseInt(intValue);
    }

    /**
     * 将字符串数值格式化为整数
     * @param longValue 字符串整数值
     * @return 将该整数字符串值格式化为整数
     */
    public static long matchLong(String longValue) {
        return Long.parseLong(longValue);
    }

    /**
     * 将字符串数值格式化为双精度浮点数
     * @param doubleValue 字符串双精度浮点数值
     * @return 将该字符串值格式化为双精度浮点数
     */
    public static double matchDouble(String doubleValue) {
        return Double.parseDouble(doubleValue);
    }

    /**
     * 将字符串数值格式化为单精度浮点数
     * @param floatValue 字符串单精度浮点数
     * @return 将该字符串值格式化为单精度浮点数
     */
    public static float matchFloat(String floatValue) {
        return Float.parseFloat(floatValue);
    }
}
