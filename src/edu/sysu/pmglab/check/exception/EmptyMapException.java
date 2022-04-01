package edu.sysu.pmglab.check.exception;

/**
 * @author suranyi
 * @description 空的容器异常
 */

public class EmptyMapException extends RuntimeException {
    public EmptyMapException() {
        super();
    }

    public EmptyMapException(String s) {
        super(s);
    }
}
