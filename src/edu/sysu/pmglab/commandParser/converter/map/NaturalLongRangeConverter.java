package edu.sysu.pmglab.commandParser.converter.map;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 整数范围转换器
 */

public class NaturalLongRangeConverter implements IMapConverter<long[]> {
    final long VALUE_MIN;
    final long VALUE_MAX;

    public NaturalLongRangeConverter() {
        this(0, Long.MAX_VALUE);
    }

    public NaturalLongRangeConverter(long MIN, long MAX) {
        if (MIN < 0 || MAX < 0 || MIN > MAX) {
            throw new CommandParserException("illegal parser");
        }

        this.VALUE_MIN = MIN;
        this.VALUE_MAX = MAX;
    }

    @Override
    public long[] convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a long[]");
        }

        return parseRange(params[0]);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    long[] parseRangeStrict(String param) {
        return parseRange(param, true);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    long[] parseRange(String param) {
        return parseRange(param, false);
    }

    /**
     * 转换为 start-end 格式
     */
    long[] parseRange(String param, boolean strict) {
        try {
            long[] parsed;
            String[] groups = param.split("-", -1);

            if (strict) {
                if ((groups.length != 2) || (groups[0].length() == 0) || (groups[1].length() == 0)) {
                    throw new ParameterException(param + " is not in 'start-end' format");
                }

                parsed = new long[2];
                parsed[0] = Long.parseLong(groups[0]);
                parsed[1] = Long.parseLong(groups[1]);
            } else {
                if (groups.length != 2) {
                    throw new ParameterException(param + " is not in 'start-end', '-end', 'start-' or '-' format");
                }

                parsed = new long[2];
                parsed[0] = groups[0].length() == 0 ? VALUE_MIN : Long.parseLong(groups[0]);
                parsed[1] = groups[1].length() == 0 ? VALUE_MAX : Long.parseLong(groups[1]);
            }

            if ((parsed[0] < VALUE_MIN) || (parsed[0] > VALUE_MAX)) {
                throw new ParameterException("parse '" + param + "' to 'start=" + parsed[0] + ",end=" + parsed[1] + "', but 'start' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
            }

            if ((parsed[1] < VALUE_MIN) || (parsed[1] > VALUE_MAX)) {
                throw new ParameterException("parse '" + param + "' to 'start=" + parsed[0] + ",end=" + parsed[1] + "', but 'end' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
            }

            return parsed;
        } catch (NumberFormatException e) {
            throw new ParameterException(param + " cannot parse to a natural long range");
        }
    }

    @Override
    public String toString() {
        return "<start>-<end> (long)";
    }
}