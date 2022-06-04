package com.x.common.stack;

import java.util.Iterator;

/**
 * 数组栈
 *
 * @author AD
 * @date 2021/11/25 14:08
 */
public class ArrayStack<T> implements IStack<T>, Iterable<T> {

    private T nodes[];

    private int index;

    public ArrayStack() {
        this.nodes = (T[]) new Object[16];
    }

    @Override
    public void push(T t) {
        if (index == nodes.length) {
            resize(nodes.length * 2);
        }
        nodes[index++] = t;
    }

    @Override
    public T pop() {
        // 此处要注意发生内存泄露，因为是数组管理的内存，数组还保留这该对象的引用。
        T t = nodes[--index];
        // 防止内存泄露，方便对象被垃圾回收器回收
        nodes[index] = null;
        // 判断数组是否需要减容
        if (index > 0 && index <= nodes.length / 4) {
            resize(nodes.length / 4);
        }
        return t;
    }

    @Override
    public boolean isEmpty() {
        return index == 0;
    }

    @Override
    public long size() {
        return index;
    }

    private void resize(int maxSize) {
        T temp[] = (T[]) new Object[maxSize];
        // 注意这里是index，以当前容量为最大循环次数
        for (int i = 0; i < index; i++) {
            temp[i] = nodes[i];
        }
        this.nodes = temp;
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator();
    }

    private class StackIterator implements Iterator<T> {


        @Override
        public boolean hasNext() {
            return index > 0;
        }

        @Override
        public T next() {
            return nodes[--index];
        }
    }

}
