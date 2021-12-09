package edu.sysu.pmglab.suranyi.commandParser;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;
import edu.sysu.pmglab.suranyi.container.SmartList;

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
    final SmartList<String[]> caughtValues = new SmartList<>(1, true);
    final HashMap<String, CommandItem> commandItems;

    CommandMatcher(CommandParser parser) {
        this.commandItems = parser.registeredCommandItems;
    }

    void add(CommandItem commandItem) {
        if (isPassedIn.contains(commandItem.getCommandName())) {
            throw new ParameterException("keyword argument repeated");
        }
        this.values.put(commandItem.getCommandName(), commandItem.getDefaultValue());
    }

    void add(CommandItem commandItem, String[] values) {
        if (isPassedIn.contains(commandItem.getCommandName())) {
            throw new ParameterException("keyword argument repeated");
        }

        this.values.put(commandItem.getCommandName(), commandItem.parseValue(values));
        this.isPassedIn.add(commandItem.getCommandName());
        this.caughtValues.add(new String[]{commandItem.getCommandName(), String.join(" ", values)});
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
        CommandItem group = this.commandItems.get(commandKey);
        if (group == null) {
            throw new ParameterException("undefined parameter: " + commandKey);
        }

        return this.values.get(group.getCommandName());
    }

    /**
     * 是否包含该参数 (可以没有传入)
     *
     * @param commandKey 参数键
     */
    public boolean contain(String commandKey) {
        return this.values.containsKey(commandKey);
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