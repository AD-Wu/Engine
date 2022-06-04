package com.x.jdk8.stream;

import com.x.doraemon.Printer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author AD
 * @date 2022/4/18 22:04
 */
public class StreamDemo {

    static String[] str = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};


    private static void joining() {
        String collect = Stream.of(str).collect(Collectors.joining());
        System.out.println(collect);
        collect = Stream.of(str).collect(Collectors.joining(","));
        System.out.println(collect);
    }

    private static void toSet() {
        TreeSet<String> collect = Stream.of(str).collect(Collectors.toCollection(TreeSet::new));
        System.out.println(collect);
    }

    private static void max() {
        Optional<String> max = Stream.of(str).max(Comparator.naturalOrder());
        System.out.println(max.get());

        Optional<String> min = Stream.of(str).max(Comparator.reverseOrder());
        System.out.println(min.get());
    }

    private static void locale() {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        // 当key重复时,会抛出异常
        // Map<String, String> collect = locales.collect(Collectors.toMap(l -> l.getDisplayLanguage(),l -> l.getDisplayLanguage(l)));

        //
        Map<String, String> collect = locales.collect(Collectors.toMap(l -> l.getDisplayLanguage(),
                                                                       l -> l.getDisplayLanguage(l),
                                                                       (existingValue, newValue) -> existingValue,
                                                                       TreeMap::new));

        Printer printer = new Printer();
        collect.forEach((k, v) -> {
            printer.add(k, v);
        });
        printer.print();

    }

    private static void groupBy() {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, List<Locale>> collect = locales.collect(Collectors.groupingBy(Locale::getCountry));
        collect.forEach((k, v) -> {
            System.out.println(k + "  " + v);
        });
    }

    private static void groupByToCounting() {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Long> collect = locales.collect(Collectors.groupingBy(Locale::getCountry, Collectors.counting()));
        collect.forEach((k, v) -> {
            System.out.println(k + "  " + v);
        });
    }

    private static void groupByToSet() {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<Locale>> collect = locales.collect(Collectors.groupingBy(Locale::getCountry, Collectors.toSet()));
        collect.forEach((k, v) -> {
            System.out.println(k + "  " + v);
        });
    }

    private static void reduce() {
        // [1,100)范围的值进行求和
        OptionalInt reduce = IntStream.range(1, 101).reduce(Integer::sum);
        System.out.println(reduce.getAsInt());
    }

    public static void main(String[] args) {
        reduce();
    }
}
