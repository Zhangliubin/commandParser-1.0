package edu.sysu.pmglab.suranyi.container;

import edu.sysu.pmglab.suranyi.check.Assert;

/**
 * @author suranyi
 * @description 与 python 类似的生成器
 */

public class Range<T extends Number & Comparable<? super T>> {
    public final T start;
    public final T end;
    public final T step;

    private T currentValue;

    public Range(T start, T end, T step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.currentValue = start;
    }

    public void reset() {
        this.currentValue = start;
    }

    public T getNext() {
        Assert.that(hasNext(), "end of range");

        try {
            return currentValue;
        } finally {
            if (currentValue instanceof Double) {
                currentValue = (T) new Double(currentValue.doubleValue() + step.doubleValue());
            } else if (currentValue instanceof Float) {
                currentValue = (T) new Float(currentValue.floatValue() + step.floatValue());
            } else if (currentValue instanceof Long) {
                currentValue = (T) new Long(currentValue.longValue() + step.longValue());
            } else {
                currentValue = (T) new Integer(currentValue.intValue() + step.intValue());
            }
        }
    }

    public boolean hasNext() {
        return currentValue.compareTo(end) < 0;
    }
}