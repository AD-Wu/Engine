package com.x.common.bag;

import com.google.common.base.Stopwatch;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/25 21:03
 */
public class BagTest {

    private static final int count = 10_0000;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Stopwatch stop = Stopwatch.createStarted();
                IBag<Integer> bag = new ListBag<>();
                for (int i = 0; i < count; i++) {
                    bag.add(i);
                }
                Iterator<Integer> it = bag.iterator();
                while (it.hasNext()) {
                    it.next();
                }
                long time = stop.elapsed(TimeUnit.MICROSECONDS);
                String name = bag.getClass().getSimpleName();
                System.out.println(name + "耗时：" + time + "微秒");
            }
        }).start();
    }

}
