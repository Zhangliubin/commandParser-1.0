package edu.sysu.pmglab.suranyi.easytools;

import edu.sysu.pmglab.suranyi.container.Pair;

import java.util.concurrent.Callable;

/**
 * @author suranyi
 * @description 运行时静态工具集
 */

public class RunTimeUtils {
    /**
     * 程序计时
     * @param runnable 可运行的方法
     */
    public static long getRunTime(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - start;
    }

    /**
     * 程序计时
     * @param callable 可运行的方法
     */
    public static <T> Pair<Long, T> getRunTime(Callable<T> callable) throws Exception {
        long start = System.currentTimeMillis();
        T result = callable.call();
        return new Pair<>(System.currentTimeMillis() - start, result);
    }
}