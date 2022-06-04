package com.x.jdk8.locale;

import com.x.doraemon.Printer;
import com.x.doraemon.Strings;
import java.util.Currency;
import java.util.Locale;

/**
 * 货币处理
 * @author AD
 * @date 2022/5/31 10:09
 */
public class CurrencyDemo {

    public static void main(String[] args) {
        getCurrencyCode();
    }


    private static void getCurrencyCode() {
        Printer printer = new Printer();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (Strings.isNull(locale.getCountry())) {
                continue;
            }
            Currency cur = Currency.getInstance(locale);
            // 国家代码
            String countryDode = locale.getCountry();
            // 语言和国家
            String displayName = locale.getDisplayName();
            // 货币的 ISO 4217 代码
            String code = cur.getCurrencyCode();
            // 货币符,如:美元($),人民币(￥)
            String symbol = cur.getSymbol(locale);
            //
            printer.add(countryDode, code, symbol, locale.toString(), displayName);
        }
        printer.print();
    }

}
