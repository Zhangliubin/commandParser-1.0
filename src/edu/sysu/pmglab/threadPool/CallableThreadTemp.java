package edu.sysu.pmglab.threadPool;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @Data        :2020/12/08
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :可以获取返回值的多线程方法
 */

public abstract class CallableThreadTemp<T> implements Callable<T> {
    public T run() {
        try {
            return threadWork();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Invalid Exception");
        }
    }

    protected T threadWork() throws IOException, InterruptedException {
        /* 子类必须重写工作方法 */
        throw new UnsupportedOperationException("Invalid Exception");
    }
}

