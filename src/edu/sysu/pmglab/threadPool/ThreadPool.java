package edu.sysu.pmglab.threadPool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author suranyi
 * @description 线程池
 */

public class ThreadPool {
    /**
     * 全局线程池
     */
    private final ThreadPoolExecutor poolExecutor;
    private boolean finish = false;
    public boolean error = false;

    public ThreadPool submit(Runnable r) {
        if (!finish) {
            poolExecutor.execute(() -> {
                try {
                    r.run();
                } catch (Exception | Error e) {
                    // 捕获到线程池异常时，需要结束当前线程池
                    this.poolExecutor.shutdownNow();
                    this.finish = true;
                    this.error = true;
                    // e.printStackTrace();  // 调试使用
                    throw new UnsupportedOperationException(e.getMessage());
                }
            });
        } else {
            throw new UnsupportedOperationException("Invalid Exception");
        }

        return this;
    }

    public ThreadPool submit(Runnable r, int times) {
        for (int i = 0; i < times; i++) {
            this.submit(r);
        }

        return this;
    }

    public void close() {
        // 关闭线程池
        this.poolExecutor.shutdown();

        // 等待线程池任务结束
        try {
            this.poolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // e.printStackTrace();  // 调试使用
            throw new UnsupportedOperationException(e.getMessage());
        }

        this.finish = true;
    }

    /**
     * 创建全局线程池，透明化线程池
     * @param corePoolSize 指定线程池大小
     */
    public ThreadPool(int corePoolSize) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
            private static final String NAME_PREFIX = "ThreadPool-thread-";
            private final SecurityManager manager = System.getSecurityManager();
            private final ThreadGroup group = (manager != null) ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r, NAME_PREFIX + THREAD_NUMBER.getAndIncrement(), 0);
                if (t.isDaemon()) {
                    t.setDaemon(false);
                }

                if (t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY);
                }

                Thread.setDefaultUncaughtExceptionHandler((Thread thread1, Throwable e) -> {
                    System.out.println("Exception: " + e.getMessage());
                });
                return t;
            }
        };

        // 包含 IO 线程
        this.poolExecutor = new ThreadPoolExecutor(corePoolSize, corePoolSize,
                10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(), threadFactory);
    }

    public boolean isError() {
        return this.error;
    }
}
