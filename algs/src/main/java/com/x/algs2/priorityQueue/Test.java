package com.x.algs2.priorityQueue;

/**
 * TODO
 *
 * @author AD
 * @date 2021/12/15 16:47
 */
public class Test {

    public static void main(String[] args) {
        MaxPQ<Integer> pq = new MaxPQ<>(20);
        for (int i = 1; i < 20; i++) {
            pq.insert(i);
        }
        printElement(pq);
        System.out.println("\n=======================");
        while (!pq.isEmpty()){
            System.out.println(pq.delMax()+" ");
            printElement(pq);
            System.out.println("\n===========");
        }
        // System.out.println(integer);
        // printElement(pq);
    }

    private static void printElement(MaxPQ<Integer> pq) {
        Comparable[] element = pq.getElement();
        int i = 0;
        int count = 0;
        for (Comparable c : element) {
            if (c != null) {
                System.out.print(c + " ");
                count++;
                if (count == (1 << i)) {
                    System.out.println();
                    i++;
                    count = 0;
                }
            }
        }

    }
}
