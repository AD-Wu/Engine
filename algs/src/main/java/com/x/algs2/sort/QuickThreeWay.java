package com.x.algs2.sort;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/12/18 19:41
 */
public class QuickThreeWay extends Quick {
    // 5~15都是ok的
    int M = 7;

    Insertion insertion = new Insertion();
    @Override
    protected void sort(Comparable[] a, int low, int high) {
        if (high <= low + M) {
            insertion.sort(a, low, high);
            return;
        }
        int lessThan = low, i = low + 1, greatThan = high;
        Comparable v = a[low];
        while (i <= greatThan) {
            int cmp = a[i].compareTo(v);
            if (cmp < 0) {
                exch(a, lessThan++, i++);
            } else if (cmp > 0) {
                exch(a, i, greatThan--);
            } else {
                i++;
            }
        }
        // 排序左半边
        sort(a, low, lessThan - 1);
        // 排序右半边
        sort(a, greatThan + 1, high);
    }
}
