package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.commandParser.converter.map.KVConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.value.PassedInConverter;
import edu.sysu.pmglab.suranyi.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;
import edu.sysu.pmglab.suranyi.container.SmartList;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.InputStreamReaderStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static edu.sysu.pmglab.suranyi.commandParser.CommandOptions.*;

/**
 * @author suranyi
 * @description 命令解析器
 */

public class CommandParser {
    /**
     * 注册的参数
     */
    HashMap<String, CommandItem> registeredCommandItems = new HashMap<>();

    /**
     * 注册的主参数名，该表与参数的优先级绑定
     */
    SmartList<String> mainRegisteredCommandItems = new SmartList<>(1, true);

    /**
     * 注册的规则及其顺序表
     */
    HashMap<Integer, CommandRule> registeredRules = new HashMap<>(1);
    SmartList<Integer> registeredRulesOrder = new SmartList<>(1, true);

    int priority = 0;
    String optionGroup = CommandOptions.DEFAULT_OPTION_GROUP;

    /**
     * 忽略前 offset 个指令
     */
    int offset = 0;

    /**
     * 全局规则
     */
    CommandRuleType globalRules = null;

    /**
     * 程序帮助文档
     */
    CommandUsage usage = new CommandUsage(this);

    /**
     * 是否解析 debug 指令
     */
    boolean debug = false;

    public CommandParser() {
        this(true, "<main class>");
    }

    public CommandParser(String programName) {
        this(true, programName);
    }

    public CommandParser(boolean help) {
        this(help, "<main class>");
    }

    public CommandParser(boolean help, String programName) {
        if (help) {
            register("--help", "-help", "-h").setOptions(HELP, HIDDEN).convertTo(new PassedInConverter() {
            });
        }
        this.usage.setProgramName(programName);
    }

    /**
     * 重设主类名
     */
    public CommandParser setProgramName(String programName) {
        this.usage.setProgramName(programName);
        return this;
    }

    /**
     * 注册全局指令规则
     *
     * @param ruleType 命令规则
     */
    public void registerGlobalRule(CommandRuleType ruleType) {
        // 全局规则 (至少有一个、至多一个、依存)
        if (ruleType.equals(CommandRuleType.SYMBIOSIS) || ruleType.equals(CommandRuleType.PRECONDITION)) {
            throw new CommandParserException(ruleType + " cannot be defined in a global-rule because it may raise some controversial issues");
        } else {
            this.globalRules = ruleType;
        }
    }

    /**
     * 跳过前面的 length 个参数
     */
    public CommandParser offset(int length) {
        Assert.that(length >= 0);

        this.offset = length;
        return this;
    }

    /**
     * debug 模式
     */
    public CommandParser debug(boolean enable) {
        this.debug = enable;
        return this;
    }

    /**
     * debug 模式
     */
    public CommandParser debug() {
        return debug(true);
    }

    /**
     * 设置参数组
     *
     * @param optionGroup 参数组
     */
    public void createOptionGroup(String optionGroup) {
        this.optionGroup = optionGroup.trim();
    }

    /**
     * 添加需要捕获的参数信息
     *
     * @param commandNames 其他参数
     */
    public CommandItem register(String... commandNames) {
        Assert.NotEmpty(commandNames);

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (registeredCommandItems.containsKey(commandName)) {
                throw new CommandParserException(commandName + " already defined");
            }
        }

        // 校验通过
        CommandItem commandItem = new CommandItem(commandNames);
        for (String commandName : commandNames) {
            registeredCommandItems.put(commandName, commandItem);
        }

        // 添加主参数名
        mainRegisteredCommandItems.add(commandItem.getCommandName());

        // 设置参数的优先级
        commandItem.setPriority(this.priority++);
        commandItem.setOptionGroup(this.optionGroup);

        return commandItem;
    }

    CommandItem register(CommandItem commandItem) {
        Assert.NotNull(commandItem != null);

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandItem.getCommandNames()) {
            if (registeredCommandItems.containsKey(commandName)) {
                throw new CommandParserException(commandName + " already defined");
            }
        }

        // 校验通过
        for (String commandName : commandItem.getCommandNames()) {
            registeredCommandItems.put(commandName, commandItem);
        }

        // 添加主参数名
        mainRegisteredCommandItems.add(commandItem.getCommandName());

        // 设置参数的优先级
        commandItem.setPriority(this.priority++);

        return commandItem;
    }

    /**
     * 注册两两参数之间的规则
     *
     * @param item1    命令组 1
     * @param item2    命令组 2
     * @param ruleType 命令规则
     */
    public void registerRule(String item1, String item2, CommandRuleType ruleType) {
        if (!registeredCommandItems.containsKey(item1)) {
            throw new CommandParserException(item1 + " is not defined in Parser");
        }

        if (!registeredCommandItems.containsKey(item2)) {
            throw new CommandParserException(item2 + " is not defined in Parser");
        }

        if (ruleType != CommandRuleType.PRECONDITION) {
            if (this.registeredCommandItems.get(item1).getPriority() > this.registeredCommandItems.get(item2).getPriority()) {
                String temp = item2;
                item2 = item1;
                item1 = temp;
            }
        }

        if (this.registeredCommandItems.get(item1).isRequest()) {
            throw new CommandParserException(item1 + " is a required parameter and cannot register rules with other parameters.");
        }

        if (this.registeredCommandItems.get(item2).isRequest()) {
            throw new CommandParserException(item2 + " is a required parameter and cannot register rules with other parameters.");
        }

        // 低优先级-高优先级参数
        int registeredMark = Objects.hash(item1, item2);

        if (this.registeredRules.containsKey(registeredMark)) {
            throw new CommandParserException(item1 + " and " + item2 + " have already defined rules and cannot be set repeatedly");
        } else {
            this.registeredRules.put(registeredMark, new CommandRule(this.registeredCommandItems.get(item1).getCommandName(), this.registeredCommandItems.get(item2).getCommandName(), ruleType));
            this.registeredRulesOrder.add(registeredMark);
        }
    }

    /**
     * 注册指令彼此之间的规则
     *
     * @param item1    命令
     * @param items    命令组
     * @param ruleType 命令规则
     */
    public void registerRule(String item1, String[] items, CommandRuleType ruleType) {
        registerRule(new String[]{item1}, items, ruleType);
    }

    /**
     * 注册指令与所有其他指令之间的规则 (items1 和 items2 不能有交集)
     *
     * @param items1   命令组 1
     * @param items2   命令组 2
     * @param ruleType 命令规则
     */
    public void registerRule(String[] items1, String[] items2, CommandRuleType ruleType) {
        for (String item1 : items1) {
            for (String item2 : items2) {
                if (item1.equals(item2)) {
                    throw new CommandParserException("repeat the defined instruction rules: " + item1);
                }
            }
        }

        for (String item1 : items1) {
            for (String item2 : items2) {
                registerRule(item1, item2, ruleType);
            }
        }
    }

    /**
     * 解析指令
     *
     * @param args 待解析的指令列表
     * @return 返回解析结果
     */
    public CommandMatcher parse(String... args) {
        if (this.offset > args.length) {
            throw new ParameterException("program takes at least " + this.offset + " positional argument (" + args.length + " given)");
        }

        // 查看是否包含 help 指令, 如果包含 help 指令，则不进行强制的参数解析工作
        boolean passedInHelp = false;

        if (this.debug) {
            for (String arg : args) {
                CommandItem matchedItem = this.registeredCommandItems.get(arg);
                if (matchedItem != null && matchedItem.isHelp()) {
                    passedInHelp = true;
                    break;
                }
            }
        } else {
            for (String arg : args) {
                CommandItem matchedItem = this.registeredCommandItems.get(arg);
                if (matchedItem != null) {
                    if (matchedItem.isDebug()) {
                        throw new ParameterException("debug parameter " + matchedItem.getCommandName() + " is used in non-debug mode.");
                    }

                    if (matchedItem.isHelp()) {
                        passedInHelp = true;
                    }
                }
            }
        }

        // 创建捕获组
        CommandMatcher matcher = new CommandMatcher(this);

        // 当开启了 request one 模式时 (常用于子模式选择), 只匹配第一个
        if (this.globalRules == CommandRuleType.REQUEST_ONE) {
            CommandItem matchedItem = this.registeredCommandItems.get(args[this.offset]);

            if (matchedItem == null) {
                if (!passedInHelp) {
                    throw new ParameterException(args[this.offset] + " is passed but no commandItem was defined in Parser");
                }
            } else {
                // 匹配任意长度的参数 (这种情况下包含 0 长参数, 但 真值 和 0 会校验长度)
                if (!passedInHelp && matchedItem.getLength() != -1 && (args.length - this.offset - 1 != matchedItem.getLength())) {
                    throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getLength() + " positional argument (" + (args.length - this.offset - 1) + " given)");
                }

                matcher.add(matchedItem, Arrays.copyOfRange(args, this.offset + 1, args.length));
            }
        } else {
            for (int seek = this.offset; seek < args.length; seek++) {
                CommandItem matchedItem = this.registeredCommandItems.get(args[seek]);

                if (matchedItem != null) {

                    if (matcher.isPassedIn.contains(matchedItem.getCommandName())) {
                        if (!passedInHelp) {
                            // 若已经包含该参数，报错
                            throw new ParameterException(matchedItem.getCommandName() + " keyword argument repeated");
                        } else {
                            continue;
                        }
                    }

                    // 匹配指定长度的参数
                    if (matchedItem.getLength() == -1) {
                        int length0 = 0;
                        while (seek + length0 + 1 < args.length) {
                            if (this.registeredCommandItems.containsKey(args[seek + length0 + 1])) {
                                break;
                            } else {
                                length0++;
                            }
                        }

                        if (length0 == 0) {
                            // 任意长度的参数，但是没有输入
                            if (!passedInHelp) {
                                throw new ParameterException(matchedItem.getCommandName() + " takes at least 1 positional argument (0 given)");
                            } else {
                                matcher.add(matchedItem, new String[]{});
                            }
                        } else {
                            // 添加捕获组
                            matcher.add(matchedItem, Arrays.copyOfRange(args, seek + 1, seek + 1 + length0));
                            seek += length0;
                        }
                    } else if (matchedItem.getLength() == 0) {
                        matcher.add(matchedItem, new String[]{});
                    } else {
                        // 有指定长度
                        int length0 = 0;
                        while ((seek + length0 + 1 < args.length) && (length0 < matchedItem.getLength())) {
                            if (this.registeredCommandItems.containsKey(args[seek + length0 + 1])) {
                                break;
                            } else {
                                length0++;
                            }
                        }

                        if (length0 < matchedItem.getLength()) {
                            if (!passedInHelp) {
                                throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getLength() + " positional argument (" + length0 + " given)");
                            }
                            // 否则，这一组没有完全输入的参数将被整体丢弃
                        } else {
                            // 添加捕获组
                            matcher.add(matchedItem, Arrays.copyOfRange(args, seek + 1, seek + 1 + length0));
                        }
                        seek += length0;
                    }
                } else {
                    if (!passedInHelp) {
                        throw new ParameterException(args[seek] + " is passed but no commandItem was defined in Parser");
                    }
                }
            }
        }


        if (!passedInHelp) {
            // 检查所有 request 参数
            for (String commandName : this.mainRegisteredCommandItems) {
                if (this.registeredCommandItems.get(commandName).isRequest() && !matcher.isPassedIn.contains(commandName)) {
                    throw new ParameterException("missing required positional argument: " + commandName);
                }
            }

            // 检查全局条件
            if (this.globalRules != null) {
                switch (this.globalRules) {
                    case AT_LEAST_ONE:
                        if (matcher.isPassedIn.size() < 1) {
                            throw new ParameterException(mainRegisteredCommandItems.toString() + " must be assigned at least one ");
                        }
                        break;
                    case AT_MOST_ONE:
                        if (matcher.isPassedIn.size() > 1) {
                            throw new ParameterException(mainRegisteredCommandItems.toString() + " could be assigned at most one");
                        }
                        break;
                    case REQUEST_ONE:
                        if (matcher.isPassedIn.size() == 0) {
                            String info = mainRegisteredCommandItems.toString();
                            throw new ParameterException("program missing 1 required argument: " + info.substring(1, info.length() - 1));
                        } else if (matcher.isPassedIn.size() >= 2) {
                            String info = mainRegisteredCommandItems.toString();
                            throw new ParameterException("program takes 1 required argument (" + info.substring(1, info.length() - 1) + ") but more than 1 were given");
                        }
                    default:
                        break;
                }
            }

            // 检查所有的关联条件组
            for (CommandRule rule : this.registeredRules.values()) {
                int flag1 = matcher.isPassedIn(rule.command1) ? 1 : 0;
                int flag2 = matcher.isPassedIn(rule.command2) ? 1 : 0;
                switch (rule.type) {
                    case AT_LEAST_ONE:
                        if (flag1 + flag2 >= 1) {
                            break;
                        } else {
                            throw new ParameterException(rule.command1 + " and " + rule.command2 + " should be assigned at least one");
                        }
                    case SYMBIOSIS:
                        if (flag1 + flag2 != 1) {
                            break;
                        } else {
                            throw new ParameterException(rule.command1 + " and " + rule.command2 + " should be assigned (or not) at the same time");
                        }
                    case AT_MOST_ONE:
                        if (flag1 + flag2 <= 1) {
                            break;
                        } else {
                            throw new ParameterException(rule.command1 + " and " + rule.command2 + " could be assigned at most one");
                        }
                    case REQUEST_ONE:
                        if (flag1 + flag2 == 1) {
                            break;
                        } else {
                            throw new ParameterException("one of " + rule.command1 + " and " + rule.command2 + " must be assigned");
                        }
                    case PRECONDITION:
                        if (flag1 >= flag2) {
                            break;
                        } else {
                            throw new ParameterException(rule.command1 + " should be assigned together with " + rule.command2);
                        }
                    default:
                        break;
                }
            }
        }

        // 添加默认参数
        for (String commandName : this.mainRegisteredCommandItems) {
            if (!matcher.isPassedIn(commandName)) {
                matcher.addAsDefault(this.registeredCommandItems.get(commandName));
            }
        }
        return matcher;
    }

    /**
     * 解析指令
     *
     * @param fileName 参数文件名
     * @return 返回解析结果
     */
    public CommandMatcher parseFromFile(String fileName) throws IOException {
        try (FileStream file = new FileStream(fileName, fileName.endsWith(".gz") ? FileOptions.GZIP_READER : FileOptions.DEFAULT_READER)) {
            SmartList<String> args = new SmartList<>();
            String line;
            while ((line = file.readLineToString()) != null) {
                // 去除首尾空白信息, 把 \t 换为空格
                line = line.replace("\t", " ").trim();

                if (!line.startsWith("#") && line.equals("\\")) {
                    // 以 \ 结尾，去除该字符
                    if (line.endsWith(" \\")) {
                        line = line.substring(0, line.length() - 2);
                    }

                    for (String arg: line.split(" ")) {
                        if (arg.length() > 0) {
                            args.add(arg);
                        }
                    }
                }
            }
            return parse(args.toStringArray());
        }
    }

    /**
     * 解析指令
     *
     * @param strings 长字符串
     * @return 返回解析结果
     */
    public CommandMatcher parseFromString(String strings) {
        // 按行切割数据
        String[] lines = strings.split("\n");

        // 把 strings 包装为 FileStream
        SmartList<String> args = new SmartList<>();

        for (String line : lines) {
            // 去除首尾空白信息, 把 \t 换为空格
            line = line.replace("\t", " ").trim();

            if (!line.startsWith("#") && line.length() > 0) {
                // 以 \ 结尾，去除该字符
                if (line.endsWith(" \\")) {
                    line = line.substring(0, line.length() - 2);
                }

                for (String arg: line.split(" ")) {
                    if (arg.length() > 0) {
                        args.add(arg);
                    }
                }
            }
        }

        return parse(args.toStringArray());
    }

    /**
     * 获取注册的命令组
     *
     * @param commandName 命令名
     */
    public CommandItem getCommandItem(String commandName) {
        if (!registeredCommandItems.containsKey(commandName)) {
            throw new CommandParserException(commandName + " is not defined in Parser");
        }

        return registeredCommandItems.get(commandName);
    }

    /**
     * 是否包含该命令
     *
     * @param commandName 命令名
     */
    public boolean containCommandItem(String commandName) {
        return registeredCommandItems.containsKey(commandName);
    }

    /**
     * 获取注册的命令规则
     *
     * @param commandName1 命令1
     * @param commandName2 命令2
     */
    public CommandRuleType getCommandRule(String commandName1, String commandName2) {
        if (!registeredCommandItems.containsKey(commandName1)) {
            throw new CommandParserException(commandName1 + " is not defined in Parser");
        }

        if (!registeredCommandItems.containsKey(commandName2)) {
            throw new CommandParserException(commandName2 + " is not defined in Parser");
        }

        if (this.registeredCommandItems.get(commandName1).getPriority() > this.registeredCommandItems.get(commandName1).getPriority()) {
            String temp = commandName2;
            commandName2 = commandName1;
            commandName1 = temp;
        }

        CommandRule rule = registeredRules.get(Objects.hash(commandName1, commandName2));
        return rule == null ? null : rule.type;
    }

    public static CommandParser loadFromFile(String fileName) throws IOException {
        return loadFromFile(new FileStream(fileName, FileOptions.CHANNEL_READER));
    }

    public static <T> CommandParser loadFromInnerResource(Class<T> className, String fileName) throws IOException {
        return CommandParser.loadFromFile(new FileStream(new InputStreamReaderStream(className.getResourceAsStream(fileName))));
    }

    public static CommandParser loadFromFile(FileStream file) throws IOException {
        // 写入注释行
        String line = file.readLineToString();
        if (line == null || !line.equals("##" + VERSION)) {
            throw new CommandParserException(VERSION);
        }

        // 创建键值对转换器
        KVConverter<String, String> kvConverter = new KVConverter<String, String>() {
            @Override
            public HashMap<String, String> convert(String... params) {
                return parseKV(params);
            }
        };

        CommandParser parser = new CommandParser(false);
        SmartList<CommandRule> registeredRules = new SmartList<>(1, true);
        while ((line = file.readLineToString()) != null) {
            // 解析注释行
            if (line.startsWith("##")) {
                if (line.startsWith("##programName=<")) {
                    HashMap<String, String> converted = kvConverter.parseKV(line.substring(15, line.length() - 1));
                    parser.setProgramName(converted.get("value").replace("\"", ""));
                }

                if (line.startsWith("##offset=<")) {
                    HashMap<String, String> converted = kvConverter.parseKV(line.substring(10, line.length() - 1));
                    parser.offset(Integer.parseInt(converted.get("value").replace("\"", "")));
                }

                if (line.startsWith("##globalRule=<")) {
                    HashMap<String, String> converted = kvConverter.parseKV(line.substring(14, line.length() - 1));
                    if (!(".".equals(converted.get("value")) || "'.'".equals(converted.get("value")) || "\".\"".equals(converted.get("value")))) {
                        // 全局规则非空
                        parser.registerGlobalRule(CommandRuleType.valueOf(converted.get("value").replace("\"", "").replace("'", "")));
                    }
                }

                if (line.startsWith("##commandRule=<")) {
                    HashMap<String, String> converted = kvConverter.parseKV(line.substring(15, line.length() - 1));
                    // 先保存，之后再添加
                    registeredRules.add(new CommandRule(converted.get("command1").replace("\"", ""), converted.get("command2").replace("\"", ""), CommandRuleType.valueOf(converted.get("value").replace("\"", ""))));
                }

                if (line.startsWith("##debugMode=")) {
                    if ("false".equals(line.substring(12))) {
                        parser.debug = false;
                    } else if ("true".equals(line.substring(12))) {
                        parser.debug = true;
                    } else {
                        throw new CommandParserException("debugMode with an unrecognized value (supported: false/true)");
                    }
                }
            } else {
                if (!line.equals(HEADER)) {
                    throw new CommandParserException("no header found");
                } else {
                    break;
                }
            }
        }

        if (line == null) {
            throw new CommandParserException("no header found");
        }

        // 解析指令
        while ((line = file.readLineToString()) != null) {
            parser.register(CommandItem.loadFromString(line));
        }

        file.close();

        // 添加规则
        for (CommandRule rule : registeredRules) {
            parser.registerRule(rule.command1, rule.command2, rule.type);
        }

        return parser;
    }

    public void toFile(String fileName) {
        try (FileStream file = new FileStream(fileName, FileOptions.CHANNEL_WRITER)) {
            // 写入注释行
            file.write("##" + VERSION + "\n");
            file.write("##programName=<value=\"" + usage.programName + "\";description=\"when '-h' were passed in, would be show 'Usage: $value [options]'\">\n");
            file.write("##debugMode=" + this.debug + "\n");
            file.write("##offset=<value=" + offset + ";description=\"skip the $value arguments before the command argument passed in\">\n");
            if (globalRules == null) {
                file.write("##globalRule=<value=\".\";description=\"one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}\">\n");
            } else {
                file.write("##globalRule=<value=\"" + globalRules + "\";description=\"one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}\">\n");
            }

            for (int order : this.registeredRulesOrder) {
                CommandRule rule = this.registeredRules.get(order);
                file.write("##commandRule=<command1=\"" + rule.command1 + "\";command2=\"" + rule.command2 + "\";value=\"" + rule.type + "\";description=\"one of the following rules is supported: {'AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE','PRECONDITION','SYMBIOSIS'}>\n");
            }

            // 写入标题行
            file.write(HEADER);
            for (String commandName : this.mainRegisteredCommandItems) {
                file.write("\n");
                file.write(this.registeredCommandItems.get(commandName).toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        this.mainRegisteredCommandItems.sort(Comparator.comparingInt(o -> registeredCommandItems.get(o).getPriority()));
        return this.usage.toString();
    }
}

