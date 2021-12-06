package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;

/**
 * @author suranyi
 * @description 数值类型
 */

public interface IValueConverter <T> extends IConverter<T> {

    @Override
    default boolean isArrayType() {
        return false;
    }
}
