package edu.sysu.pmglab.commandParser.converter.value;

import edu.sysu.pmglab.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 整数转换器转换器
 */

public class ShortConverter implements IValueConverter<Short> {

    @Override
    public Short convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a short value");
        }

        return Short.parseShort(params[0]);
    }

    @Override
    public String toString() {
        return "short";
    }
}