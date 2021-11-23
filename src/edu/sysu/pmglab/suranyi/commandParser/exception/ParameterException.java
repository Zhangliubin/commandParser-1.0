package edu.sysu.pmglab.suranyi.commandParser.exception;

/**
 * @author suranyi
 * @description 参数异常
 */

public class ParameterException extends RuntimeException {
    public ParameterException() {
        this("");
    }

    public ParameterException(String message) {
        super(message);
    }
}
