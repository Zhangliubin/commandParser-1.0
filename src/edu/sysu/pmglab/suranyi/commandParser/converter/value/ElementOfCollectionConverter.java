package edu.sysu.pmglab.suranyi.commandParser.converter.value;

import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;
import edu.sysu.pmglab.suranyi.container.SmartList;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author suranyi
 * @description 指定可接受值中的一个的转换器
 */

public abstract class ElementOfCollectionConverter<T> implements IConverter<T> {
    protected HashSet<String> sets = new HashSet<>();

    public ElementOfCollectionConverter(String... supportedKey) {
        this.sets.addAll(Arrays.asList(supportedKey));
    }

    public String catchOne(String... params) {
        if (params.length != 1) {
            throw new ParameterException("unable convert " + Arrays.toString(params) + " to a string value");
        }

        if (sets.contains(params[0])) {
            return params[0];
        } else {
            throw new ParameterException("missing one of " + sets.toString());
        }
    }

    public String[] catchAll(String... params) {
        SmartList<String> supported = new SmartList<>(params);

        for (String param: params) {
            if (sets.contains(param)) {
                supported.add(param);
            }
        }

        return supported.toStringArray();
    }

    @Override
    public String toString() {
        String elements = sets.toString();
        return "elementOf" + elements.substring(1, elements.length() - 1);
    }
}