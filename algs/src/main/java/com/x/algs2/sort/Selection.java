package com.x.algs2.sort;

/**
 * 选择排序
 *
 * @author AD
 * @date 2021/12/8 20:33
 */
public class Selection extends BaseSort {

    @Override
    public void sort(Comparable[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i + 1; j < N; j++) {
                if (less(a[j], a[min])) {
                    min = j;
                }
            }
            exch(a, i, min);
        }
    }
}
