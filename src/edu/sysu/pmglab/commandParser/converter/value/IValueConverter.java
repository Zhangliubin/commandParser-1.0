package edu.sysu.pmglab.commandParser.converter.value;

import edu.sysu.pmglab.commandParser.converter.IConverter;

/**
 * @author suranyi
 * @description 单值类型
 */

public interface IValueConverter <T> extends IConverter<T> {

    @Override
    default boolean isArrayType() {
        return false;
    }

    @Override
    default int getDefaultLength() {
        return 1;
    }
}
