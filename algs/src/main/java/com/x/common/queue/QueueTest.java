package com.x.common.queue;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/25 20:02
 */
public class QueueTest {

    private static final int count = 10;

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Stopwatch stop = Stopwatch.createStarted();
                IQueue<Integer> queue = new ListQueue<>();
                for (int i = 0; i < count; i++) {
                    queue.enqueue(i);
                }
                while (!queue.isEmpty()) {
                    System.out.print(queue.dequeue());
                }
                long elapsed = stop.elapsed(TimeUnit.MICROSECONDS);
                System.out.println(queue.getClass().getSimpleName() + "耗时：" + elapsed + "微秒");
            }
        }).start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Stopwatch stop = Stopwatch.createStarted();
                IQueue<Integer> queue = new ListQueue<>();
                for (int i = 0; i < count; i++) {
                    queue.enqueue(i);
                }
                while (!queue.isEmpty()) {
                    System.out.print(queue.dequeue());
                }
                long elapsed = stop.elapsed(TimeUnit.MICROSECONDS);
                System.out.println(queue.getClass().getSimpleName() + "耗时：" + elapsed + "微秒");
            }
        }).start();
    }
}
