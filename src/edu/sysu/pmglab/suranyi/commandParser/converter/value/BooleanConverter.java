package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description bool 对象转换器
 */

public abstract class BooleanConverter implements IConverter<Boolean> {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public Boolean convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a boolean value");
        }

        if (TRUE.equalsIgnoreCase(params[0])) {
            return Boolean.TRUE;
        } else if (FALSE.equalsIgnoreCase(params[0])) {
            return Boolean.FALSE;
        } else {
            throw new ParameterException("couldn't convert " + params[0] + " to a boolean value");
        }
    }

    @Override
    public Boolean convert(Object defaultValue, String... params) {
        if (params.length == 0) {
            if (defaultValue != null) {
                return !(Boolean) defaultValue;
            } else {
                return true;
            }
        } else {
            return convert(params);
        }
    }

    @Override
    public int getDefaultLength() {
        return 1;
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
