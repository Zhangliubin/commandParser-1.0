package edu.sysu.pmglab.suranyi.commandParser.validator;

import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

/**
 * @author suranyi
 * @description 范围验证器
 */

public class RangeValidator implements IValidator {
    double MIN;
    double MAX;

    public RangeValidator(float MIN, float MAX) {
        this((long) MIN, (long) MAX);
    }

    public RangeValidator(double MIN, double MAX) {
        this((long) MIN, (long) MAX);
    }

    public RangeValidator(short MIN, short MAX) {
        this((long) MIN, (long) MAX);
    }

    public RangeValidator(int MIN, int MAX) {
        this((long) MIN, (long) MAX);
    }

    public RangeValidator(long MIN, long MAX) {
        this.MIN = MIN;
        this.MAX = MAX;

        if ((MIN > MAX)) {
            throw new CommandParserException("illegal parser");
        }
    }

    @Override
    public void validate(String commandKey, Object params) {
        if (params instanceof int[]) {
            for (int value : (int[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof short[]) {
            for (short value : (short[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof long[]) {
            for (long value : (long[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof double[]) {
            for (double value : (double[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof float[]) {
            for (float value : (float[]) params) {
                if (value < MIN || value > MAX) {
                    throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
                }
            }
        } else if (params instanceof Integer) {
            int passedInValue = (Integer) params;
            if (passedInValue < MIN || passedInValue > MAX) {
                throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
            }
        } else if (params instanceof Short) {
            short passedInValue = (Short) params;
            if (passedInValue < MIN || passedInValue > MAX) {
                throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
            }
        } else if (params instanceof Long) {
            long passedInValue = (Long) params;
            if (passedInValue < MIN || passedInValue > MAX) {
                throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
            }
        } else if (params instanceof Double) {
            double passedInValue = (Double) params;
            if (passedInValue < MIN || passedInValue > MAX) {
                throw new ParameterException(commandKey + " is out of range [" + MIN + ", " + MAX + "]");
            }
        } else if (params instanceof Float) {
            float passedInValue = (Float) params;
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