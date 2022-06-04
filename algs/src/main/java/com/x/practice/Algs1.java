package com.x.practice;

import java.math.BigDecimal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/24 13:15
 */
// @Component
public class Algs1 implements InitializingBean {

    @Autowired
    private Algs1 algs;

    private static final int WIDTH = 35;

    @Override
    public void afterPropertiesSet() throws Exception {
        algs.test_1_1_1();
        algs.test_1_1_2();

        algs.test_1_1_9(567);
        algs.testBinaryString(567);
        algs.testForBinary(567);
        algs.test_1_1_9(Integer.MIN_VALUE);
        algs.testBinaryString(Integer.MIN_VALUE);
        algs.testForBinary(Integer.MIN_VALUE);
    }

    public void test_1_1_1() {
        int a = (0 + 15) / 2;
        // 等价于 2 * 100.0000001
        double b = 2.0e-6 * 100000000.1;
        boolean c = true && false || true && true;
        System.out.printf("%" + WIDTH + "s %d\n", "(0 + 15) / 2 =", a);
        System.out.printf("%" + WIDTH + "s %.7f\n", "2.0e-6 * 100000000.1 =", b);
        System.out.printf("%" + WIDTH + "s %s\n", "true && false || true && true =", c);
    }

    public void test_1_1_2() {
        double a = (1 + 2.36) / 2;
        double b = 1 + 2 + 3 + 4.0;
        boolean c = 4.1 >= 4;
        String d = 1 + 2 + "3";
        System.out.printf("%" + WIDTH + "s\n", "(1 + 2.36) / 2 = " + a);
        System.out.printf("%" + WIDTH + "s\n", "1 + 2 + 3 + 4.0 = " + b);
        System.out.printf("%" + WIDTH + "s\n", "4.1 >= 4 = " + c);
        System.out.printf("%" + WIDTH + "s\n", "1 + 2 + \"3\" = " + d);
    }

    public void test_1_1_9(int a) {
        String s = "";
        while (a != 0) {
            s = (a & 1) + s;
            a = a >>> 1;
        }
        System.out.println("二进制表示(自我算法实现):" + s);
    }

    public void testBinaryString(int a) {
        System.out.println("二进制表示(JDK算法实现):" + Integer.toBinaryString(a));
    }

    public void testForBinary(int a) {
        String s = "";
        for (int n = a; n != 0; n /= 2) {
            s = (n % 2) + s;
        }
        System.out.println("二进制表示(for循环算法实现):" + Integer.toBinaryString(a));
    }

    public void testHigh(BigDecimal h) {
        long count = 2;
        while (h.doubleValue() > 0) {
            System.out.println(h.doubleValue());
            h = h.divide(BigDecimal.valueOf(2));
            count++;
        }
        System.out.println(count);
    }

}
