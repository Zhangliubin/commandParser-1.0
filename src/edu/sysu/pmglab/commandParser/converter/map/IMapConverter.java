package edu.sysu.pmglab.commandParser.converter.map;

import edu.sysu.pmglab.commandParser.converter.IConverter;

/**
 * @author suranyi
 * @description 映射类型
 */

public interface IMapConverter <T> extends IConverter<T> {
    @Override
    default int getDefaultLength() {
        return 1;
    }

    @Override
    default boolean isArrayType() {
        return false;
    }
}
