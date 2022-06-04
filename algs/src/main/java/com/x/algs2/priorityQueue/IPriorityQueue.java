package com.x.algs2.priorityQueue;

/**
 * 优先级队列接口
 *
 * @author AD
 * @date 2021/12/14 19:55
 */
public interface IPriorityQueue<Key extends Comparable<Key>> {

    /**
     * 向优先队列插入一个元素
     *
     * @param v
     */
    void insert(Key v);

    /**
     * 返回最大元素
     */
    Key max();

    /**
     * 删除并返回最大元素
     */
    Key delMax();

    /**
     * 返回队列是否为空
     */
    boolean isEmpty();

    /**
     * 返回优先队列中元素个数
     */
    int size();
}
