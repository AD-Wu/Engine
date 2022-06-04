package com.x.common.stack;

/**
 * 链表是递归结构，栈、队列、背包都是这种结构，只是背包不注重顺序
 *
 * @author AD
 * @date 2021/11/25 19:13
 */
public class ListStack<T> implements IStack<T> {

    private Node first;
    private long size;

    @Override
    public void push(T t) {
        if (t == null) {
            return;
        }
        Node next = new Node();
        next.t = t;
        next.next = first;
        first = next;
        size++;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T t = first.t;
        first = first.next;
        size--;
        return t;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public long size() {
        return size;
    }

    private class Node {

        private T t;
        private Node next;
    }
}
