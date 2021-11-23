package edu.sysu.pmglab.suranyi.check;

/**
 * @author suranyi
 * @description
 */

public class Value {
    private Value() {
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static byte of(byte value, byte lowerBound, byte upperBound) {
        return (byte) Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static short of(short value, short lowerBound, short upperBound) {
        return (short) Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static int of(int value, int lowerBound, int upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static long of(long value, long lowerBound, long upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static double of(double value, double lowerBound, double upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * 将值 value 约束在 [lowerBound, upperBound] 范围内
     * @param value 原数值
     * @param lowerBound 约束下界
     * @param upperBound 约束上界
     * @return 约束后的值
     */
    public static float of(float value, float lowerBound, float upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }
}