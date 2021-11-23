package edu.sysu.pmglab.suranyi.easytools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author suranyi
 * @description 正则表达式生成器
 */

public enum Regex {
    /**
     * 正整数匹配器 2147483647
     */
    PositiveIntegerRule(generateIntegerValueRule(String.valueOf(Integer.MAX_VALUE), false), "Format: 1 ~ " + Integer.MAX_VALUE),
    NonNegativeIntegerRule(generateIntegerValueRule(String.valueOf(Integer.MAX_VALUE), true), "Format: 0 ~ " + Integer.MAX_VALUE),
    PositiveLongRule(generateIntegerValueRule(String.valueOf(Long.MAX_VALUE), false), "Format: 1 ~ " + Long.MAX_VALUE),
    NonNegativeLongRule(generateIntegerValueRule(String.valueOf(Long.MAX_VALUE), true), "Format: 0 ~ " + Long.MAX_VALUE),
    DoubleRule("(0(?:\\.\\d+)?|1(?:\\.0+)?)", "Format: 0.0 ~ 1.0"),
    AnyCharacterRule(".*");

    private final String regex;
    private final String exception;

    Regex(String regex) {
        this(regex, "Mismatch rules.");
    }

    Regex(String regex, String exception) {
        this.regex = regex;
        this.exception = exception;
    }

    /**
     * 获取规则
     * @return 正则规则的字符串格式
     */
    public String getRule() {
        return this.regex;
    }

    /**
     * 编译
     */
    public Pattern compile() {
        return Pattern.compile(this.regex);
    }

    /**
     * 编译
     */
    public static Pattern compile(String regex) {
        return Pattern.compile(regex);
    }

    /**
     * 匹配
     */
    public Matcher match(String src) {
        Pattern pattern = compile(this.regex);
        Matcher matcher = pattern.matcher(src);
        matcher.matches();
        return matcher;
    }


    /**
     * 匹配
     */
    public static Matcher match(String regex, String src) {
        Pattern pattern = compile(regex);
        Matcher matcher = pattern.matcher(src);
        matcher.matches();
        return matcher;
    }

    /**
     * 抛出异常
     */
    public void throwException() {
        throw new UnsupportedOperationException(this.exception);
    }

    /**
     * 生成正则表达式规则
     * @param maxValue 最大值
     * @return 规则
     */
    public static String generateIntegerValueRule(String maxValue) {
        return generateIntegerValueRule(maxValue, true);
    }

    /**
     * 生成正则表达式规则
     * @param maxValue 最大值
     * @param includeZero 是否包含 0
     * @return 规则
     */
    public static String generateIntegerValueRule(String maxValue, boolean includeZero) {
        StringBuilder builder = new StringBuilder();
        int length = maxValue.length();

        // 一个数字时
        if (length == 1) {
            if (includeZero) {
                if (maxValue.charAt(0) >= 1) {
                    builder.append("([0" + "-" + (maxValue.charAt(0)) + "])");
                } else {
                    builder.append("(0)");
                }
            } else {
                if (maxValue.charAt(0) >= 2) {
                    builder.append("([1" + "-" + (maxValue.charAt(0)) + "])");
                } else {
                    builder.append("(1)");
                }
            }
        } else {
            // 处理次高位
            if (maxValue.charAt(0) == ByteCode.ZERO) {
                throw new UnsupportedOperationException("Not a standard integer.");
            }

            if (includeZero) {
                builder.append("([0-9]|");
            } else {
                builder.append("([1-9]|");
            }

            if (length == 3) {
                builder.append("[1-9][0-9]|");
            } else if (length > 3) {
                builder.append("[1-9][0-9]{1," + (length - 2) + "}|");
            }

            if (maxValue.charAt(0) == ByteCode.TWO) {
                builder.append("1[0-9]");
            } else if (maxValue.charAt(0) > ByteCode.TWO) {
                builder.append("[1-" + (maxValue.charAt(0) - 49) + "][0-9]");
            }

            if (length - 1 > 1) {
                builder.append("{" + (length - 1) + "}");
            }

            // 继续往后处理
            for (int i = 1; i < length - 1; i++) {
                if (length - i - 1 > 1) {
                    if (maxValue.charAt(i) == ByteCode.TWO) {
                        builder.append("|");
                        builder.append(maxValue, 0, i);
                        builder.append("1[0-9]{" + (length - i - 1) + "}");
                    } else if (maxValue.charAt(i) > ByteCode.TWO) {
                        builder.append("|");
                        builder.append(maxValue, 0, i);
                        builder.append("[1-" + (maxValue.charAt(i) - 49) + "][0-9]{" + (length - i - 1) + "}");
                    }
                } else {
                    if (maxValue.charAt(i) == ByteCode.TWO) {
                        builder.append("|");
                        builder.append(maxValue, 0, i);
                        builder.append("1[0-9]");
                    } else if (maxValue.charAt(i) > ByteCode.TWO) {
                        builder.append("|");
                        builder.append(maxValue, 0, i);
                        builder.append("[1-" + (maxValue.charAt(i) - 49) + "][0-9]");
                    }
                }
            }

            // 处理最后一个数据组
            if (maxValue.charAt(length - 1) == ByteCode.ZERO) {
                builder.append("|").append(maxValue, 0, length - 1).append(0).append(")");
            } else {
                builder.append("|").append(maxValue, 0, length - 1).append("[0-" + maxValue.charAt(length - 1) + "])");
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return this.regex;
    }
}