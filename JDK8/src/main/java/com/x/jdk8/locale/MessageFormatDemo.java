package com.x.jdk8.locale;

import com.x.doraemon.Printer;
import com.x.doraemon.Strings;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * @author AD
 * @date 2022/5/31 13:54
 */
public class MessageFormatDemo {

    public static void main(String[] args) {
        formatByLocale();
    }

    private static void formatByLocale() {
        String pattern = "My name is {0}, age is {1,number,currency}, birthday is {2}";
        Printer printer = new Printer();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (Strings.isNull(locale.getCountry())) {
                continue;
            }
            String formatted = MessageFormat.format(pattern,
                                                    "Sunday",
                                                    123456.789,
                                                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
                                                        .format(ZonedDateTime.now()));
            printer.add(locale.getDisplayName(), formatted);
        }
        printer.print();
    }

    private static void format() {
        String patten = "My name is {0}, age is {1}, birthday is {2}";
        String formatted = MessageFormat.format(patten,
                                                "Sunday",
                                                123456.789,
                                                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                                    .withLocale(Locale.getDefault()).format(ZonedDateTime.now()));
        System.out.println(formatted);
    }
}
