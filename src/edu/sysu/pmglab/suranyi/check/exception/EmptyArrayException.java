package edu.sysu.pmglab.suranyi.check.exception;

/**
 * @author suranyi
 * @description 空的数组异常
 */

public class EmptyArrayException extends RuntimeException {
    public EmptyArrayException() {
        super();
    }

    public EmptyArrayException(String s) {
        super(s);
    }
}
