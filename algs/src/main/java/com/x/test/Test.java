package com.x.test;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/24 12:48
 */
public class Test {

    public static void main(String[] args) {
        int abs = Math.abs(-2147483648);
        // abs = -2147483648。这是整形溢出的结果
        System.out.println(abs);
        System.out.println(Integer.MAX_VALUE);
        System.out.println("######################");
        // 产生的结果是无穷大，如果是1/0则会报错。
        double a = 1.0/0.0;
        double b = -1.0/0.0;
        System.out.println(a);
        System.out.println(b);
        System.out.println("######################");
        System.out.println(Double.POSITIVE_INFINITY);
        System.out.println(Double.NEGATIVE_INFINITY);
    }
}
