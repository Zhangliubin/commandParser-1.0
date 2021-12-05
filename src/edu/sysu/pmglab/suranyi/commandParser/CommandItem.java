package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.commandParser.converter.AbstractConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.array.*;
import edu.sysu.pmglab.suranyi.commandParser.converter.map.*;
import edu.sysu.pmglab.suranyi.commandParser.converter.value.*;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.validator.*;
import edu.sysu.pmglab.suranyi.container.SmartList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author suranyi
 * @description 命令组
 */

public class CommandItem {
    private final String[] commandNames;
    private boolean request = CommandOptions.DEFAULT_REQUEST;
    private boolean help = CommandOptions.DEFAULT_HELP;
    private boolean hide = CommandOptions.DEFAULT_HIDDEN;
    private int length = CommandOptions.DEFAULT_LENGTH;
    private boolean lengthSet = false;
    private Object defaultValue = CommandOptions.DEFAULT_VALUE;
    private int priority;

    private IValidator[] validators = CommandOptions.DEFAULT_VALIDATOR;
    private IConverter converter = CommandOptions.DEFAULT_CONVERTER;
    private boolean converterSet = false;

    private String description = CommandOptions.DEFAULT_DESCRIPTION;
    private String format = CommandOptions.DEFAULT_FORMAT;
    private String optionGroup = CommandOptions.DEFAULT_OPTION_GROUP;

    CommandItem(String... commandNames) {
        if (commandNames == null || commandNames.length == 0) {
            throw new CommandParserException("invalid syntax: commandNames is empty");
        }

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (!CommandOptions.checkCommandName(commandName)) {
                throw new CommandParserException("invalid syntax: commandName (" + commandName + ") contains invalid characters");
            }
        }

        // 设置参数名列表
        this.commandNames = commandNames;
    }

    /**
     * 设置是否为必备参数
     */
    public CommandItem setOptions(CommandOptions... options) {
        HashSet<CommandOptions> optionsHashSet = new HashSet<>(Arrays.asList(options));

        this.request = optionsHashSet.contains(CommandOptions.REQUEST);
        this.help = optionsHashSet.contains(CommandOptions.HELP);
        this.hide = optionsHashSet.contains(CommandOptions.HIDDEN);

        return this;
    }

    /**
     * 添加参数
     *
     * @param options 添加的参数
     */
    public CommandItem addOptions(CommandOptions... options) {
        for (CommandOptions option : options) {
            if (option == CommandOptions.HELP) {
                this.help = true;
            } else if (option == CommandOptions.HIDDEN) {
                this.hide = true;
            } else if (option == CommandOptions.REQUEST) {
                this.request = true;
            }
        }

        return this;
    }

    /**
     * 设置参数长度
     *
     * @param length 参数长度
     */
    public CommandItem arity(int length) {
        if (length >= -1) {
            // -1 指 >= 1 的任意值
            this.length = length;
            this.lengthSet = true;
            return this;
        } else {
            throw new CommandParserException("invalid syntax: arity < 0");
        }
    }

    /**
     * 设置默认值 (根据默认值自动推断参数类型)
     *
     * @param defaultValue 默认值
     */
    public CommandItem defaultTo(Object defaultValue) {
        if (!this.converterSet) {
            boolean converterAutoSet = false;
            // 用户没有设置转换器时，自动推断参数类型
            if (defaultValue instanceof Integer) {
                this.converter = new IntConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof Double) {
                this.converter = new DoubleConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof String) {
                this.converter = new StringConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof Boolean) {
                this.converter = new BooleanConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof Long) {
                this.converter = new LongConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof Short) {
                this.converter = new ShortConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof int[] || defaultValue instanceof Integer[]) {
                this.converter = new IntArrayConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof double[] || defaultValue instanceof Double[]) {
                this.converter = new DoubleArrayConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof long[] || defaultValue instanceof Long[]) {
                this.converter = new LongArrayConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof short[] || defaultValue instanceof Short[]) {
                this.converter = new ShortArrayConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof String[]) {
                this.converter = new StringArrayConverter() {
                };
                converterAutoSet = true;
            }

            if (!this.lengthSet && converterAutoSet) {
                this.length = this.converter.getDefaultLength();
            }
        }

        this.defaultValue = defaultValue;
        return this;
    }

    public CommandItem validateWith(IValidator... validators) {
        if (validators == null || validators.length == 0) {
            this.validators = new IValidator[]{};
        } else {
            for (IValidator validator : validators) {
                if (validator.toString().contains(CommandOptions.SEPARATOR)) {
                    throw new CommandParserException("invalid syntax: validator contains invalid characters");
                }
            }
            this.validators = validators;
        }
        return this;
    }

    /**
     * 设置参数验证器
     *
     * @param MIN 验证最小值
     * @param MAX 验证最大值
     */
    public CommandItem validateWith(int MIN, int MAX) {
        if (!this.converterSet) {
            // 自动推断参数类型
            this.converter = new IntConverter() {
            };
        }

        if (!this.lengthSet) {
            this.length = this.converter.getDefaultLength();
        }

        validateWith(new RangeValidator(MIN, MAX));
        return this;
    }

    /**
     * 设置参数验证器
     *
     * @param MIN 验证最小值
     * @param MAX 验证最大值
     */
    public CommandItem validateWith(double MIN, double MAX) {
        if (!this.converterSet) {
            // 自动推断参数类型
            this.converter = new DoubleConverter() {
            };
        }

        if (!this.lengthSet) {
            this.length = this.converter.getDefaultLength();
        }

        validateWith(new RangeValidator(MIN, MAX));
        return this;
    }

    /**
     * 设置参数转换器
     *
     * @param converter 转换器
     */
    public <T> CommandItem convertTo(IConverter<T> converter) {
        if (converter == null) {
            this.converter = CommandOptions.DEFAULT_CONVERTER;
        } else {
            if (converter.toString().contains(CommandOptions.SEPARATOR)) {
                throw new CommandParserException("invalid syntax: converter contains invalid characters");
            }

            this.converter = converter;
        }

        if (!this.lengthSet) {
            this.length = this.converter.getDefaultLength();
        }

        // 标记为 true，不再进行自动推断
        this.converterSet = true;
        return this;
    }

    /**
     * 设置描述文档
     *
     * @param description 描述信息
     */
    public CommandItem setDescription(String description) {
        if (description == null || description.length() == 0) {
            this.description = CommandOptions.DEFAULT_DESCRIPTION;
        } else {
            if (description.contains(CommandOptions.SEPARATOR)) {
                throw new CommandParserException("invalid syntax: description contains invalid characters");
            }

            this.description = description;
        }
        return this;
    }

    /**
     * 设置参数描述
     *
     * @param description 描述
     * @param format      输入格式
     */
    public CommandItem setDescription(String description, String format) {
        setDescription(description);
        setFormat(format);
        return this;
    }

    /**
     * 设置参数描述
     *
     * @param format 输入格式
     */
    public CommandItem setFormat(String format) {
        if (format == null || format.length() == 0) {
            this.format = CommandOptions.DEFAULT_FORMAT;
        } else {
            if (format.contains(CommandOptions.SEPARATOR)) {
                throw new CommandParserException("invalid syntax: format contains invalid characters");
            }

            this.format = format;
        }
        return this;
    }

    /**
     * 设置参数组
     *
     * @param optionGroup 参数组
     */
    CommandItem setOptionGroup(String optionGroup) {
        if (optionGroup == null || optionGroup.length() == 0) {
            this.optionGroup = CommandOptions.DEFAULT_OPTION_GROUP;
        } else {
            if (optionGroup.contains(CommandOptions.SEPARATOR)) {
                throw new CommandParserException("invalid syntax: optionGroup contains invalid characters");
            }

            this.optionGroup = optionGroup;
        }

        return this;
    }

    /**
     * 获取参数的名字列表
     */
    public String[] getCommandNames() {
        return this.commandNames;
    }

    /**
     * 获取优先级
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * 设置优先级，由上层 Parser 定义
     *
     * @param priority 优先级值
     */
    CommandItem setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 获取第一个参数名
     */
    public String getCommandName() {
        return this.commandNames[0];
    }

    /**
     * 是否为必备参数
     */
    public boolean isRequest() {
        return this.request;
    }

    /**
     * help 模式
     */
    public boolean isHelp() {
        return this.help;
    }

    /**
     * 是否隐藏参数
     */
    public boolean isHide() {
        return this.hide;
    }

    /**
     * 获取转换器
     */
    IConverter getConverter() {
        return converter;
    }

    /**
     * 获取参数描述
     *
     * @return 参数描述信息
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 获取参数描述
     *
     * @return 参数描述信息
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * 获取参数长度
     *
     * @return 参数长度
     */
    public int getLength() {
        return this.length;
    }

    /**
     * 获取默认值
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * 获取参数分组
     */
    public String getOptionGroup() {
        return optionGroup;
    }

    /**
     * 解析值
     */
    Object parseValue(String... params) {
        Object value = this.converter.convert(defaultValue, params);
        for (IValidator validator : this.validators) {
            validator.validate(commandNames[0], value);
        }

        return value;
    }

    @Override
    public String toString() {
        // 将该指令导出为单行格式
        StringBuilder builder = new StringBuilder();
        String commandNames = Arrays.toString(this.commandNames).replace(" ", "");
        builder.append(commandNames, 1, commandNames.length() - 1);
        builder.append("\t");
        builder.append(this.request);
        builder.append("\t");
        builder.append(defaultValue == null ? "." : defaultValue);
        builder.append("\t");
        builder.append(converter);
        builder.append("\t");
        if (this.validators.length == 0) {
            builder.append(".");
        } else {
            builder.append(this.validators[0]);
            if (this.validators.length > 1) {
                for (int i = 1; i < this.validators.length; i++) {
                    builder.append(" ");
                    builder.append(validators[i]);
                }
            }
        }
        builder.append("\t");
        builder.append(this.length);
        builder.append("\t");
        builder.append(this.optionGroup.length() == 0 ? "." : this.optionGroup);
        builder.append("\t");
        builder.append(this.description.length() == 0 ? "." : this.description);
        builder.append("\t");
        builder.append(this.format.length() == 0 ? "." : this.format);
        builder.append("\t");
        builder.append(this.hide);
        builder.append("\t");
        builder.append(this.help);

        return builder.toString();
    }

    Object[] toObject() {
        Object[] row = new Object[11];
        String commandNames = Arrays.toString(getCommandNames()).replace(" ", "");

        row[0] = commandNames.substring(1, commandNames.length() - 1);
        row[1] = request;
        row[2] = this.defaultValue == null ? "." : this.defaultValue;
        row[3] = converter.toString();

        if (validators.length == 0) {
            row[4] = ".";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(this.validators[0]);
            if (this.validators.length > 1) {
                for (int i = 1; i < this.validators.length; i++) {
                    builder.append(" ");
                    builder.append(validators[i]);
                }
            }
            row[4] = builder.toString();
        }

        row[5] = length == -1 ? "≥1" : length;
        row[6] = optionGroup;
        row[7] = description;
        row[8] = format;
        row[9] = hide;
        row[10] = help;

        return row;
    }

    static CommandItem loadFromString(String command) {
        // 将该指令导出为单行格式
        String[] options = command.split(CommandOptions.SEPARATOR, -1);
        if (options.length != 11) {
            throw new CommandParserException("command takes 11 positional argument (separated by \\t, " + options.length + " given)");
        }

        // 检查每一个分割组的信息
        for (String option : options) {
            if (option.length() == 0) {
                throw new CommandParserException("command contains empty values (use '" + CommandOptions.MISS_VALUE + "' as placeholder)");
            }
        }

        CommandItem item = new CommandItem(options[0].split(","));
        if (options[1].equals(CommandOptions.MISS_VALUE) || options[1].equals("false")) {
            item.request = false;
        } else if (options[1].equals("true")) {
            item.request = true;
        } else {
            throw new CommandParserException("couldn't convert " + options[1] + " to a boolean value");
        }

        // 推断转换器及验证器
        if (!options[3].equals(CommandOptions.MISS_VALUE)) {
            switch (options[3]) {
                case "boolean":
                    item.convertTo(new BooleanConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "double":
                    item.convertTo(new DoubleConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "integer":
                    item.convertTo(new IntConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "long":
                    item.convertTo(new LongConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "short":
                    item.convertTo(new ShortConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "string":
                    item.convertTo(new StringConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(options[2]);
                    }
                    break;
                case "passedIn":
                    item.convertTo(new PassedInConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        throw new CommandParserException("passedIn type don't accept the defaultValue, please use '.'");
                    }
                    break;
                case "integer-array":
                    item.convertTo(new IntArrayConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2].split(" ", -1)));
                    }
                    break;
                case "short-array":
                    item.convertTo(new ShortArrayConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2].split(" ", -1)));
                    }
                    break;
                case "string-array":
                    item.convertTo(new StringArrayConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2].split(" ", -1)));
                    }
                    break;
                case "long-array":
                    item.convertTo(new LongArrayConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2].split(" ", -1)));
                    }
                    break;
                case "double-array":
                    item.convertTo(new DoubleArrayConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2].split(" ", -1)));
                    }
                    break;
                case "k1=v1;k2=v2;...":
                    item.convertTo(new KVConverter<String, String>() {
                        @Override
                        public HashMap<String, String> convert(String... params) {
                            return parseKV(params);
                        }
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<start>-<end> (double)":
                    item.convertTo(new NaturalDoubleRangeConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<start>-<end> (integer)":
                    item.convertTo(new NaturalIntRangeConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<index>:<start>-<end> (integer)":
                    item.convertTo(new NaturalIntRangeWithIndexConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<start>-<end> (long)":
                    item.convertTo(new NaturalLongRangeConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<start>-<end> (string)":
                    item.convertTo(new RangeConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                case "<index>:<start>-<end> (string)":
                    item.convertTo(new RangeWithIndexConverter() {
                    });

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(item.converter.convert(options[2]));
                    }
                    break;
                default:
                    // 其他情况需要用户重新配置
                    item.convertTo(new AbstractConverter(options[3]));

                    if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                        item.defaultTo(options[2]);
                    }
                    break;
            }
        } else {
            // 没有传入转换器，此时一律当成 string 处理
            if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                item.defaultTo(options[2]);
            }
        }

        // 设定验证器
        String[] validators = options[4].split(" ", -1);
        if (!options[4].equals(CommandOptions.MISS_VALUE)) {
            SmartList<IValidator> addToItem = new SmartList<>();

            for (String validator : validators) {
                if (validator.startsWith("RangeOf(")) {
                    addToItem.add(new RangeValidator(Double.parseDouble(validator.substring(8, validator.indexOf(","))), Double.parseDouble(validator.substring(validator.indexOf(",") + 1, validator.indexOf(")")))));
                } else if (validator.equals("EnsureFileExists")) {
                    addToItem.add(EnsureFileExistsValidator.INSTANCE);
                } else if (validator.equals("NotDirectory")) {
                    addToItem.add(EnsureFileIsNotDirectoryValidator.INSTANCE);
                } else {
                    addToItem.add(new AbstractValidator(validator));
                }
            }

            item.validateWith(addToItem.toArray(new IValidator[]{}));
        }

        // 替换空值
        options[5] = options[5].replace(" ", "");
        if (!options[5].equals(CommandOptions.MISS_VALUE)) {
            // 如果没有设置长度，则会在上面的转换器设置中设置
            if (options[5].equals(">=1") || options[5].equals("≥1")) {
                item.arity(-1);
            } else {
                item.arity(Integer.parseInt(options[5]));
            }
        }

        if (!options[6].equals(CommandOptions.MISS_VALUE)) {
            item.setOptionGroup(options[6]);
        }

        if (!options[7].equals(CommandOptions.MISS_VALUE)) {
            item.setDescription(options[7]);
        }

        if (!options[8].equals(CommandOptions.MISS_VALUE)) {
            item.setFormat(options[8]);
        }

        if (options[9].equals(CommandOptions.MISS_VALUE) || options[9].equals("false")) {
            item.hide = false;
        } else if (options[9].equals("true")) {
            item.hide = true;
        } else {
            throw new CommandParserException("couldn't convert " + options[9] + " to a boolean value");
        }

        if (options[10].equals(CommandOptions.MISS_VALUE) || options[10].equals("false")) {
            item.help = false;
        } else if (options[10].equals("true")) {
            item.help = true;
        } else {
            throw new CommandParserException("couldn't convert " + options[10] + " to a boolean value");
        }

        return item;
    }
}