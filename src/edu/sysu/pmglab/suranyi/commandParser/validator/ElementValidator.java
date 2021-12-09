package edu.sysu.pmglab.suranyi.commandParser.validator;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author suranyi
 * @description
 */

public class ElementValidator implements IValidator {
    final HashSet<String> keys;
    final static Pattern COMMAND_NAME_RULE = Pattern.compile("^[a-zA-Z0-9+_\\-@./]+$");

    private ElementValidator() {
        keys = null;
    }

    public ElementValidator(String... keys) {
        Assert.NotEmpty(keys);
        this.keys = new HashSet<>();

        for (String key : keys) {
            if (!checkKeyName(key)) {
                throw new CommandParserException("invalid syntax: element (" + key + ") contains invalid characters");
            }
            this.keys.add(key);
        }
    }

    @Override
    public void validate(String commandKey, Object params) {
        if (params instanceof String[]) {
            for (String param : (String[]) params) {
                if (!keys.contains(param)) {
                    throw new ParameterException(commandKey + ": one of the following values is supported: " + keys);
                }
            }
        } else if (params instanceof String) {
            if (!keys.contains(((String) params))) {
                throw new ParameterException(commandKey + ": one of the following values is supported: " + keys);
            }
        } else if (params instanceof Collection) {
            for (String param : (Collection<String>) params) {
                if (!keys.contains(param)) {
                    throw new ParameterException(commandKey + ": one of the following values is supported: " + keys);
                }
            }
        } else if (params instanceof Map) {
            for (String param : ((Map<?, String>) params).values()) {
                if (!keys.contains(param)) {
                    throw new ParameterException(commandKey + ": one of the following values is supported: " + keys);
                }
            }
        } else {
            throw new ParameterException(commandKey + ": unable to infer the type of " + commandKey);
        }
    }

    boolean checkKeyName(String commandName) {
        return commandName != null && commandName.length() != 0 && COMMAND_NAME_RULE.matcher(commandName).matches();
    }

    @Override
    public String toString() {
        String values = keys.toString().replace(" ", "");
        return "ElementOf(" + values.substring(1, values.length() - 1) + ')';
    }
}
