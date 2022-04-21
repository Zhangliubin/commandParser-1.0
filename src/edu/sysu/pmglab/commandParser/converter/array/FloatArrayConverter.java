package edu.sysu.pmglab.commandParser.converter.array;
import edu.sysu.pmglab.container.array.FloatArray;

/**
 * @author suranyi
 * @description 浮点数组转换器
 */

public class FloatArrayConverter implements IArrayConverter<Float[]> {
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
    public Float[] convert(String... params) {
        if (this.separator.length() == 0) {
            Float[] converted = new Float[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Float.parseFloat(params[i]);
            }

            return converted;
        } else {
            FloatArray converted = new FloatArray();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Float.parseFloat(v));
                }
            }

            return converted.toArray();
        }
    }

    @Override
    public String toString() {
        return "float-array";
    }
}