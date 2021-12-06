package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 字符串转换器
 */

public abstract class StringConverter implements IValueConverter<String> {

    @Override
    public String convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a string");
        }

        return params[0];
    }

    @Override
    public String toString() {
        return "string";
    }
}