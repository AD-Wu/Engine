package com.x.algs2.sort;

import com.x.algs.StdOut;

/**
 * TODO
 *
 * @author AD
 * @date 2021/12/8 17:16
 */
public abstract class BaseSort {

    public abstract void sort(Comparable[] a);


    protected boolean less(Comparable next, Comparable cur) {
        return next.compareTo(cur) < 0;
    }

    protected void exch(Comparable[] a, int i, int j) {
        Comparable t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    protected void show(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.print(a[i] + " ");
        }
        StdOut.println();
    }

    protected boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (less(a[i], a[i - 1])) {
                return false;
            }
        }
        return true;
    }

}
