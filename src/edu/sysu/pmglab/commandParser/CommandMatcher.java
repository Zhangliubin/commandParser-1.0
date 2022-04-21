package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.ParameterException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author suranyi
 * @description 命令匹配器，匹配结果将被保存在此处
 */

public class CommandMatcher {
    /**
     * 参数解析列表
     */
    final HashMap<String, Object> values = new HashMap<>();
    final HashSet<String> isPassedIn = new HashSet<>();
    final ArrayList<String[]> caughtValues = new ArrayList<>();
    final HashMap<String, CommandItem> commandItems;
    final boolean help;

    CommandMatcher(CommandParser parser, boolean help) {
        this.commandItems = parser.registeredCommandItems;
        this.help = help;
    }

    void addAsDefault(CommandItem commandItem) {
        String commandName = commandItem.getCommandName();

        if (this.values.containsKey(commandName)) {
            throw new ParameterException("keyword argument repeated");
        }

        if (!this.help) {
            this.values.put(commandName, commandItem.getDefaultValue());
        }
    }

    void add(CommandItem commandItem, String[] values) {
        if (help) {
            String commandName = commandItem.getCommandName();

            if (isPassedIn.contains(commandName)) {
                throw new ParameterException("keyword argument repeated");
            }

            // help 模式下不解析值
            this.isPassedIn.add(commandName);
            this.caughtValues.add(new String[]{commandName, String.join(" ", values)});
        } else {
            String commandName = commandItem.getCommandName();

            if (isPassedIn.contains(commandName)) {
                throw new ParameterException("keyword argument repeated");
            }

            this.values.put(commandName, commandItem.parseValue(values));
            this.isPassedIn.add(commandName);
            this.caughtValues.add(new String[]{commandName, String.join(" ", values)});
        }
    }

    public boolean isPassedIn(String commandKey) {
        CommandItem group = this.commandItems.get(commandKey);
        if (group == null) {
            throw new ParameterException("undefined parameter: " + commandKey);
        }

        return this.isPassedIn.contains(group.getCommandName());
    }

    /**
     * 获取参数对应的值
     *
     * @param commandKey 参数键
     * @return 参数值
     */
    public Object get(String commandKey) {
        if (help) {
            throw new ParameterException("only '.isPassedIn($commandName)' can be used in help mode");
        } else {
            CommandItem group = this.commandItems.get(commandKey);
            if (group == null) {
                throw new ParameterException("undefined parameter: " + commandKey);
            }

            return this.values.get(group.getCommandName());
        }
    }

    @Override
    public String toString() {
        if (caughtValues.size() > 0) {
            // 按照优先级排序
            this.caughtValues.sort(Comparator.comparingInt(o -> commandItems.get(o[0]).getPriority()));

            // 链接参数
            StringBuilder commands = new StringBuilder();
            for (int i = 0; i < caughtValues.size() - 1; i++) {
                commands.append(caughtValues.get(i)[0]);
                String value = caughtValues.get(i)[1];
                if (value.length() > 0) {
                    commands.append(" ").append(value);
                }

                commands.append(" \\").append("\n");
            }

            // 添加最后一个参数
            commands.append(caughtValues.get(-1)[0]);

            String value = caughtValues.get(-1)[1];
            if (value.length() > 0) {
                commands.append(" ").append(value);
            }

            return commands.toString();
        } else {
            return "";
        }
    }
}