package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.array.BooleanArray;

/**
 * @author suranyi
 * @description 布尔数组转换器
 */

public class BooleanArrayConverter implements IArrayConverter<Boolean[]> {
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
    public Boolean[] convert(String... params) {
        if (this.separator.length() == 0) {
            Boolean[] converted = new Boolean[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Boolean.parseBoolean(params[i]);
            }

            return converted;
        } else {
            BooleanArray converted = new BooleanArray();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Boolean.parseBoolean(v));
                }
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "boolean-array";
    }
}