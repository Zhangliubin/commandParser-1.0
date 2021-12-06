package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;

/**
 * @author suranyi
 * @description 数组类型
 */

public interface IArrayConverter<T> extends IConverter<T> {

    @Override
    default boolean isArrayType() {
        return true;
    }

    @Override
    default int getDefaultLength() {
        return -1;
    }
}
