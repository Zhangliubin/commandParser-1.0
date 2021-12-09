package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;

/**
 * @author suranyi
 * @description 参数是否被传入转换器 (只要被调用了 convert，就一定被传入了)
 */

public class PassedInConverter implements IConverter<Boolean> {
    @Override
    public Boolean convert(String... params) {
        if (params.length != 0) {
            throw new CommandParserException("passedIn type don't accept any values");
        }

        return true;
    }

    @Override
    public Boolean convert(Object defaultValue, String... params) {
        if (defaultValue != null) {
            throw new CommandParserException("passedIn type don't accept the defaultValue, please use '.'");
        }

        if (params.length != 0) {
            throw new CommandParserException("passedIn type don't accept any values");
        }

        return true;
    }

    @Override
    public int getDefaultLength() {
        return 0;
    }

    @Override
    public boolean isArrayType() {
        return false;
    }

    @Override
    public String toString() {
        return "passedIn";
    }
}
