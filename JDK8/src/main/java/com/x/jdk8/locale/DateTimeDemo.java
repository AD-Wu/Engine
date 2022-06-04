package com.x.jdk8.locale;

import com.x.doraemon.Printer;
import com.x.doraemon.Strings;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * 当格式化日期和时间时，需要考虑4个与Locale相关的问题:
 * 1.月份和星期应该用本地语言来表示。
 * 2.年月日的顺序要符合本地习惯。
 * 3.公历可能不是本地首选的日期表示方法。
 * 4.必须要考虑本地的时区。
 * @author AD
 * @date 2022/5/31 11:27
 */
public class DateTimeDemo {

    public static void main(String[] args) {
        defaultFirstOfWeek();
    }


    private static void defaultFirstOfWeek() {
        Locale locale = Locale.getDefault();
        DayOfWeek first = WeekFields.of(locale).getFirstDayOfWeek();
        System.out.println(first + "\t" + locale.getDisplayName());
    }

    private static void firstOfWeek() {
        Printer printer = new Printer();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (Strings.isNull(locale.getCountry())) {
                continue;
            }
            DayOfWeek first = WeekFields.of(locale).getFirstDayOfWeek();
            printer.add(first, locale.getDisplayName());
        }
        printer.print();
    }

    private static void displayDefault() {
        display(Locale.getDefault());
    }

    private static void displayAll() {
        for (Locale locale : Locale.getAvailableLocales()) {
            display(locale);
        }
    }

    private static void display(Locale locale) {
        ZonedDateTime now = ZonedDateTime.now();
        Printer printer = new Printer();
        FormatStyle[] styles = FormatStyle.values();
        if (Strings.isNull(locale.getCountry())) {
            return;
        }
        for (FormatStyle style : styles) {
            DateTimeFormatter date = DateTimeFormatter.ofLocalizedDate(style).withLocale(locale);
            String dateFormat = date.format(now);
            printer.add(style, "Date", dateFormat, locale.getDisplayName());
        }
        printer.add(" ", " ", " ", " ");
        for (FormatStyle style : styles) {
            DateTimeFormatter time = DateTimeFormatter.ofLocalizedTime(style).withLocale(locale);
            String timeFormat = time.format(now);
            printer.add(style, "Time", timeFormat, locale.getDisplayName());
        }
        printer.add(" ", " ", " ", " ");
        for (FormatStyle style : styles) {
            DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(style).withLocale(locale);
            String dateTimeFormat = dateTime.format(now);
            printer.add(style, "DateTime", dateTimeFormat, locale.getDisplayName());
        }
        printer.add(" ", " ", " ", " ");
        printer.print();

    }
}
