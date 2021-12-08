package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.container.SmartList;

/**
 * @author suranyi
 * @description 布尔数组转换器
 */

public class BooleanArrayConverter implements IArrayConverter<boolean[]> {
    public final String separator;

    public BooleanArrayConverter() {
        this.separator = "";
    }

    public BooleanArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public boolean[] convert(String... params) {
        if (this.separator.length() == 0) {
            boolean[] converted = new boolean[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Boolean.parseBoolean(params[i]);
            }

            return converted;
        } else {
            SmartList<Boolean> converted = new SmartList<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Boolean.parseBoolean(v));
                }
            }

            return converted.toBooleanArray();
        }
    }

    @Override
    public String toString() {
        return "boolean-array";
    }
}