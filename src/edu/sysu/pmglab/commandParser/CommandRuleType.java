package edu.sysu.pmglab.commandParser;

/**
 * @author suranyi
 * @description 两组命令之间的组合状态
 */
public enum CommandRuleType {
    /**
     * 至多 1 个 A + B <= 1
     */
    AT_MOST_ONE,

    /**
     * 至少 1 个 A + B >= 1
     */
    AT_LEAST_ONE,

    /**
     * 恰好一个 A + B == 1
     */
    REQUEST_ONE,

    /**
     * A 是 B 的先决条件 A >= B
     */
    PRECONDITION,

    /**
     * 依存 A = B
     */
    SYMBIOSIS
}