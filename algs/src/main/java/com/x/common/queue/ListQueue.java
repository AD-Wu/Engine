package com.x.common.queue;

/**
 * TODO
 *
 * @author AD
 * @date 2021/11/25 19:34
 */
public class ListQueue<T> implements IQueue<T> {

    private Node first;
    private T t;
    private Node last;
    private long size;

    public ListQueue() {
        this.first = new Node();

    }

    @Override
    public void enqueue(T t) {
        if (t == null) {
            return;
        }
        if (isEmpty()) {
            last = first;
        }
        Node next = new Node();
        last.t = t;
        last.next = next;
        last = next;
        size++;
    }

    @Override
    public T dequeue() {
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

    private class Node {

        private T t;
        private Node next;
    }
}
