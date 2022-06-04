package com.x.isearch.config;

/**
 * @author AD
 * @date 2022/1/19 12:16
 */
public class MyPage {

    /**
     * 当前页
     * @mock 1
     */
    private int current;

    /**
     * 每页大小
     * @mock 10
     */
    private int size;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
