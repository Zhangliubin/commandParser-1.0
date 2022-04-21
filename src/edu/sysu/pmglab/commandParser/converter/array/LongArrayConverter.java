package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.array.LongArray;

/**
 * @author suranyi
 * @description 整数数组转换器
 */

public class LongArrayConverter implements IArrayConverter<Long[]> {
    public final String separator;

    public LongArrayConverter() {
        this.separator = "";
    }

    public LongArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public Long[] convert(String... params) {
        if (this.separator.length() == 0) {
            Long[] converted = new Long[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Long.parseLong(params[i]);
            }

            return converted;
        } else {
            LongArray converted = new LongArray();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Long.parseLong(v));
                }
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "long-array";
    }
}