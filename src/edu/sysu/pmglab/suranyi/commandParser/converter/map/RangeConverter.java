package edu.sysu.pmglab.suranyi.commandParser.converter.map;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 范围转换器
 */

public abstract class RangeConverter implements IConverter<String[]> {
    @Override
    public String[] convert(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a String[]");
        }

        return parseRange(params[0]);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    String[] parseRangeStrict(String param) {
        return parseRange(param, true);
    }

    /**
     * 严格转换 (必须是 start-end 形式)
     */
    String[] parseRange(String param) {
        return parseRange(param, false);
    }

    /**
     * 转换为 start-end 格式
     */
    String[] parseRange(String param, boolean strict) {
        String[] parsed = param.split("-", -1);

        if (strict) {
            if ((parsed.length != 2) || (parsed[0].length() == 0) || (parsed[1].length() == 0)) {
                throw new ParameterException(param + " is not in 'start-end' format");
            }
        } else {
            if (parsed.length != 2) {
                throw new ParameterException(param + " is not in 'start-end', '-end', 'start-' or '-' format");
            }
        }

        return parsed;
    }

    @Override
    public String toString() {
        return "<start>-<end> (string)";
    }
}