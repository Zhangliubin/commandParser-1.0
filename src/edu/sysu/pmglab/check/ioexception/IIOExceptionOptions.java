package edu.sysu.pmglab.check.ioexception;

import java.io.IOException;

/**
 * @author suranyi
 * @description 异常接口
 */

public interface IIOExceptionOptions {
    /**
     * 抛出一个异常
     */
    default void throwException() throws IOException {
        throwException("");
    }

    /**
     * 抛出带异常原因的异常
     * @param reason 异常信息
     */
    void throwException(String reason) throws IOException;
}