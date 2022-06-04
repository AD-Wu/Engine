package com.x.algs2.sort;

/**
 * 希尔排序
 *
 * @author AD
 * @date 2021/12/13 15:56
 */
public class Shell extends BaseSort {

    @Override
    public void sort(Comparable[] a) {
        int L = a.length;
        int h = 1;
        while (h < L / 3) {
            h = h * 3 + 1;// 1,4,13,40,121,364,1093...
        }
        // 9,8,7,6 | 5,4,3,2 | 1,0
        while (h >= 1) {
            for (int i = h; i < L; i++) {
                for (int j = i; j >= h && less(a[j], a[j - h]); j = j - h) {
                    exch(a, j, j - h);
                }
            }
            h = h / 3;
        }
    }
}
