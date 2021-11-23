package edu.sysu.pmglab.suranyi.commandParser.validator;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

/**
 * @author suranyi
 * @description 抽象验证器
 */

public class AbstractValidator implements IValidator {
    final String path;

    public AbstractValidator(String path) {
        this.path = path;
    }

    @Override
    public void validate(String commandKey, Object params) {
        throw new ParameterException(path + " points to a custom validator, please use parser.getCommandItem(" + commandKey + ").validateWith($validator) to reset the validator method");
    }

    @Override
    public String toString() {
        return "built-in";
    }
}
