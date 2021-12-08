package edu.sysu.pmglab.suranyi.commandParser.converter.map;

import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 整数范围转换器, 带有索引项
 */

public class NaturalIntRangeWithIndexConverter extends NaturalIntRangeConverter {
    final int INDEX_MIN;
    final int INDEX_MAX;
    final int VALUE_MIN;
    final int VALUE_MAX;

    public NaturalIntRangeWithIndexConverter() {
        this(0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    public NaturalIntRangeWithIndexConverter(int INDEX_MIN, int INDEX_MAX, int VALUE_MIN, int VALUE_MAX) {
        if ((INDEX_MIN < 0 || INDEX_MAX < 0 || INDEX_MIN > INDEX_MAX) || (VALUE_MIN < 0 || VALUE_MAX < 0 || VALUE_MIN > VALUE_MAX)) {
            throw new CommandParserException("illegal parser");
        }

        this.INDEX_MIN = INDEX_MIN;
        this.INDEX_MAX = INDEX_MAX;
        this.VALUE_MIN = VALUE_MIN;
        this.VALUE_MAX = VALUE_MAX;
    }

    @Override
    public int[] convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a int[]");
        }

        return parseRangeWithIndex(params[0]);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    int[] parseRangeWithIndexStrict(String param) {
        return parseRangeWithIndex(param, true);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    int[] parseRangeWithIndex(String param) {
        return parseRangeWithIndex(param, false);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    int[] parseRangeWithIndex(String param, boolean strict) {
        int[] parsed;
        String[] groups = param.split(":", -1);

        if (strict) {
            if ((groups.length != 2) || (groups[0].length() == 0) || (groups[1].length() == 0)) {
                throw new ParameterException(param + " is not in 'index:start-end' format");
            }

            String[] rangeGroups = groups[1].split("-", -1);
            if ((rangeGroups.length != 2) || (rangeGroups[0].length() == 0) || (rangeGroups[1].length() == 0)) {
                throw new ParameterException(param + " is not in 'index:start-end' format");
            }

            parsed = new int[3];
            parsed[0] = Integer.parseInt(groups[0]);
            parsed[1] = Integer.parseInt(rangeGroups[0]);
            parsed[2] = Integer.parseInt(rangeGroups[1]);
        } else {
            if (groups.length == 1) {
                // 没有监测到 :
                parsed = new int[3];
                parsed[0] = Integer.parseInt(groups[0]);
                parsed[1] = VALUE_MIN;
                parsed[2] = VALUE_MAX;
            } else if (groups.length == 2) {
                parsed = new int[3];
                parsed[0] = Integer.parseInt(groups[0]);

                if (groups[1].length() == 0) {
                    // 检测到 :，但后面没有值
                    parsed[1] = VALUE_MIN;
                    parsed[2] = VALUE_MAX;
                } else {
                    int[] parsedRange = parseRange(groups[1], false);
                    parsed[1] = parsedRange[0];
                    parsed[2] = parsedRange[1];
                }

            } else {
                throw new ParameterException(param + " is not in 'index:start-end', 'index:-end', 'index:start-' or 'index' format");
            }
        }

        if ((parsed[0] < INDEX_MIN) || (parsed[0] > INDEX_MAX)) {
            throw new ParameterException("parse '" + param + "' to 'index=" + parsed[0] + ",start=" + parsed[1] + ",end=" + parsed[2] + "', but 'index' is out of range [" + INDEX_MIN + ", " + INDEX_MAX + "]");
        }

        if ((parsed[1] < VALUE_MIN) || (parsed[1] > VALUE_MAX)) {
            throw new ParameterException("parse '" + param + "' to 'index=" + parsed[0] + ",start=" + parsed[1] + ",end=" + parsed[2] + "', but 'start' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
        }

        if ((parsed[2] < VALUE_MIN) || (parsed[2] > VALUE_MAX)) {
            throw new ParameterException("parse '" + param + "' to 'index=" + parsed[0] + ",start=" + parsed[1] + ",end=" + parsed[2] + "', but 'end' is out of range [" + VALUE_MIN + ", " + VALUE_MAX + "]");
        }

        return parsed;
    }

    @Override
    public String toString() {
        return "<index>:<start>-<end> (integer)";
    }
}