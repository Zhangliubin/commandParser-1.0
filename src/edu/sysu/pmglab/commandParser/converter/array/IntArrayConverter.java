package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.Array;

/**
 * @author suranyi
 * @description 整数数组转换器
 */

public class IntArrayConverter implements IArrayConverter<int[]> {
    public final String separator;

    public IntArrayConverter() {
        this.separator = "";
    }

    public IntArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public int[] convert(String... params) {
        if (this.separator.length() == 0) {
            int[] converted = new int[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Integer.parseInt(params[i]);
            }

            return converted;
        } else {
            Array<Integer> converted = new Array<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Integer.parseInt(v));
                }
            }

            return converted.toIntegerArray();
        }
    }

    @Override
    public String toString() {
        return "integer-array";
    }
}