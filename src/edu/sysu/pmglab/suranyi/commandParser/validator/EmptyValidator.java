package edu.sysu.pmglab.suranyi.commandParser.validator;

/**
 * @author suranyi
 * @description 空验证器
 */

public enum EmptyValidator implements IValidator {
    /**
     * 单例
     */
    INSTANCE;

    @Override
    public void validate(String commandKey, Object params) {

    }
}