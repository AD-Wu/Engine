package com.x.algs2.sort;

import com.google.common.base.Stopwatch;
import com.x.algs.StdRandom;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author AD
 * @date 2021/12/13 11:11
 */
public class Test {

    public static void main(String[] args) {
        int count = 100;
        int arrLength = 100000;
        int randomRange = 1000;
        int m5 = 5, m7 = 7, m10 = 10, m13 = 13, m15 = 15;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quick(arr);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quick average=" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickInsertion(arr, m5);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickInsertion average, 【m5】 =" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickInsertion(arr, m7);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickInsertion average, 【m7】 =" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickInsertion(arr, m10);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickInsertion average, 【m10】 =" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickInsertion(arr, m13);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickInsertion average, 【m13】 =" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickInsertion(arr, m15);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickInsertion average, 【m15】 =" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = quickThreeWay(arr);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("quickThreeWay average=" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SecureRandom sr = new SecureRandom();
                    Integer arr[] = new Integer[arrLength];
                    for (int j = 0, L = arr.length; j < L; j++) {
                        arr[j] = sr.nextInt(randomRange);
                    }
                    long time = jdkSort(arr);
                    times.add(time);
                }
                double sum = 0;
                for (long t : times) {
                    sum += t;
                }
                double average = sum / count;
                System.out.println("jdkSort average=" + average);
                System.out.println(times);
                System.out.println("--------------------------");
            }
        }).start();

    }

    public static void insertion(Comparable[] arr) {
        StdRandom.shuffle(arr);
        Insertion insertion = new Insertion();
        Stopwatch stopwatch = Stopwatch.createStarted();
        insertion.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("insertion:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        System.out.println("--------------------------------");

    }

    public static void selection(Comparable[] arr) {
        StdRandom.shuffle(arr);
        Selection selection = new Selection();
        Stopwatch stopwatch = Stopwatch.createStarted();
        selection.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("selection:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        System.out.println("--------------------------------");
    }

    public static void shell(Comparable[] arr) {
        StdRandom.shuffle(arr);
        Shell shell = new Shell();
        Stopwatch stopwatch = Stopwatch.createStarted();
        shell.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("shell:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        System.out.println("--------------------------------");
    }

    public static long jdkSort(Comparable[] arr) {
        StdRandom.shuffle(arr);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Arrays.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        // System.out.println("jdkSort:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        // System.out.println("--------------------------------");
        return elapsed;
    }

    public static long quick(Comparable[] arr) {
        Quick quick = new Quick();
        Stopwatch stopwatch = Stopwatch.createStarted();
        quick.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        // System.out.println("quick:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        // System.out.println("--------------------------------");
        return elapsed;
    }

    public static long quickInsertion(Comparable[] arr, int m) {
        QuickInsertion quick = new QuickInsertion(m);
        Stopwatch stopwatch = Stopwatch.createStarted();
        quick.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        // System.out.println("quickInsertion:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        // System.out.println("--------------------------------");
        return elapsed;
    }

    public static long quickThreeWay(Comparable[] arr) {
        QuickThreeWay quick = new QuickThreeWay();
        Stopwatch stopwatch = Stopwatch.createStarted();
        quick.sort(arr);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        // System.out.println("quickThreeWay:" + elapsed + "ms");
        // System.out.println(Arrays.toString(arr));
        // System.out.println("--------------------------------");
        return elapsed;
    }

    private static Integer[] copy(Integer[] arr) {
        Integer[] copy = Arrays.copyOf(arr, arr.length);
        return copy;
    }
}
