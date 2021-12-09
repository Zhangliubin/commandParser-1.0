package edu.sysu.pmglab.suranyi.commandParser.converter.map;

import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description double 范围转换器
 */

public class NaturalDoubleRangeConverter implements IMapConverter<double[]> {
    final double VALUE_MIN;
    final double VALUE_MAX;

    public NaturalDoubleRangeConverter() {
        this(0, Double.MAX_VALUE);
    }

    public NaturalDoubleRangeConverter(double MIN, double MAX) {
        if (MIN < 0 || MAX < 0 || MIN > MAX) {
            throw new CommandParserException("illegal parser");
        }

        this.VALUE_MIN = MIN;
        this.VALUE_MAX = MAX;
    }

    @Override
    public double[] convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a double[]");
        }

        return parseRange(params[0]);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    double[] parseRangeStrict(String param) {
        return parseRange(param, true);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    double[] parseRange(String param) {
        return parseRange(param, false);
    }

    /**
     * 转换为 start-end 格式
     */
    double[] parseRange(String param, boolean strict) {
        try {
            double[] parsed;
            String[] groups = param.split("-", -1);

            if (strict) {
                if ((groups.length != 2) || (groups[0].length() == 0) || (groups[1].length() == 0)) {
                    throw new ParameterException(param + " is not in 'start-end' format");
                }

                parsed = new double[2];
                parsed[0] = Double.parseDouble(groups[0]);
                parsed[1] = Double.parseDouble(groups[1]);
            } else {
                if (groups.length != 2) {
                    throw new ParameterException(param + " is not in 'start-end', '-end', 'start-' or '-' format");
                }

                parsed = new double[2];
                parsed[0] = groups[0].length() == 0 ? VALUE_MIN : Double.parseDouble(groups[0]);
                parsed[1] = groups[1].length() == 0 ? VALUE_MAX : Double.parseDouble(groups[1]);
            }

            if ((parsed[0] < VALUE_MIN) || (parsed[0] > VALUE_MAX)) {
                throw new ParameterException("parse '" + param + "' to 'start=" + parsed[0] + ",end=" + parsed[1] + "', but 'start' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
            }

            if ((parsed[1] < VALUE_MIN) || (parsed[1] > VALUE_MAX)) {
                throw new ParameterException("parse '" + param + "' to 'start=" + parsed[0] + ",end=" + parsed[1] + "', but 'end' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
            }

            return parsed;
        } catch (NumberFormatException e) {
            throw new ParameterException(param + " cannot parse to a natural integer range");
        }
    }

    @Override
    public String toString() {
        return "<start>-<end> (double)";
    }
}