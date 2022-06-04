package com.x.jdk8.locale;

import com.x.doraemon.Printer;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author AD
 * @date 2022/5/30 10:26
 */
public class NumberDemo {

    public static void main(String[] args) throws Exception {
        format();
    }

    private static void format() {
        double price = 123456.78;
        // 德国
        Locale germany = Locale.GERMANY;
        NumberFormat germanNumber = NumberFormat.getNumberInstance(germany);
        NumberFormat germanCurrency = NumberFormat.getCurrencyInstance(germany);
        NumberFormat germanPercent = NumberFormat.getPercentInstance(germany);
        String germanPrice = germanCurrency.format(price);
        System.out.println(germany.getDisplayName() + "\t" + germanPrice);
        // 美国
        Locale us = Locale.US;
        NumberFormat usCurrency = NumberFormat.getCurrencyInstance(us);
        String usPrice = usCurrency.format(price);
        System.out.println(us.getDisplayName() + "\t" + usPrice);
        // 系统默认
        Locale def = Locale.getDefault();
        NumberFormat defCurrency = NumberFormat.getCurrencyInstance(def);
        String defPrice = defCurrency.format(price);
        System.out.println(def.getDisplayName() + "\t" + defPrice);
        System.out.println("--------------------------------------");
        // 获取所有支持的Locale
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String s = locale.toString();
            if (s.contains("_")) {
                NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
                String format = currency.format(price);
                System.out.println(locale.getDisplayName() + "\t" + format);
            }

        }

    }

    /**
     * 获取当前系统默认的Locale
     */
    private static void getDefaultLocale() {
        Locale defLocale = Locale.getDefault();
        String displayName = defLocale.getDisplayName();
        System.out.println(displayName);
    }

    /**
     * 打印Locale里的语言标签
     * @throws Exception
     */
    private static void printLanguageTag() throws Exception {
        Field[] fields = Locale.class.getFields();
        Printer printer = new Printer();
        for (Field field : fields) {
            Object o = field.get(null);
            if (o instanceof Locale) {
                Locale locale = (Locale) o;
                String languageTag = locale.toLanguageTag();
                String displayName = locale.getDisplayName();
                printer.add(displayName, languageTag);
            }
        }
        printer.print();
    }

    private static void languageAndCountry() {
        // ----------------------- 语言 -----------------------
        Locale chinese = Locale.CHINESE;
        Locale japanese = Locale.JAPANESE;
        Locale english = Locale.ENGLISH;
        Locale italian = Locale.ITALIAN;
        Locale french = Locale.FRENCH;
        Locale simplifiedChinese = Locale.SIMPLIFIED_CHINESE;
        Locale traditionalChinese = Locale.TRADITIONAL_CHINESE;
        // ----------------------- 国家 -----------------------
        Locale china = Locale.CHINA;
        Locale japan = Locale.JAPAN;
        Locale us = Locale.US;
        Locale italy = Locale.ITALY;
        Locale france = Locale.FRANCE;

    }
}
