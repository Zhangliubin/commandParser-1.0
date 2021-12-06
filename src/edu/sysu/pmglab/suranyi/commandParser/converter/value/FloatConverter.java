package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 浮点数转换器转换器
 */


public abstract class FloatConverter implements IValueConverter<Float> {

    @Override
    public Float convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a float value");
        }

        return Float.parseFloat(params[0]);
    }

    @Override
    public String toString() {
        return "double";
    }
}