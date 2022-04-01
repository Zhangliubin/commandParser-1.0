package edu.sysu.pmglab.threadPool;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Data        :2020/06/26
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :动态数据管道，T 代表数据类型
 */

public class DynamicPipeline<S, D> {
    private final S defaultStatus;
    private final D defaultData;
    private final LinkedBlockingQueue<Block<S, D>> pipeLine;

    public DynamicPipeline(int capacity) {
        this(capacity, null, null);
    }

    public DynamicPipeline(int capacity, S status, D data) {
        this.defaultStatus = status;
        this.defaultData = data;
        pipeLine = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * 送入数据
     */
    public void put(S status, D data) throws InterruptedException {
        this.pipeLine.put(new Block<>(status, data));
    }

    /**
     * 送入状态
     */
    public void putStatus(S status) throws InterruptedException {
        this.pipeLine.put(new Block<>(status, this.defaultData));
    }

    /**
     * 送入多个状态
     */
    public void putStatus(int times, S status) throws InterruptedException {
        /* 输入状态 */
        for (int i = 0; i < times; i++) {
            this.pipeLine.put(new Block<>(status, this.defaultData));
        }
    }

    /**
     * 获得数据
     */
    public Block<S, D> get() throws InterruptedException {
        return this.pipeLine.take();
    }

    /**
     * 获得状态
     */
    public S getStatus() throws InterruptedException {
        return this.pipeLine.take().getStatus();
    }

    /**
     * 获取数据
     */
    public D getData() throws InterruptedException {
        return this.pipeLine.take().getData();
    }

    /**
     * 关闭数据通路
     */
    public void clear() {
        this.pipeLine.clear();
    }
}
