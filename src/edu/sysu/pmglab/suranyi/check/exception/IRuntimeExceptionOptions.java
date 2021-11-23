package edu.sysu.pmglab.suranyi.check.exception;

/**
 * @author suranyi
 * @description 异常接口
 */

public interface IRuntimeExceptionOptions {
    /**
     * 抛出一个异常
     */
    default void throwException() {
        throwException("");
    }

    /**
     * 抛出带异常原因的异常
     * @param reason 异常信息
     */
    void throwException(String reason);
}