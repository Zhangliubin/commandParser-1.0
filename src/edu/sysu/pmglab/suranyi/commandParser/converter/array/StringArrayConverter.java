package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.container.SmartList;

/**
 * @author suranyi
 * @description 字符串数组转换器
 */

public abstract class StringArrayConverter implements IArrayConverter<String[]> {
    public final String separator;

    public StringArrayConverter() {
        this.separator = "";
    }

    public StringArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public String[] convert(String... params) {
        if (this.separator.length() == 0) {
            return params;
        } else {
            SmartList<String> converted = new SmartList<>();
            for (String param : params) {
                converted.add(param.split(this.separator));
            }

            return converted.toStringArray();
        }
    }

    @Override
    public String toString() {
        return "string-array";
    }
}