package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;

/**
 * @author suranyi
 * @description 参数是否被传入转换器 (只要被调用了 convert，就一定被传入了)
 */

public abstract class PassedInConverter implements IConverter<Boolean> {
    @Override
    public Boolean convert(String... params) {
        return true;
    }

    @Override
    public Boolean convert(Object defaultValue, String... params) {
        return true;
    }

    @Override
    public int getDefaultLength() {
        return 0;
    }

    @Override
    public String toString() {
        return "passedIn";
    }
}
