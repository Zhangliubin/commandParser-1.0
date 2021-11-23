package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.container.SmartList;

/**
 * @author suranyi
 * @description 浮点数组转换器
 */

public abstract class DoubleArrayConverter implements IConverter<double[]> {
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
    public double[] convert(String... params) {
        if (this.separator.length() == 0) {
            double[] converted = new double[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Double.parseDouble(params[i]);
            }

            return converted;
        } else {
            SmartList<Double> converted = new SmartList<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Double.parseDouble(v));
                }
            }

            return converted.toDoubleArray();
        }
    }

    @Override
    public int getDefaultLength() {
        return -1;
    }

    @Override
    public String toString() {
        return "double-array";
    }
}