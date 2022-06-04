package com.x.algs2.priorityQueue;

/**
 * @author AD
 * @date 2021/12/14 19:59
 */
public class MaxPQ<Key extends Comparable<Key>> implements IPriorityQueue<Key> {

    private Key pq[];
    private int N = 0;

    public MaxPQ(int maxN) {
        this.pq = (Key[]) new Comparable[maxN];
    }

    @Override
    public void insert(Key v) {
        pq[++N] = v;
        swim(N);
    }

    @Override
    public Key max() {
        if (isEmpty()) {
            return null;
        }
        return pq[1];
    }

    @Override
    public Key delMax() {
        if (isEmpty()) {
            return null;
        }
        // 从根节点获取最大元素
        Key max = pq[1];
        // 将其和最后一个节点交换，并将较小size
        exch(1, N--);
        // 防止对象游离
        pq[N + 1] = null;
        // 恢复堆的有序性
        sink(1);
        return max;
    }

    @Override
    public boolean isEmpty() {
        return N == 0;
    }

    @Override
    public int size() {
        return N;
    }

    public Key[] getElement(){
        return pq;
    }

    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exch(int i, int j) {
        Key t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;
    }

    private void swim(int k) {
        // k有效且k的父节点比k小
        while (k > 1 && less(k / 2, k)) {
            exch(k / 2, k);
            k = k / 2;
        }
    }

    private void sink(int k) {
        // 判断子节点是否越界
        while (2 * k <= N) {
            int j = 2 * k;
            // 判断左、右节点（此处j不能等于N）
            if (j < N && less(j, j + 1)) {
                j++;
            }
            // 判断是否下沉至最底端
            if (!less(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }
}
