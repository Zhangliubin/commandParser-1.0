package edu.sysu.pmglab.suranyi.easytools;

/**
 * @author suranyi
 * @description 常用字节码
 */

public class ByteCode {
    /**
     * 制表符 \t
     */
    public static final byte TAB = 0x9;

    /**
     * 换行符 \n
     */
    public static final byte NEWLINE = 0xa;

    /**
     * 换行符 \r
     */
    public static final byte CARRIAGE_RETURN = 0xd;

    /**
     * 正斜杠 /
     */
    public static final byte SLASH = 0x2f;

    /**
     * 反斜杠 \
     */
    public static final byte BACKSLASH = 0x5c;

    /**
     * 竖线 |
     */
    public static final byte VERTICAL_BAR = 0x7c;

    /**
     * 逗号 ,
     */
    public static final byte COMMA = 0x2c;

    /**
     * 点 .
     */
    public static final byte PERIOD = 0x2e;

    /**
     * 分号 ;
     */
    public static final byte SEMICOLON = 0x3b;

    /**
     * 等号 =
     */
    public static final byte EQUAL = 0x3d;

    /**
     * 加 +
     */
    public static final byte ADD = 0x2b;

    /**
     * 减 -
     */
    public static final byte MINUS = 0x2d;

    /**
     * 冒号 :
     */
    public static final byte COLON = 0x3a;

    /**
     * 注释符、井号 #
     */
    public static final byte NUMBER_SIGN = 0x23;

    /**
     * 0
     */
    public static final byte ZERO = 0x30;

    /**
     * 1
     */
    public static final byte ONE = 0x31;

    /**
     * 2
     */
    public static final byte TWO = 0x32;

    /**
     * 3
     */
    public static final byte THREE = 0x33;

    /**
     * 4
     */
    public static final byte FOUR = 0x34;

    /**
     * 5
     */
    public static final byte FIVE = 0x35;

    /**
     * 6
     */
    public static final byte SIX = 0x36;

    /**
     * 7
     */
    public static final byte SEVEN = 0x37;

    /**
     * 8
     */
    public static final byte EIGHT = 0x38;

    /**
     * 9
     */
    public static final byte NINE = 0x39;

    /**
     * 0 ~ 9
     */
    public static final byte[] NUMBER = {ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};

    /**
     * chr 字符
     */
    public static final byte[] CHR_STRING = {0x63, 0x68, 0x72};

    /**
     * ##reference=ftp://ftp.1000genomes.ebi.ac.uk//vol1/ftp/technical/reference/phase2_reference_assembly_sequence/hs37d5.fa.gz
     */
    public static final byte[] REFERENCE_STRING = {0x23, 0x23, 0x72, 0x65, 0x66, 0x65, 0x72, 0x65, 0x6e, 0x63, 0x65, 0x3d};

    /**
     * DP 字符
     */
    public static final byte[] DP_STRING = {0x44, 0x50};

    /**
     * GQ 字符
     */
    public static final byte[] GQ_STRING = {0x47, 0x51};

    /**
     * GT 字符
     */
    public static final byte[] GT_STRING = {0x47, 0x54};

    /**
     * AC 字符
     */
    public static final byte[] AC_STRING = {0x41, 0x43, 0x3d};

    /**
     * AN 字符
     */
    public static final byte[] AN_STRING = {0x3b, 0x41, 0x4e, 0x3d};

    /**
     * AF 字符
     */
    public static final byte[] AF_STRING = {0x3b, 0x41, 0x46, 0x3d};

    /**
     * ID 字符
     */
    public static final byte[] ID_STRING = {0x49, 0x44, 0x3d};
}