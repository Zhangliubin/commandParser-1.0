package edu.sysu.pmglab.suranyi.commandParser.converter.array;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.container.SmartList;

/**
 * @author suranyi
 * @description 整数数组转换器
 */

public abstract class ShortArrayConverter implements IConverter<short[]> {
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
    public short[] convert(String... params) {
        if (this.separator.length() == 0) {
            short[] converted = new short[params.length];
            for (int i = 0; i < params.length; i++) {
                converted[i] = Short.parseShort(params[i]);
            }

            return converted;
        } else {
            SmartList<Short> converted = new SmartList<>();
            for (String param : params) {
                String[] valuesUnconverted = param.split(this.separator);

                for (String v : valuesUnconverted) {
                    converted.add(Short.parseShort(v));
                }
            }

            return converted.toShortArray();
        }
    }

    @Override
    public int getDefaultLength() {
        return -1;
    }

    @Override
    public String toString() {
        return "short-array";
    }
}