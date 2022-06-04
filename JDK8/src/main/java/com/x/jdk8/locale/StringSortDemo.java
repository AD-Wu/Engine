package com.x.jdk8.locale;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AD
 * @date 2022/5/31 13:40
 */
public class StringSortDemo {

    public static void main(String[] args) {
        sort();
    }

    private static void sort() {
        // 获取默认的字符排序器,实现了Comparator接口,所以是一个比较器
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.IDENTICAL);
        List<String> strings = new ArrayList<>();
        strings.add("a");
        strings.add("A");
        strings.add("r");
        strings.add("T");
        strings.add("g");
        strings.add("H");
        strings.add("I");
        strings.add("l");
        strings.add(";");
        strings.add("吴");
        strings.add("8");
        System.out.println("before:" + strings);
        strings.sort(collator);
        System.out.println("after:" + strings);
    }
}
