package com.x.algs2.sort;

import com.x.algs.StdRandom;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 插入排序
 *
 * @author AD
 * @date 2021/12/13 10:37
 */
public class Insertion extends BaseSort {

    @Override
    public void sort(Comparable[] a) {
        int L = a.length;
        for (int i = 1; i < L; i++) {
            for (int j = i; j > 0; j--) {
                if (less(a[j], a[j - 1])) {
                    exch(a, j, j - 1);
                }
            }
        }
    }

    public void sort(Comparable[] a, int low, int high) {
        for (int i = low + 1; i < high + 1; i++) {
            for (int j = i; j > low; j--) {
                if (less(a[j], a[j - 1])) {
                    exch(a, j, j - 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        SecureRandom sr = new SecureRandom();
        Integer arr[] = new Integer[10000];
        for (int i = 0, L = arr.length; i < L; i++) {
            arr[i] = sr.nextInt(L);
        }
        Insertion insertion = new Insertion();
        insertion.sort(arr);
        System.out.println(Arrays.toString(arr));
        StdRandom.shuffle(arr);
        insertion.sort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }
}
