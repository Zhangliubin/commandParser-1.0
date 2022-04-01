package edu.sysu.pmglab.check.exception;

/**
 * @author suranyi
 * @description 空的容器异常
 */

public class EmptyCollectionException extends RuntimeException {
    public EmptyCollectionException() {
        super();
    }

    public EmptyCollectionException(String s) {
        super(s);
    }
}
