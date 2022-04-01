package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.converter.array.*;
import edu.sysu.pmglab.commandParser.converter.map.*;
import edu.sysu.pmglab.container.Array;

import java.util.Arrays;

/**
 * @author suranyi
 * @description 程序使用说明
 */

class CommandUsage {
    String programName;

    private final int length = 80;
    private final int firstLevelPrefixLength = 0;
    private final int secondLevelPrefixLength = 2;
    private final int descriptionPrefixLength = 2;
    private final String requestMark = "*";

    CommandParser parser;

    public CommandUsage(CommandParser parser) {
        this.parser = parser;
    }

    public void setProgramName(String commandName) {
        this.programName = commandName.trim();
    }

    String addOptionGroups(StringBuilder out, String groupName, Array<String> commands) {
        out.append(generateSpaces(firstLevelPrefixLength)).append(groupName).append(":");

        // 获取该参数分组下最长的参数长度
        int commandNamesMaxLength = 0;

        for (String command : commands) {
            CommandItem commandItem = this.parser.getCommandItem(command);
            commandNamesMaxLength = Math.max(commandNamesMaxLength, (commandItem.isRequest() ? requestMark.length() : 0) + Arrays.toString(commandItem.getCommandNames()).length() - 2);
        }

        commandNamesMaxLength += descriptionPrefixLength;

        int descriptionPrefixLength = commandNamesMaxLength + secondLevelPrefixLength;
        for (String command : commands) {
            CommandItem commandItem = this.parser.getCommandItem(command);

            out.append("\n");
            out.append(generateSpaces(secondLevelPrefixLength));
            String commandLinked = Arrays.toString(commandItem.getCommandNames());
            int markLength = 0;
            if (commandItem.isRequest()) {
                out.append(requestMark);
                markLength = requestMark.length();
            }
            out.append(commandLinked, 1, commandLinked.length() - 1);
            out.append(generateSpaces(commandNamesMaxLength - commandLinked.length() + 2 - markLength));

            String description = commandItem.getDescription().equals(CommandOptions.DEFAULT_DESCRIPTION) ? "" : commandItem.getDescription();

            if (commandItem.getLength() >= 1 && commandItem.getDefaultValue() != null) {
                if (description.length() > 0) {
                    description += "\n";
                }

                description += "default: " + getDefaultValue(commandItem);

                if (!commandItem.getFormat().equals(CommandOptions.DEFAULT_FORMAT)) {
                    description += "; format: " + commandItem.getFormat();
                }
            } else if (!commandItem.getFormat().equals(CommandOptions.DEFAULT_FORMAT)) {
                if (description.length() > 0) {
                    description += "\n";
                }

                description += "format: " + commandItem.getFormat();
            }

            wrapDescription(out, descriptionPrefixLength, description);
        }

        return out.toString();
    }

    String getDefaultValue(CommandItem commandItem) {
        if (commandItem.getConverter() instanceof IntArrayConverter || commandItem.getConverter() instanceof NaturalIntRangeConverter || commandItem.getConverter() instanceof NaturalIntRangeWithIndexConverter) {
            return Arrays.toString((int[]) commandItem.getDefaultValue());
        } else if (commandItem.getConverter() instanceof DoubleArrayConverter || commandItem.getConverter() instanceof NaturalDoubleRangeConverter) {
            return Arrays.toString((double[]) commandItem.getDefaultValue());
        } else if (commandItem.getConverter() instanceof ShortArrayConverter) {
            return Arrays.toString((short[]) commandItem.getDefaultValue());
        } else if (commandItem.getConverter() instanceof LongArrayConverter || commandItem.getConverter() instanceof NaturalLongRangeConverter) {
            return Arrays.toString((long[]) commandItem.getDefaultValue());
        } else if (commandItem.getConverter() instanceof StringArrayConverter || commandItem.getConverter() instanceof RangeConverter || commandItem.getConverter() instanceof RangeWithIndexConverter) {
            return Arrays.toString((String[]) commandItem.getDefaultValue());
        }

        return commandItem.getDefaultValue().toString();
    }

    void wrapDescription(StringBuilder out, int indent, String description) {
        if (description.contains("\n")) {
            // 包含 \n，此时需要细微处理
            String[] descriptions = description.split("\n");

            if (descriptions.length > 1) {
                wrapDescription(out, indent, descriptions[0]);
                for (int i = 1; i < descriptions.length; i++) {
                    out.append("\n");
                    out.append(generateSpaces(indent));
                    wrapDescription(out, indent, descriptions[i]);
                }
            } else {
                wrapDescription(out, indent, descriptions[0]);
            }
        } else {
            String[] words = description.split(" ");
            int current = indent;

            for (int i = 0; i < words.length; ++i) {
                String word = words[i];
                if (word.length() > 0) {
                    if ((word.length() <= length) && (current + 1 + word.length()) > length) {
                        out.append("\n").append(generateSpaces(indent)).append(word).append(" ");
                        current = indent + word.length() + 1;
                    } else {
                        out.append(word);
                        current += word.length();
                        if (i != words.length - 1) {
                            out.append(" ");
                            ++current;
                        }
                    }
                }
            }
        }
    }

    String generateSpaces(int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; ++i) {
            result.append(" ");
        }

        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append("Usage: " + programName + " [options]");
        boolean showDebugParam = parser.debug;

        // 对命令进行分组
        Array<String> optionGroups = new Array<>();
        for (String commandName : this.parser.mainRegisteredCommandItems) {
            CommandItem commandItem = this.parser.getCommandItem(commandName);
            if (!optionGroups.contains(commandItem.getOptionGroup()) && !commandItem.isHide()) {
                if (showDebugParam || !commandItem.isDebug()) {
                    optionGroups.add(commandItem.getOptionGroup());
                }
            }
        }

        Array<String> commands = new Array<>();
        for (String optionGroup : optionGroups) {
            commands.clear();
            for (String command : this.parser.mainRegisteredCommandItems) {
                CommandItem commandItem = this.parser.getCommandItem(command);

                if (commandItem.getOptionGroup().equals(optionGroup) && (showDebugParam || !commandItem.isDebug()) && !commandItem.isHide()) {
                    commands.add(command);
                }
            }

            out.append("\n");
            addOptionGroups(out, optionGroup, commands);
        }

        return out.toString();
    }
}