package edu.sysu.pmglab.commandParser.validator;

import edu.sysu.pmglab.check.Assert;
import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.BiDict;
import edu.sysu.pmglab.easytools.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author suranyi
 * @description
 */

public class ElementValidator implements IValidator {
    boolean index = true;
    boolean ignoreCase = true;

    final BiDict<String, String> keysBiDict;
    final String[] keys;

    final static Pattern COMMAND_NAME_RULE = Pattern.compile("^[a-zA-Z0-9+_\\-./]+$");

    public ElementValidator(String... keys) {
        Assert.NotEmpty(keys);
        this.keysBiDict = new BiDict<>(keys.length);

        for (int i = 0; i < keys.length; i++) {
            if (!checkKeyName(keys[i])) {
                throw new CommandParserException("invalid syntax: element (" + keys[i] + ") contains invalid characters");
            }

            // 默认是忽略大小写的
            this.keysBiDict.put(String.valueOf(i), keys[i].toLowerCase());
        }

        this.keys = keys;
    }

    public ElementValidator setAllowIndex(boolean enable) {
        this.index = enable;
        return this;
    }

    public ElementValidator setIgnoreCase(boolean enable) {
        if (this.ignoreCase != enable) {
            keysBiDict.clear();

            if (ignoreCase) {
                for (int i = 0; i < keys.length; i++) {
                    this.keysBiDict.put(String.valueOf(i), keys[i].toLowerCase());
                }
            } else {
                for (int i = 0; i < keys.length; i++) {
                    this.keysBiDict.put(String.valueOf(i), keys[i]);
                }
            }

            this.ignoreCase = enable;
        }
        return this;
    }

    @Override
    public void validate(String commandKey, Object params) {
        if (index) {
            // 允许通过索引访问数据
            if (params instanceof String[]) {
                for (String param : (String[]) params) {
                    if (!containIndex(param) && !containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values/indexes are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else if (params instanceof String) {
                if (!containIndex((String) params) && !containValue((String) params)) {
                    throw new ParameterException(commandKey + ": one (or more) of the following values/indexes are supported: " + Arrays.toString(this.keys));
                }
            } else if (params instanceof Collection) {
                for (String param : (Collection<String>) params) {
                    if (!containIndex(param) && !containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values/indexes are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else if (params instanceof Map) {
                for (String param : ((Map<?, String>) params).values()) {
                    if (!containIndex(param) && !containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values/indexes are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else {
                throw new ParameterException(commandKey + ": unable to infer the type of " + commandKey);
            }
        } else {
            // 不允许通过索引访问数据
            if (params instanceof String[]) {
                for (String param : (String[]) params) {
                    if (!containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else if (params instanceof String) {
                if (!containValue((String) params)) {
                    throw new ParameterException(commandKey + ": one (or more) of the following values are supported: " + Arrays.toString(this.keys));
                }
            } else if (params instanceof Collection) {
                for (String param : (Collection<String>) params) {
                    if (!containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else if (params instanceof Map) {
                for (String param : ((Map<?, String>) params).values()) {
                    if (!containValue(param)) {
                        throw new ParameterException(commandKey + ": one (or more) of the following values are supported: " + Arrays.toString(this.keys));
                    }
                }
            } else {
                throw new ParameterException(commandKey + ": unable to infer the type of " + commandKey);
            }
        }
    }

    public boolean containIndex(String key) {
        try {
            int index = Integer.parseInt(key);
            return index >= 0 && index < this.keys.length;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean containValue(String value) {
        if (ignoreCase) {
            return this.keysBiDict.containValue(value.toLowerCase());
        } else {
            return ArrayUtils.contain(this.keys, value);
        }
    }

    /**
     * 获取索引对应的值
     */
    public String valueOf(int index) {
        if (index < keys.length) {
            if (ignoreCase) {
                return this.keysBiDict.valueOf(String.valueOf(index));
            } else {
                return this.keys[index];
            }
        } else {
            return null;
        }
    }

    /**
     * 获取值对应的索引
     */
    public int indexOf(String key) {
        if (ignoreCase) {
            key = key.toLowerCase();
        }

        String index = this.keysBiDict.keyOf(key);
        if (index != null) {
            return Integer.parseInt(index);
        } else {
            return -1;
        }
    }

    boolean checkKeyName(String commandName) {
        return commandName != null && commandName.length() != 0 && COMMAND_NAME_RULE.matcher(commandName).matches();
    }

    @Override
    public String toString() {
        String values = Arrays.toString(keys).replace(" ", "");
        return "ElementOf(" + values.substring(1, values.length() - 1) + ')';
    }
}
