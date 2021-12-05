package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;
import edu.sysu.pmglab.suranyi.container.SmartList;

import java.util.Comparator;
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

    SmartList<String[]> passedInValues = new SmartList<>(1, true);

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
        this.passedInValues.add(new String[]{commandItem.getCommandName(), String.join(" ", values)});
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
        if (passedInValues.size() > 0) {
            // 按照优先级排序
            this.passedInValues.sort(Comparator.comparingInt(o -> commandItems.get(o[0]).getPriority()));

            // 链接参数
            StringBuilder commands = new StringBuilder();
            for (int i = 0; i < passedInValues.size() - 1; i++) {
                commands.append(passedInValues.get(i)[0]);
                String value = passedInValues.get(i)[1];
                if (value.length() > 0) {
                    commands.append(" ").append(value);
                }

                commands.append(" \\").append("\n");
            }

            // 添加最后一个参数
            commands.append(passedInValues.get(-1)[0]);

            String value = passedInValues.get(-1)[1];
            if (value.length() > 0) {
                commands.append(" ").append(value);
            }

            return commands.toString();
        } else {
            return "";
        }
    }
}