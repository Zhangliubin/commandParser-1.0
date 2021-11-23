package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 整数转换器转换器
 */

public abstract class IntConverter implements IConverter<Integer> {

    @Override
    public Integer convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a int value");
        }

        return Integer.parseInt(params[0]);
    }

    @Override
    public String toString() {
        return "integer";
    }
}