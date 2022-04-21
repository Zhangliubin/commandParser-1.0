package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.array.StringArray;

/**
 * @author suranyi
 * @description 字符串数组转换器
 */

public class StringArrayConverter implements IArrayConverter<String[]> {
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
            StringArray converted = new StringArray();
            for (String param : params) {
                converted.addAll(param.split(this.separator));
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "string-array";
    }
}