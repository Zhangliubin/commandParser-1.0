package edu.sysu.pmglab.suranyi.commandParser;

/**
 * @author suranyi
 * @description 两组命令之间的组合状态
 */
public enum CommandRuleType {
    /**
     * 至多 1 个 (<= 1)
     */
    AT_MOST_ONE,

    /**
     * 至少 1 个 (>= 1)
     */
    AT_LEAST_ONE,

    /**
     * 恰好一个 (= 1)
     */
    REQUEST_ONE,

    /**
     * 依存 (0 or 2)
     */
    INTERDEPEND
}