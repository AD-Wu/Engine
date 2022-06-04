package com.x.common.bag;

import com.x.common.stack.IStack;
import com.x.common.stack.ListStack;
import java.util.Iterator;

/**
 * TODO
 *
 * @author AD
 * @date 2021/11/25 20:57
 */
public class ListBag<T> implements IBag<T> {

    private IStack<T> stack = new ListStack<>();

    @Override
    public void add(T t) {
        stack.push(t);
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public long size() {
        return stack.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new BagIterator();
    }

    private class Node {

        private T t;
        private Node next;
    }

    private class BagIterator implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return stack.size() > 0;
        }

        @Override
        public T next() {
            return stack.pop();
        }
    }
}
