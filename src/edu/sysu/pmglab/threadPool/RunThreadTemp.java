package edu.sysu.pmglab.threadPool;

import java.io.IOException;

/**
 * @Data        :2020/12/08
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :多线程运行方法
 */

public abstract class RunThreadTemp extends Thread {
    @Override
    public void run() {
        try {
            threadWork();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void threadWork() throws IOException, InterruptedException {
        /* 子类必须重写工作方法 */
        throw new UnsupportedOperationException("Invalid Exception");
    }
}

