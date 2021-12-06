package edu.sysu.pmglab.suranyi.commandParser.converter.map;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 整数范围转换器, 带有索引项
 */

public abstract class RangeWithIndexConverter extends RangeConverter {
    @Override
    public String[] convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a String[]");
        }

        return parseRangeWithIndex(params[0]);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    String[] parseRangeWithIndexStrict(String param) {
        return parseRangeWithIndex(param, true);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    String[] parseRangeWithIndex(String param) {
        return parseRangeWithIndex(param, false);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    String[] parseRangeWithIndex(String param, boolean strict) {
        String[] parsed;
        String[] groups = param.split(":", -1);

        if (strict) {
            if ((groups.length != 2) || (groups[0].length() == 0) || (groups[1].length() == 0)) {
                throw new ParameterException(param + " is not in 'index:start-end' format");
            }

            String[] rangeGroups = parseRange(groups[1], true);
            parsed = new String[3];
            parsed[0] = groups[0];
            parsed[1] = rangeGroups[0];
            parsed[2] = rangeGroups[1];
        } else {
            if (groups.length == 1) {
                // 没有监测到 :
                parsed = new String[3];
                parsed[0] = groups[0];
                parsed[1] = "";
                parsed[2] = "";
            } else if (groups.length == 2) {
                parsed = new String[3];
                parsed[0] = groups[0];

                if (groups[1].length() == 0) {
                    // 检测到 :，但后面没有值
                    parsed[1] = "";
                    parsed[2] = "";
                } else {
                    String[] parsedRange = parseRange(groups[1], false);
                    parsed[1] = parsedRange[0];
                    parsed[2] = parsedRange[1];
                }

            } else {
                throw new ParameterException(param + " is not in 'index:start-end', 'index:-end', 'index:start-' or 'index' format");
            }
        }

        return parsed;
    }

    @Override
    public String toString() {
        return "<index>:<start>-<end> (string)";
    }
}