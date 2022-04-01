package edu.sysu.pmglab.check.exception;

/**
 * @author suranyi
 * @description 空的字符串异常
 */

public class ArgumentOutOfRangeException extends RuntimeException {
    public ArgumentOutOfRangeException() {
        super();
    }

    public ArgumentOutOfRangeException(String s) {
        super(s);
    }
}
