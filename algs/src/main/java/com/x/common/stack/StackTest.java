package com.x.common.stack;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author AD
 * @date 2021/11/25 15:15
 */
public class StackTest {

    private static final int count = 100_0000;

    // 结论: 性能差不多
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                IStack<Integer> arrayStack = new ListStack<>();
                testStack(arrayStack);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                IStack<Integer> arrayStack = new ArrayStack<>();
                testStack(arrayStack);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Stopwatch stop = Stopwatch.createStarted();
                List<Integer> stack = new LinkedList<>();
                for (int i = 0; i < count; i++) {
                    stack.add(i);
                }
                ListIterator<Integer> it = stack.listIterator();
                while (it.hasPrevious()) {
                    it.previous();
                }
                long time = stop.elapsed(TimeUnit.MICROSECONDS);
                String name = stack.getClass().getSimpleName();
                System.out.println(name + "耗时：" + time + "微秒");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Stopwatch stop = Stopwatch.createStarted();
                List<Integer> stack = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    stack.add(i);
                }
                for (int i = stack.size() - 1; i > 0; i--) {
                    stack.get(i);
                }
                long time = stop.elapsed(TimeUnit.MICROSECONDS);
                String name = stack.getClass().getSimpleName();
                System.out.println(name + "耗时：" + time + "微秒");
            }
        }).start();

    }

    private static void testStack(IStack<Integer> stack) {
        Stopwatch stop = Stopwatch.createStarted();
        for (int i = 0; i < count; i++) {
            stack.push(i);
        }
        while (!stack.isEmpty()) {
            stack.pop();
        }
        long time = stop.elapsed(TimeUnit.MICROSECONDS);
        String name = stack.getClass().getSimpleName();
        System.out.println(name + "耗时：" + time + "微秒");
    }

}
