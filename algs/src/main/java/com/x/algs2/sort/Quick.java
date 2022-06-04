package com.x.algs2.sort;

import com.x.algs.StdRandom;

/**
 * 快速排序
 *
 * @author AD
 * @date 2021/12/18 12:14
 */
public class Quick extends BaseSort {

    @Override
    public void sort(Comparable[] a) {
        // 洗牌，快速排序对于随机性数组更友好
        StdRandom.shuffle(a);
        // 排序
        sort(a, 0, a.length - 1);
    }

    /**
     * 该方法的关键在于切分，这个过程使得数组满足下面三个条件：
     * <p>
     * 1、对于某个j, a[j]已经排定；
     * <p>
     * 2、a[lo]到a[j-1]中的所有元素都不大于a[j]；
     * <p>
     * 3、 a[j+1]到a[hi]中的所有元素都不小于a[j]。
     */
    protected void sort(Comparable[] a, int low, int high) {
        if (high <= low) {
            return;
        }
        // 切分，把a[j]放到合适的位置
        int j = partition(a, low, high);
        // 排序左半边
        sort(a, low, j - 1);
        // 排序右半边
        sort(a, j + 1, high);
    }


    /**
     * 一般策略是先随意地取a[lo]作为切分元素，即那个将会被排定的元素。
     * <p>
     * 从数组的左端开始,向【右】扫描直到找到一个【大于等于】它的元素。
     * <p>
     * 从数组的右端开始,向【左】扫描直到找到一个【小于等于】它的元素。
     * <p>
     * 这两个元素显然是没有排定的，因此我们交换它们的位置。
     */
    protected int partition(Comparable[] a, int low, int high) {
        // 假定第一个数为中位
        Comparable v = a[low];
        // 定义左、右2个指针
        int i = low, j = high + 1;

        while (true) {
            // 向【右】扫描【大于等于】v的元素
            while (less(a[++i], v)) {
                if (i == high) {
                    break;
                }
            }
            // 向【左扫】描【小于等于】v的元素
            while (less(v, a[--j])) {
                if (j == low) {
                    break;
                }

            }
            // 左、右指针相遇，即扫描完毕
            if (i >= j) {
                break;
            }
            // 交换2个值
            exch(a, i, j);
        }
        // 将v（中间元素）放入正确的位置(j是高位指针，这样v所在位置的左边就都小于等于v)
        exch(a, low, j);
        // a[low...j-1] <= a[j] <= a[j+1...high]
        return j;
    }
}
