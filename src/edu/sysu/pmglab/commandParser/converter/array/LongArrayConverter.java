package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.Array;

/**
 * @author suranyi
 * @description 整数数组转换器
 */

public class LongArrayConverter implements IArrayConverter<long[]> {
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
    public long[] convert(String... params) {
        if (this.separator.length() == 0) {
            long[] converted = new long[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Long.parseLong(params[i]);
            }

            return converted;
        } else {
            Array<Long> converted = new Array<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Long.parseLong(v));
                }
            }

            return converted.toLongArray();
        }
    }

    @Override
    public String toString() {
        return "long-array";
    }
}