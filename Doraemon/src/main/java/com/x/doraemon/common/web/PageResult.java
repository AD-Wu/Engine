package com.x.doraemon.common.web;

/**
 * @author AD
 * @date 2022/1/21 13:00
 */
public class PageResult<T> {

    /**
     * 总条数
     */
    private long total;
    /**
     * 当前页
     */
    private long current;
    /**
     * 每页显示条数，默认 10
     */
    private long size;
    /**
     * 分页数
     */
    private long pages;

    /**
     * 结果
     */
    private Result<T> result;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public Result<T> getResult() {
        return result;
    }

    public void setResult(Result<T> result) {
        this.result = result;
    }
}
