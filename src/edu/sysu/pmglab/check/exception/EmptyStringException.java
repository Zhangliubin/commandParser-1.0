package edu.sysu.pmglab.check.exception;

/**
 * @author suranyi
 * @description 空的字符串异常
 */

public class EmptyStringException extends RuntimeException {
    public EmptyStringException() {
        super();
    }

    public EmptyStringException(String s) {
        super(s);
    }
}
