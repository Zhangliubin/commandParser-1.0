package edu.sysu.pmglab.threadPool;

/**
 * @Data        :2021/02/11
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :数据块
 */

public class Block<S, D> {
    private final S status;
    private final D data;

    Block(S status, D data) {
        this.status = status;
        this.data = data;
    }

    public D getData() {
        return data;
    }

    public S getStatus() {
        return status;
    }
}