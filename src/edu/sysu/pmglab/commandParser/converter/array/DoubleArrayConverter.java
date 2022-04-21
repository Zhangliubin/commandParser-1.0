package edu.sysu.pmglab.commandParser.converter.array;

import edu.sysu.pmglab.container.array.DoubleArray;

/**
 * @author suranyi
 * @description 浮点数组转换器
 */

public class DoubleArrayConverter implements IArrayConverter<Double[]> {
    public final String separator;

    public DoubleArrayConverter() {
        this.separator = "";
    }

    public DoubleArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public Double[] convert(String... params) {
        if (this.separator.length() == 0) {
            Double[] converted = new Double[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Double.parseDouble(params[i]);
            }

            return converted;
        } else {
            DoubleArray converted = new DoubleArray();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Double.parseDouble(v));
                }
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "double-array";
    }
}