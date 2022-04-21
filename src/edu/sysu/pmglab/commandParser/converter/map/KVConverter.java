package edu.sysu.pmglab.commandParser.converter.map;

import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author suranyi
 * @description 键值对转换器 (格式: k1=v1;k2=v2 k3=v3)
 */

public abstract class KVConverter<K, V> implements IMapConverter<HashMap<K, V>> {
    /**
     * 捕获的键值对关键字
     */
    public final StringArray KEYWORD;
    public final String separator = ";";

    public KVConverter(String... KEYWORD) {
        if (KEYWORD == null) {
            this.KEYWORD = new StringArray(0, false);
        } else {
            this.KEYWORD = new StringArray(KEYWORD);
        }
    }

    @Override
    public abstract HashMap<K, V> convert(String... params);

    public HashMap<String, String> parseKV(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a hashmap");
        }

        String[] diffGroups = params[0].split(separator);
        HashMap<String, String> converted = new HashMap<>(diffGroups.length);

        // 捕获所有的键值对
        if (KEYWORD.size() == 0) {
            for (String diffGroup : diffGroups) {
                if (diffGroup.length() == 0) {
                    // 空键值对，跳过
                    continue;
                }

                String[] groups = diffGroup.split("=", -1);

                if (groups.length == 1) {
                    // K, 则 V 默认为 null
                    converted.put(groups[0], null);
                } else if (groups.length == 2) {
                    // K=V 形式
                    converted.put(groups[0], groups[1]);
                } else if (groups.length >= 3) {
                    throw new ParameterException(diffGroup + " is not in 'K=V' or 'K' format");
                }
            }
        } else {
            for (String diffGroup : diffGroups) {
                if (diffGroup.length() == 0) {
                    // 空键值对，跳过
                    continue;
                }

                String[] groups = diffGroup.split("=", -1);

                if (groups.length <= 2) {
                    // K, 则 V 默认为 null
                    if (converted.containsKey(groups[0])) {
                        throw new ParameterException(groups[0] + " keyword argument repeated");
                    }

                    if (KEYWORD.contains(groups[0])) {
                        if (groups.length == 1) {
                            converted.put(groups[0], null);
                        } else {
                            converted.put(groups[0], groups[1]);
                        }
                    } else {
                        throw new ParameterException("unsupported key: " + groups[0]);
                    }
                } else {
                    throw new ParameterException(diffGroup + " is not in 'K=V' or 'K' format");
                }
            }

        }
        return converted;
    }

    @Override
    public String toString() {
        return "k1=v1" + separator + "k2=v2" + separator + "...";
    }
}