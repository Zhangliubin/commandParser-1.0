package edu.sysu.pmglab.suranyi.commandParser.validator;

/**
 * @author suranyi
 * @description 参数验证器接口
 */

public interface IValidator {
    /**
     * 验证
     * @param commandKey 参数类型
     * @param params 验证的参数
     */
    default void validate(String commandKey, Object params) {

    }
}
