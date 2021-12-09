package edu.sysu.pmglab.suranyi.commandParser.converter;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

/**
 * @author suranyi
 * @description 抽象转换器
 */

public class AbstractConverter implements IConverter<Object> {
    final String path;

    public AbstractConverter(String path) {
        this.path = path;
    }

    @Override
    public Object convert(String... params) {
        throw new ParameterException(path + " points to a custom converter, please use parser.getCommandItem($commandName).convertTo($convertor) to reset the convertor method");
    }

    @Override
    public int getDefaultLength() {
        return 1;
    }

    @Override
    public boolean isArrayType() {
        return false;
    }

    @Override
    public String toString() {
        return "built-in";
    }
}
