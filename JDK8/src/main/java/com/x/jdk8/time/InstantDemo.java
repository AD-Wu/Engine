package com.x.jdk8.time;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author AD
 * @date 2022/5/10 21:52
 */
public class InstantDemo {

    public static void main(String[] args) throws Exception {
        getRandom(10);
    }

   private static int[] getRandom(int count){
       SecureRandom sr = new SecureRandom();

       int[] result = new int[count];
       for (int i = 0; i < count; i++) {
           int r = sr.nextInt(20);
           System.out.println(r);
       }
       return result;
   }

    private static void calTime() throws InterruptedException {
        Instant start = Instant.now();
        TimeUnit.SECONDS.sleep(1);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration.toMillis());
    }

    private static void instantDemo() {
        // Instant表示时间线上的某个点,是绝对时间.原点是格林威治时间,即本初子午线,北京是东8区
        Instant now = Instant.now();
        System.out.println(now);
        System.out.println(LocalDateTime.now());
    }


}
