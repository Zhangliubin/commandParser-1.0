package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.util.HashMap;

/**
 * @author suranyi
 * @description 命令匹配器，匹配结果将被保存在此处
 */

public class CommandMatcher {
    /**
     * 参数解析列表
     */
    HashMap<String, Object> commandValues = new HashMap<>();
    HashMap<String, Boolean> commandIsPassedIn = new HashMap<>();
    HashMap<String, String> passedInValues = new HashMap<>();

    final HashMap<String, CommandItem> commandItems;

    CommandMatcher(CommandParser parser) {
        this.commandItems = parser.registeredCommandItems;
    }

    public void add(CommandItem commandItem) {
        if (commandIsPassedIn.containsKey(commandItem.getCommandName())) {
            throw new ParameterException("keyword argument repeated");
        }
        this.commandValues.put(commandItem.getCommandName(), commandItem.getDefaultValue());
        this.commandIsPassedIn.put(commandItem.getCommandName(), false);
    }

    public void add(CommandItem commandItem, String[] values) {
        if (commandIsPassedIn.containsKey(commandItem.getCommandName())) {
            throw new ParameterException("keyword argument repeated");
        }

        this.commandValues.put(commandItem.getCommandName(), commandItem.parseValue(values));
        this.commandIsPassedIn.put(commandItem.getCommandName(), true);
        this.passedInValues.put(commandItem.getCommandName(), String.join(" ", values));
    }

    public boolean isPassedIn(String commandKey) {
        CommandItem group = this.commandItems.get(commandKey);
        if (group == null) {
            throw new ParameterException("undefined parameter: " + commandKey);
        }

        return this.commandIsPassedIn.get(group.getCommandName());
    }

    /**
     * 获取参数对应的值
     *
     * @param commandKey 参数键
     * @return 参数值
     */
    public Object get(String commandKey) {
        CommandItem group = this.commandItems.get(commandKey);
        if (group == null) {
            throw new ParameterException("undefined parameter: " + commandKey);
        }

        return this.commandValues.get(group.getCommandName());
    }

    /**
     * 是否包含该参数
     *
     * @param commandKey 参数键
     */
    public boolean contain(String commandKey) {
        return this.commandValues.containsKey(commandKey);
    }

    @Override
    public String toString() {
        StringBuilder commands = new StringBuilder();
        for (String commandName : this.passedInValues.keySet()) {
            commands.append(commandName).append(this.passedInValues.get(commandName)).append(" \\").append("\n");
        }
        return commands.toString();
    }
}