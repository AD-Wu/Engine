package com.x.doraemon.common.web;

import java.io.Serializable;

/**
 * 分页查询参数
 *
 * @author AD
 * @date 2022/1/19 17:06
 */
public class Param<T> implements Serializable {

    /**
     * 当前页
     *
     * @mock 1
     * @required
     */
    private int current;
    /**
     * 每页大小
     *
     * @mock 10
     * @required
     */
    private int size;
    /**
     * 数据
     *
     * @mock
     */
    private T data;

    public Param() {
    }

    public Param(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public int getCurrent() {
        return Math.max(current, 1);
    }

    public void setCurrent(int current) {
        this.current = Math.max(current, 1);
    }

    public int getSize() {
        int max = Math.max(size, 0);
        return Math.min(max, 10000);
    }

    public void setSize(int size) {
        this.size = Math.max(size, 0);
        this.size = Math.min(this.size, 10000);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
