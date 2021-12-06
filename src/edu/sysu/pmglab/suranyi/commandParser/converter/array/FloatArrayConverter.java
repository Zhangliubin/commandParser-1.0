package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.container.SmartList;

/**
 * @author suranyi
 * @description 浮点数组转换器
 */

public abstract class FloatArrayConverter implements IArrayConverter<float[]> {
    public final String separator;

    public FloatArrayConverter() {
        this.separator = "";
    }

    public FloatArrayConverter(String separator) {
        if (separator == null) {
            this.separator = "";
        } else {
            this.separator = separator;
        }
    }

    @Override
    public float[] convert(String... params) {
        if (this.separator.length() == 0) {
            float[] converted = new float[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Float.parseFloat(params[i]);
            }

            return converted;
        } else {
            SmartList<Float> converted = new SmartList<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Float.parseFloat(v));
                }
            }

            return converted.toFloatArray();
        }
    }

    @Override
    public String toString() {
        return "float-array";
    }
}