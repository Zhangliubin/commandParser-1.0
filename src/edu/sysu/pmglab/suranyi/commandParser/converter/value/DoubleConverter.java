package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 浮点数转换器转换器
 */


public class DoubleConverter implements IValueConverter<Double> {

    @Override
    public Double convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a double value");
        }

        return Double.parseDouble(params[0]);
    }

    @Override
    public String toString() {
        return "double";
    }
}