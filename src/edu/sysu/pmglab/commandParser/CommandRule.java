package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.check.Assert;

import java.util.Objects;

/**
 * @author suranyi
 * @description 命令规则
 */

class CommandRule {
    final String command1;
    final String command2;
    final CommandRuleType type;

    public CommandRule(String command1, String command2, CommandRuleType type) {
        Assert.that(type != null);

        this.command1 = command1;
        this.command2 = command2;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandRule)) {
            return false;
        }
        CommandRule that = (CommandRule) o;
        return command1.equals(that.command1) && command2.equals(that.command2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command1, command2);
    }

    @Override
    public String toString() {
        return command1 + " " + command2 + this.type;
    }
}