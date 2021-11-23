package edu.sysu.pmglab.suranyi.commandParser.converter;

/**
 * @author suranyi
 * @description 转换器接口
 */

public interface IConverter<T> {
    /**
     * 转换数据类型
     * @param params 参数列表
     * @return 转换结果
     */
    T convert(String... params);

    /**
     * 转换数据类型
     * @param defaultValue 默认值
     * @param params 参数列表
     * @return 转换结果
     */
    default T convert(Object defaultValue, String... params) {
        return convert(params);
    }

    /**
     * 获取参数的默认长度
     * @return 参数长度默认值，为 1
     */
    default int getDefaultLength() {
        return 1;
    }
}