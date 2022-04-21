package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.array.ShortArray;

/**
 * @author suranyi
 * @description 整数数组转换器
 */

public class ShortArrayConverter implements IArrayConverter<Short[]> {
    public final String separator;

    public ShortArrayConverter() {
        this.separator = "";
    }

    public ShortArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public Short[] convert(String... params) {
        if (this.separator.length() == 0) {
            Short[] converted = new Short[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Short.parseShort(params[i]);
            }

            return converted;
        } else {
            ShortArray converted = new ShortArray();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Short.parseShort(v));
                }
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "short-array";
    }
}