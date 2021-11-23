package edu.sysu.pmglab.suranyi.commandParser.validator;

import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

/**
 * @author suranyi
 * @description 浮点数范围验证器
 */

public class DoubleValidator implements IValidator {
    double MIN;
    double MAX;

    public DoubleValidator(double MIN, double MAX) {
        this.MIN = MIN;
        this.MAX = MAX;

        if ((MIN > MAX)) {
            throw new CommandParserException("illegal parser");
        }
    }

    @Override
    public void validate(String commandKey, Object params) {
        if (params instanceof double[]) {
            for (double value : (double[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof Double) {
            double passedInValue = (Double) params;
            if (passedInValue < MIN || passedInValue > MAX) {
                throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
            }
        } else {
            throw new ParameterException("unable to infer the type of " + commandKey);
        }
    }

    @Override
    public String toString() {
        return "RangeOf(" + MIN + "," + MAX + ")";
    }
}