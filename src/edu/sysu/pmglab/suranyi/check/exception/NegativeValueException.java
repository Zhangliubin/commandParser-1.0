package edu.sysu.pmglab.suranyi.check.exception;

/**
 * @author suranyi
 * @description 负数异常
 */

public class NegativeValueException extends RuntimeException {
    public NegativeValueException() {
        super();
    }

    public NegativeValueException(String s) {
        super(s);
    }
}
