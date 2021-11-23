package edu.sysu.pmglab.suranyi.unifyIO.partwriter;

import edu.sysu.pmglab.suranyi.check.Assert;

/**
 * @author suranyi
 * @description BGZF 参数类，该类是轻量级可复用类
 */

public class BGZOutputParam {
    public final boolean toBGZF;
    public final int level;

    public static final int DEFAULT_LEVEL = 5;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 9;

    public BGZOutputParam() {
        this(true, DEFAULT_LEVEL);
    }

    public BGZOutputParam(boolean toBGZF, int level) {
        Assert.valueRange(level, MIN_LEVEL, MAX_LEVEL);

        this.toBGZF = toBGZF;
        this.level = level;
    }

    public BGZOutputParam(boolean toBGZF) {
        this(toBGZF, DEFAULT_LEVEL);
    }

    @Override
    public String toString() {
        return "compressToBGZF: " + this.toBGZF + (this.toBGZF ? " (" + this.level + ")" : "");
    }
}