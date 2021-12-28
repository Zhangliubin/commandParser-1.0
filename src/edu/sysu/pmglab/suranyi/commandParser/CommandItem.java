package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.commandParser.converter.AbstractConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.IConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.array.*;
import edu.sysu.pmglab.suranyi.commandParser.converter.map.*;
import edu.sysu.pmglab.suranyi.commandParser.converter.value.*;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.validator.*;
import edu.sysu.pmglab.suranyi.container.SmartList;

import java.util.*;

/**
 * @author suranyi
 * @description 命令组
 */

public class CommandItem {
    private final String[] commandNames;
    private boolean request = CommandOptions.DEFAULT_REQUEST;
    private boolean help = CommandOptions.DEFAULT_HELP;
    private boolean hide = CommandOptions.DEFAULT_HIDDEN;
    private boolean debug = CommandOptions.DEFAULT_DEBUG;
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
        this.debug = optionsHashSet.contains(CommandOptions.DEBUG);

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
            } else if (option == CommandOptions.DEBUG) {
                this.debug = true;
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
            } else if (defaultValue instanceof Float) {
                this.converter = new FloatConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof Double) {
                this.converter = new DoubleConverter() {
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
            } else if (defaultValue instanceof float[] || defaultValue instanceof Float[]) {
                this.converter = new FloatArrayConverter() {
                };
                converterAutoSet = true;
            } else if (defaultValue instanceof boolean[] || defaultValue instanceof Boolean[]) {
                this.converter = new BooleanArrayConverter() {
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
     * 是否为调试参数
     */
    public boolean isDebug() {
        return this.debug;
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
        if (this.defaultValue == null || this.converter instanceof PassedInConverter) {
            builder.append(".");
        } else {
            if (this.converter instanceof StringArrayConverter && this.defaultValue instanceof String[]) {
                String value = Arrays.toString(((String[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof BooleanArrayConverter && this.defaultValue instanceof boolean[]) {
                String value = Arrays.toString(((boolean[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof ShortArrayConverter && this.defaultValue instanceof short[]) {
                String value = Arrays.toString(((short[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof IntArrayConverter && this.defaultValue instanceof int[]) {
                String value = Arrays.toString(((int[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof LongArrayConverter && this.defaultValue instanceof long[]) {
                String value = Arrays.toString(((long[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof FloatArrayConverter && this.defaultValue instanceof float[]) {
                String value = Arrays.toString(((float[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.converter instanceof DoubleArrayConverter && this.defaultValue instanceof double[]) {
                String value = Arrays.toString(((double[]) this.defaultValue)).replace(" ", "");
                builder.append(value, 1, value.length() - 1);
            } else if (this.defaultValue instanceof Boolean || this.defaultValue instanceof Integer || this.defaultValue instanceof String ||
                    this.defaultValue instanceof Short || this.defaultValue instanceof Long || this.defaultValue instanceof Float || this.defaultValue instanceof Double) {
                builder.append(this.defaultValue);
            } else if (this.converter instanceof NaturalLongRangeConverter && this.defaultValue instanceof long[] && ((long[]) this.defaultValue).length == 2) {
                builder.append(((long[]) this.defaultValue)[0] + "-" + ((long[]) this.defaultValue)[1]);
            } else if (this.converter instanceof NaturalIntRangeWithIndexConverter && this.defaultValue instanceof int[] && ((int[]) this.defaultValue).length == 3) {
                builder.append((((int[]) this.defaultValue)[0] + ":" + ((int[]) this.defaultValue)[1]) + ((int[]) this.defaultValue)[2]);
            } else if (this.converter instanceof NaturalIntRangeConverter && this.defaultValue instanceof int[] && ((int[]) this.defaultValue).length == 2) {
                builder.append(((int[]) this.defaultValue)[0] + "-" + ((int[]) this.defaultValue)[1]);
            } else if (this.converter instanceof NaturalDoubleRangeConverter && this.defaultValue instanceof double[] && ((double[]) this.defaultValue).length == 2) {
                builder.append(((double[]) this.defaultValue)[0] + "-" + ((double[]) this.defaultValue)[1]);
            } else if (this.converter instanceof RangeConverter && this.defaultValue instanceof String[] && ((String[]) this.defaultValue).length == 2) {
                builder.append(((String[]) this.defaultValue)[0] + "-" + ((String[]) this.defaultValue)[1]);
            } else if (this.converter instanceof RangeWithIndexConverter && this.defaultValue instanceof String[] && ((String[]) this.defaultValue).length == 3) {
                builder.append((((String[]) this.defaultValue)[0] + ":" + ((String[]) this.defaultValue)[1]) + ((String[]) this.defaultValue)[2]);
            } else if (this.converter instanceof KVConverter && this.defaultValue instanceof HashMap) {
                String value = this.defaultValue.toString().replace(" ", "").replace(",", ";");
                builder.append(value, 1, value.length() - 1);
            } else {
                builder.append(this.defaultValue);
            }
        }
        builder.append("\t");
        builder.append(converter);
        builder.append("\t");
        if (this.validators.length == 0) {
            builder.append(".");
        } else {
            builder.append(this.validators[0]);
            if (this.validators.length > 1) {
                for (int i = 1; i < this.validators.length; i++) {
                    builder.append(";");
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
        builder.append("\t");
        builder.append(this.debug);

        return builder.toString();
    }

    Object[] toObject() {
        Object[] row = new Object[12];
        String commandNames = Arrays.toString(getCommandNames()).replace(" ", "");

        row[0] = commandNames.substring(1, commandNames.length() - 1);
        row[1] = request;

        if (this.defaultValue == null || this.converter instanceof PassedInConverter) {
            row[2] = ".";
        } else {
            if (this.converter instanceof StringArrayConverter && this.defaultValue instanceof String[]) {
                String value = Arrays.toString(((String[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof BooleanArrayConverter && this.defaultValue instanceof boolean[]) {
                String value = Arrays.toString(((boolean[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof ShortArrayConverter && this.defaultValue instanceof short[]) {
                String value = Arrays.toString(((short[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof IntArrayConverter && this.defaultValue instanceof int[]) {
                String value = Arrays.toString(((int[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof LongArrayConverter && this.defaultValue instanceof long[]) {
                String value = Arrays.toString(((long[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof FloatArrayConverter && this.defaultValue instanceof float[]) {
                String value = Arrays.toString(((float[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.converter instanceof DoubleArrayConverter && this.defaultValue instanceof double[]) {
                String value = Arrays.toString(((double[]) this.defaultValue)).replace(" ", "");
                row[2] = value.substring(1, value.length() - 1);
            } else if (this.defaultValue instanceof Boolean || this.defaultValue instanceof Integer || this.defaultValue instanceof String ||
                    this.defaultValue instanceof Short || this.defaultValue instanceof Long || this.defaultValue instanceof Float || this.defaultValue instanceof Double) {
                row[2] = this.defaultValue.toString();
            } else if (this.converter instanceof NaturalLongRangeConverter && this.defaultValue instanceof long[] && ((long[]) this.defaultValue).length == 2) {
                String value = Arrays.toString(((long[]) this.defaultValue)).replace(" ", "");
                row[2] = value;
            } else if (this.converter instanceof NaturalIntRangeWithIndexConverter && this.defaultValue instanceof int[] && ((int[]) this.defaultValue).length == 3) {
                int[] converted = (int[]) this.defaultValue;
                row[2] = converted[0] + ":" + converted[1] + "-" + converted[2];
            } else if (this.converter instanceof NaturalIntRangeConverter && this.defaultValue instanceof int[] && ((int[]) this.defaultValue).length == 2) {
                int[] converted = (int[]) this.defaultValue;
                row[2] = converted[0] + "-" + converted[1];
            } else if (this.converter instanceof NaturalDoubleRangeConverter && this.defaultValue instanceof double[] && ((double[]) this.defaultValue).length == 2) {
                double[] converted = (double[]) this.defaultValue;
                row[2] = converted[0] + "-" + converted[1];
            } else if (this.converter instanceof RangeConverter && this.defaultValue instanceof String[] && ((String[]) this.defaultValue).length == 2) {
                String[] converted = (String[]) this.defaultValue;
                row[2] = converted[0] + "-" + converted[1];
            } else if (this.converter instanceof RangeWithIndexConverter && this.defaultValue instanceof String[] && ((String[]) this.defaultValue).length == 3) {
                String[] converted = (String[]) this.defaultValue;
                row[2] = converted[0] + ":" + converted[1] + "-" + converted[2];
            } else if (this.converter instanceof KVConverter && this.defaultValue instanceof HashMap) {
                String value = this.defaultValue.toString().replace(" ", "").replace(",", ";");
                row[2] = value.substring(1, value.length() - 1);
            } else {
                row[2] = this.defaultValue.toString();
            }
        }

        row[3] = converter.toString();

        if (validators.length == 0) {
            row[4] = ".";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(this.validators[0]);
            if (this.validators.length > 1) {
                for (int i = 1; i < this.validators.length; i++) {
                    builder.append(";");
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
        row[11] = debug;

        return row;
    }

    static CommandItem loadFromString(String command) {
        // 将该指令导出为单行格式
        String[] options = command.split(CommandOptions.SEPARATOR, -1);

        // 解析命令
        CommandItem item = new CommandItem(options[0].split(","));

        // 检查每一个分割组的信息，不能为空白
        for (String option : options) {
            if (option.length() == 0) {
                throw new CommandParserException(item.getCommandName() + ": contains empty values, please use '" + CommandOptions.MISS_VALUE + "' as placeholder");
            }
        }

        // 检验长度是否为 12
        if (options.length != 12) {
            throw new CommandParserException(item.getCommandName() + ": command takes 11 positional argument (separated by \\t, " + options.length + " given)");
        }

        // 是否为 . 或布尔值
        if (options[1].equals(CommandOptions.MISS_VALUE) || options[1].equalsIgnoreCase("false")) {
            item.request = false;
        } else if (options[1].equalsIgnoreCase("true")) {
            item.request = true;
        } else {
            throw new CommandParserException(item.getCommandName() + ": couldn't convert " + options[1] + " to a boolean value (true/false)");
        }

        if (options[9].equals(CommandOptions.MISS_VALUE) || options[9].equalsIgnoreCase("false")) {
            item.hide = false;
        } else if (options[9].equalsIgnoreCase("true")) {
            item.hide = true;
        } else {
            throw new CommandParserException(item.getCommandName() + ": couldn't convert " + options[9] + " to a boolean value (true/false)");
        }

        if (options[10].equals(CommandOptions.MISS_VALUE) || options[10].equalsIgnoreCase("false")) {
            item.help = false;
        } else if (options[10].equalsIgnoreCase("true")) {
            item.help = true;
        } else {
            throw new CommandParserException(item.getCommandName() + ": couldn't convert " + options[10] + " to a boolean value (true/false)");
        }

        if (options[11].equals(CommandOptions.MISS_VALUE) || options[11].equalsIgnoreCase("false")) {
            item.debug = false;
        } else if (options[11].equalsIgnoreCase("true")) {
            item.debug = true;
        } else {
            throw new CommandParserException(item.getCommandName() + ": couldn't convert " + options[11] + " to a boolean value (true/false)");
        }

        // 推断转换器及验证器
        switch (options[3]) {
            case "boolean":
                item.convertTo(new BooleanConverter() {
                });
                break;
            case "short":
                item.convertTo(new ShortConverter() {
                });
                break;
            case "integer":
                item.convertTo(new IntConverter() {
                });
                break;
            case "long":
                item.convertTo(new LongConverter() {
                });
                break;
            case "float":
                item.convertTo(new FloatConverter() {
                });
                break;
            case "double":
                item.convertTo(new DoubleConverter() {
                });
                break;
            case "string":
                item.convertTo(new StringConverter() {
                });
                break;
            case CommandOptions.MISS_VALUE:
                // 未指定转换器时结合其他信息推断
                if (options[5].equals("0")) {
                    item.convertTo(new PassedInConverter());
                } else if (options[5].equals("1") || options[5].equals(CommandOptions.MISS_VALUE)) {
                    item.convertTo(new StringConverter());
                } else {
                    item.convertTo(new StringArrayConverter());
                }
                break;
            case "passedIn":
                item.convertTo(new PassedInConverter() {
                });
                break;
            case "boolean-array":
                item.convertTo(new BooleanArrayConverter() {
                });
                break;
            case "short-array":
                item.convertTo(new ShortArrayConverter() {
                });
                break;
            case "integer-array":
                item.convertTo(new IntArrayConverter() {
                });
                break;
            case "long-array":
                item.convertTo(new LongArrayConverter() {
                });
                break;
            case "string-array":
                item.convertTo(new StringArrayConverter() {
                });
                break;
            case "float-array":
                item.convertTo(new FloatArrayConverter() {
                });
                break;
            case "double-array":
                item.convertTo(new DoubleArrayConverter() {
                });
                break;
            case "k1=v1;k2=v2;...":
                item.convertTo(new KVConverter<String, String>() {
                    @Override
                    public HashMap<String, String> convert(String... params) {
                        return parseKV(params);
                    }
                });
                break;
            case "<start>-<end> (double)":
                item.convertTo(new NaturalDoubleRangeConverter() {
                });
                break;
            case "<start>-<end> (integer)":
                item.convertTo(new NaturalIntRangeConverter() {
                });
                break;
            case "<index>:<start>-<end> (integer)":
                item.convertTo(new NaturalIntRangeWithIndexConverter() {
                });
                break;
            case "<start>-<end> (long)":
                item.convertTo(new NaturalLongRangeConverter() {
                });
                break;
            case "<start>-<end> (string)":
                item.convertTo(new RangeConverter() {
                });
                break;
            case "<index>:<start>-<end> (string)":
                item.convertTo(new RangeWithIndexConverter() {
                });
                break;
            default:
                // built-in 或其他的转换器
                // 其他情况需要用户重新配置
                item.convertTo(new AbstractConverter(options[3]));

                if (!options[2].equals(CommandOptions.MISS_VALUE)) {
                    item.defaultTo(options[2]);
                }
                break;
        }

        // 参数长度验证
        if (!options[5].equals(CommandOptions.MISS_VALUE)) {
            try {
                if (options[5].equals(">=1") || options[5].equals("≥1")) {
                    item.arity(-1);
                } else {
                    item.arity(Integer.parseInt(options[5]));
                }
            } catch (NumberFormatException e) {
                throw new CommandParserException(item.getCommandName() + ": couldn't identify arity=" + options[5]);
            }
        } else {
            // 没有设置长度，则按照转换器的长度默认值设置
            item.arity(item.converter.getDefaultLength());
        }

        // 设定默认值
        if (!options[2].equals(CommandOptions.MISS_VALUE)) {
            // 使用转换器将默认值转换为对应的类型
            if (item.converter.isArrayType()) {
                // 无限长度或长度大于 1 时，才进行数组切割
                if (options[2].contains(",")) {
                    // 以 , 作为分隔符
                    item.defaultTo(item.converter.convert(options[2].split(",")));
                } else {
                    // 都不包含，则该参数整体作为一个值
                    item.defaultTo(item.converter.convert(options[2]));
                }
            } else if (item.converter instanceof AbstractConverter) {
                // 抽象转换器会报错，因此只能以 string 形式保存
                item.defaultTo(options[2]);
            } else {
                item.defaultTo(item.converter.convert(options[2]));
            }
        }


        // 其他规则判断: 长度
        if (item.converter instanceof PassedInConverter && item.length != 0) {
            throw new CommandParserException(item.commandNames[0] + ": passedIn type don't accept any values, please set arity to '0'");
        }

        if (item.converter instanceof PassedInConverter && !options[4].equals(CommandOptions.MISS_VALUE)) {
            throw new CommandParserException(item.commandNames[0] + ": passedIn type don't accept any validators, please set validateWith to '.'");
        }

        if (item.converter instanceof IValueConverter && item.length != 1) {
            throw new CommandParserException(item.commandNames[0] + ": single-value (boolean,short,integer,long,float,double,string) type accept 1 value, please set arity to '1'");
        }

        if (item.converter instanceof IMapConverter && item.length != 1) {
            throw new CommandParserException(item.commandNames[0] + ": mapping type accept 1 value, please set arity to '1'");
        }

        if (item.converter instanceof IArrayConverter && item.length == 0) {
            throw new CommandParserException(item.commandNames[0] + ": array (boolean,short,integer,long,float,double,string) type accept at least 1 value, please set arity to any value other than 0");
        }

        // 设定验证器
        String[] validators = options[4].split(";");
        if (!options[4].equals(CommandOptions.MISS_VALUE)) {
            SmartList<IValidator> addToItem = new SmartList<>();

            for (String validator : validators) {
                // 替换空格
                validator = validator.replace(" ", "");

                // 验证器转为小写
                String validator2LowerCase = validator.toLowerCase(Locale.ROOT);
                if (validator2LowerCase.startsWith("rangeof(") && validator2LowerCase.endsWith(")")) {
                    addToItem.add(new RangeValidator(Double.parseDouble(validator2LowerCase.substring(8, validator2LowerCase.indexOf(","))), Double.parseDouble(validator2LowerCase.substring(validator2LowerCase.indexOf(",") + 1, validator2LowerCase.indexOf(")")))));
                } else if (validator2LowerCase.equals("ensurefileexists")) {
                    addToItem.add(EnsureFileExistsValidator.INSTANCE);
                } else if (validator2LowerCase.equals("notdirectory")) {
                    addToItem.add(EnsureFileIsNotDirectoryValidator.INSTANCE);
                } else if (validator2LowerCase.startsWith("elementof(") && validator2LowerCase.endsWith(")")) {
                    addToItem.add(new ElementValidator(validator.substring(10, validator.length() - 1).split(",")));
                } else {
                    addToItem.add(new AbstractValidator(validator));
                }
            }

            item.validateWith(addToItem.toArray(new IValidator[]{}));
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

        return item;
    }
}