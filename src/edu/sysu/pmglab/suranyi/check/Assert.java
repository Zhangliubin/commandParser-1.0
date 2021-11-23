package edu.sysu.pmglab.suranyi.check;

import edu.sysu.pmglab.suranyi.check.exception.IRuntimeExceptionOptions;
import edu.sysu.pmglab.suranyi.check.ioexception.IIOExceptionOptions;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static edu.sysu.pmglab.suranyi.check.exception.RuntimeExceptionOptions.*;

/**
 * @author suranyi
 * @description 断言类
 */

public final class Assert {
    private Assert() {
    }

    /**
     * 断言
     * @param condition 条件
     */
    public static boolean that(boolean condition) {
        return that(condition, AssertionError);
    }

    /**
     * 断言
     * @param condition 条件
     * @param reason 错误原因
     */
    public static boolean that(boolean condition, String reason) {
        return that(condition, AssertionError, reason);
    }

    /**
     * 断言
     * @param condition 条件
     * @param exception 异常实例
     */
    public static boolean that(boolean condition, RuntimeException exception) {
        if (!condition) {
            throw exception;
        }

        return true;
    }

    /**
     * 断言
     * @param condition 条件
     * @param exception IO 异常实例
     */
    public static boolean that(boolean condition, IOException exception) throws IOException {
        if (!condition) {
            throw exception;
        }

        return true;
    }

    /**
     * 断言
     * @param condition 条件
     * @param option 异常类型
     */
    public static boolean that(boolean condition, IRuntimeExceptionOptions option) {
        return that(condition, option, "");
    }

    /**
     * 检验参数
     * @param condition 条件
     * @param option 异常类型
     * @param reason 异常原因
     */
    public static boolean that(boolean condition, IRuntimeExceptionOptions option, String reason) {
        if (!condition) {
            option.throwException(reason);
        }

        return true;
    }

    /**
     * 检验参数
     * @param condition 条件
     * @param option IO 异常类型
     */
    public static boolean that(boolean condition, IIOExceptionOptions option) throws IOException {
        return that(condition, option, "");
    }

    /**
     * 检验参数
     * @param condition 条件
     * @param option IO 异常类型
     * @param reason 异常原因
     */
    public static boolean that(boolean condition, IIOExceptionOptions option, String reason) throws IOException {
        if (!condition) {
            option.throwException(reason);
        }

        assert condition;
        return true;
    }

    /**
     * 抛出异常
     * @param option IO 异常类型
     * @param reason 异常原因
     */
    public static void throwException(IRuntimeExceptionOptions option, String reason) {
        option.throwException(reason);
    }

    /**
     * 抛出异常
     * @param option IO 异常类型
     * @param reason 异常原因
     */
    public static void throwException(IIOExceptionOptions option, String reason) throws IOException {
        option.throwException(reason);
    }

    /**
     * 检查是否为 null
     */
    public static <T> T NotNull(T obj) {
        that(obj != null, NullPointerException);
        return obj;
    }

    /**
     * 检查是否为 null 或值为空
     */
    public static <T> T[] NotEmpty(T[] obj) {
        boolean condition = obj != null && obj.length > 0;
        that(condition, EmptyArrayException);
        return obj;
    }

    /**
     * 检查是否为 null 或值为空
     */
    public static <E> Collection<E> NotEmpty(Collection<E> obj) {
        boolean condition = obj != null && obj.size() > 0;
        that(condition, EmptyCollectionException);
        return obj;
    }

    /**
     * 检查是否为 null 或值为空
     */
    public static String NotEmpty(String obj) {
        boolean condition = obj != null && obj.length() > 0;
        that(condition, EmptyStringException);
        return obj;
    }

    /**
     * 检查是否为 null 或值为空
     */
    public static <K, V> Map<K, V> NotEmpty(Map<K, V> obj) {
        boolean condition = obj != null && obj.size() > 0;
        that(condition, EmptyMapException);
        return obj;
    }

    /**
     * 检查是否为负数
     */
    public static byte NotNegativeValue(byte value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 检查是否为负数
     */
    public static short NotNegativeValue(short value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 检查是否为负数
     */
    public static int NotNegativeValue(int value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 检查是否为负数
     */
    public static long NotNegativeValue(long value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 检查是否为负数
     */
    public static float NotNegativeValue(float value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 检查是否为负数
     */
    public static double NotNegativeValue(double value) {
        that(value >= 0, NegativeValueException, "value cannot be negative");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static byte valueRange(byte value, byte minValue, byte maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static short valueRange(short value, short minValue, short maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static int valueRange(int value, int minValue, int maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static long valueRange(long value, long minValue, long maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static float valueRange(float value, float minValue, float maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param value 校验值
     * @param minValue 值上界
     * @param maxValue 值下界
     */
    public static double valueRange(double value, double minValue, double maxValue) {
        boolean condition = value >= minValue && value <= maxValue;
        that(condition, ArgumentOutOfRangeException, "value out of range [" + minValue + ", " + maxValue + "]");
        return value;
    }

    /**
     * 参数边界校验方法
     * @param index 校验值
     * @param offset 最小索引
     * @param length 长度
     */
    public static int arrayIndex(int index, int offset, int length) {
        boolean condition = (index >= offset) && (index < offset + length);
        that(condition, ArrayIndexOutOfBoundsException, "arrayIndex out of range [" + offset + ", " + (offset + length - 1) + "]");
        return index;
    }
}

