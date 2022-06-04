package com.x.algs2.sort;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/12/18 18:48
 */
public class QuickInsertion extends Quick {

    // 5~15都是ok的
    int M = 7;

    Insertion insertion = new Insertion();

    public QuickInsertion(int m){
        this.M = m;
    }

    /**
     * 小数组时，插入排序更快
     */
    @Override
    protected void sort(Comparable[] a, int low, int high) {
        if (high <= low + M) {
            insertion.sort(a, low, high);
            return;
        }
        // 切分，把a[j]放到合适的位置
        int j = partition(a, low, high);
        // 排序左半边
        sort(a, low, j - 1);
        // 排序右半边
        sort(a, j + 1, high);
    }
}
