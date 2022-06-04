package com.x.doraemon;

import java.lang.reflect.Field;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Desc 日期时间工具类
 * @Date 2020/9/17 21:08
 * @Author AD
 */
public final class DateTimes {

    public static void main(String[] args) throws Exception {
        registerPattern("yyyyMMdd HH:mm:ss");
        String f = format("20220409 12:40:22");
        System.out.println(f);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        System.out.println("====================");
        for (Formatter formatter : Formatter.values()) {
            String format = format(now, formatter);
            LocalDateTime localDateTime = toLocalDateTime(format);
            Date date = toDate(format);
            System.out.println(format);
            System.out.println(localDateTime);
            System.out.println(date);
            System.out.println("---------------");
        }
    }

    // ------------------------ 变量定义 ------------------------

    private static final Set<String> patterns;

    private static final Set<DateTimeFormatter> localDateTimeFormatters;

    // ------------------------ 构造方法 ------------------------
    private DateTimes() {
    }
    // ------------------------ 方法定义 ------------------------

    /**
     * 当前时间: yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String now() {
        return now(Formatter.DEFAULT);
    }

    /**
     * 当前时间: yyyy-MM-dd HH:mm:ss +08:00
     * @param timeZone 是否带时区
     * @return
     */
    public static String now(boolean timeZone) {
        try {
            return timeZone ? now(Formatter.DATE_TIME_ZONE) : now(Formatter.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeZone ? OffsetDateTime.now().toString().replace("T", " ").replace("+", " +")
            : LocalDateTime.now().toString().replace("T", " ");
    }

    /**
     * 当前时间
     * @param formatter 格式化对象
     * @return
     */
    public static String now(Formatter formatter) {
        try {
            return format(LocalDateTime.now(), formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LocalDateTime.now().toString().replace("T", " ");
    }


    /**
     * 自动将对象转为日期时间类（包括日期和时间，在DateTimeFormat里找对应的样式）
     * @param dateTime 字符串、Date、LocalDateTime、int、long对象
     * @return LocalDateTime对象
     * @throws Exception 没有样式异常
     */
    public static LocalDateTime toLocalDateTime(Object dateTime) throws Exception {
        // 将对象转换为LocalDateTime对象
        LocalDateTime local = toLocalDateTime(dateTime, null);
        // 判断有效性
        if (local == null) {
            throw new Exception("Can not parse the dateTime,please register the pattern for " + dateTime);
        }
        // 返回结果
        return local;
    }

    /**
     * 自动将对象转为Date，不推荐使用Date，推荐使用LocalDateTime
     * @param date 对象，如：String、int、long、Date、LocalDateTime
     * @return Date 日期对象
     * @throws Exception
     */
    public static Date toDate(Object date) throws Exception {
        return toDate(date, null);
    }


    /**
     * 日期格式化，结果样式：yyyy-MM-dd HH:mm:ss
     * @param dateTime 格式化对象
     * @return
     * @throws Exception
     */
    public static String format(Object dateTime) {
        try {
            return format(dateTime, Formatter.DEFAULT.getPattern());
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(dateTime);
        }
    }

    /**
     * 日期格式化
     * @param dateTime  格式化对象
     * @param formatter 格式化样式对象
     * @return
     * @throws Exception
     */
    public static String format(Object dateTime, DateTimes.Formatter formatter) throws Exception {
        return format(dateTime, formatter.getPattern());
    }

    /**
     * 日期格式化
     * @param dateTime 格式化对象
     * @param pattern  格式化样式
     * @return
     * @throws Exception
     */
    public static String format(Object dateTime, String pattern) throws Exception {
        // 将对象转为Date对象
        Date date = toDate(dateTime);
        try {
            // 进行格式化（自定义样式的建议使用SimpleDateFormat）
            String format = new SimpleDateFormat(pattern).format(date);
            return format;
        } catch (Exception e) {
            e.printStackTrace();
            LocalDateTime localDateTime = toLocalDateTime(dateTime);
            String format = DateTimeFormatter.ISO_DATE_TIME.format(localDateTime);
            return format;
        }
    }

    /**
     * 添加格式化样式
     * @param pattern
     */
    public static void registerPattern(String pattern) {
        if (!patterns.contains(pattern)) {
            synchronized (patterns) {
                if (!patterns.contains(pattern)) {
                    patterns.add(pattern);
                }
            }
        }
    }

    /**
     * 是否闰年
     * @param year 年份
     */
    public static boolean isLeapYear(int year) {
        if (year % 100 != 0) {
            if (year % 4 == 0) {
                return true;
            }
        } else if (year % 400 == 0) {
            return true;
        }
        return false;
    }

    // ------------------------ 私有方法 ------------------------

    /**
     * 自动将对象转为日期时间类（包括日期和时间，在DateTimePattern里找对应的样式），转换异常时返回默认值
     * @param dateTime     字符串、Date、LocalDateTime、int、long对象
     * @param defaultValue 默认值
     * @return LocalDateTime对象
     * @throws Exception
     */
    private static LocalDateTime toLocalDateTime(Object dateTime, LocalDateTime defaultValue) throws Exception {
        // 判断有效性
        if (dateTime == null) {
            return defaultValue;
        }
        // 如果是LocalDateTime对象，直接返回
        else if (dateTime instanceof LocalDateTime) {
            return (LocalDateTime) dateTime;
        }
        // 如果是Date对象，进行转换
        else if (dateTime instanceof Date) {
            return dateToLocalDateTime((Date) dateTime);
        }
        // 不是数int或long，则转成String对象
        else if (!(dateTime instanceof Long) && !(dateTime instanceof Integer)) {
            // 将字符串解析为LocalDateTime对象
            LocalDateTime localDateTime = stringToLocalDateTime(dateTime.toString());
            // 判断解析结果是否有效
            if (localDateTime == null) {
                // 将字符串解析为Date对象
                Date date = stringToDate(dateTime.toString());
                // 无效返回默认值
                if (date == null) {
                    if (defaultValue == null) {
                        throw new Exception("Can not parse the dateTime,please register the pattern for " + dateTime.toString());
                    } else {
                        return defaultValue;
                    }
                }
                // 有效则将Date转为LocalDateTime对象
                return dateToLocalDateTime(date);
            }
            return localDateTime;
        }
        // 是int或long类型，则先转为Date，再转为LocalDateTime对象
        return dateToLocalDateTime(new Date((Long) dateTime));
    }

    /**
     * 自动将对象转为Date，不推荐使用Date，推荐使用LocalDateTime
     * @param date        对象，如：String、int、long、Date、LocalDateTime
     * @param defaultDate 默认值
     * @return Date 日期对象
     * @throws Exception
     */
    private static Date toDate(Object date, Date defaultDate) throws Exception {
        LocalDateTime defaultValue = null;
        // 判断默认值是否有效
        if (defaultDate != null) {
            // 将默认值Date转为LocalDateTime对象（同时进行修正）
            defaultValue = dateToLocalDateTime(defaultDate);
        }
        // 转换：Object->LocalDateTime->Date
        return localDateTimeToDate(toLocalDateTime(date, defaultValue));
    }

    /**
     * 将字符串解析城Date对象
     * @param dateTime
     * @param index
     * @return
     */
    private static Date stringToDate(String dateTime) {
        for (String pattern : patterns) {
            Date parse = null;
            try {
                // 解析结果
                parse = new SimpleDateFormat(pattern).parse(dateTime, new ParsePosition(0));
                if (parse != null) {
                    return parse;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 将字符串解析城LocalDateTime对象
     * @param dateTime
     * @return
     */
    private static LocalDateTime stringToLocalDateTime(String dateTime) {
        for (DateTimeFormatter formatter : localDateTimeFormatters) {
            try {
                // 解析
                LocalDateTime result = LocalDateTime.from(formatter.parse(dateTime));
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
            }
        }
        // 无法解析返回空
        return null;
    }

    /**
     * 将LocalDateTime类转换成类Date，不推荐使用Date
     * @param localDateTime
     * @return
     */
    @SuppressWarnings("all")
    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        Date date = new Date();
        /*
            - LocalDateTime使用正数，如：1100=1100
            - Date使用1900做为基准，1910:1910-1900=10，1100=1100-1900=-800
         */
        date.setYear(localDateTime.getYear() - 1900);
        date.setMonth(localDateTime.getMonthValue() - 1);
        date.setDate(localDateTime.getDayOfMonth());
        date.setHours(localDateTime.getHour());
        date.setMinutes(localDateTime.getMinute());
        date.setSeconds(localDateTime.getSecond());
        // 修复毫秒值
        try {
            // 获取带毫秒的时间字符串
            String timeStr = localDateTime.toLocalTime().toString();
            if (timeStr.indexOf(".") > 0) {
                // 截取并解析毫秒数(固定截取3位,防止毫秒数超长而出错)
                int i = timeStr.indexOf(".");
                long millSeconds = Long.parseLong(timeStr.substring(i + 1, i + 4));
                // 设置毫秒数(将老数值置为000)
                date.setTime((date.getTime() / 1000) * 1000 + millSeconds);
            } else {
                // 设置毫秒数(将老数值置为000)
                date.setTime((date.getTime() / 1000) * 1000);
            }
        } catch (Exception e) {
        }
        return date;
        /**
         * 不使用这种方式，会有错误，如：
         * LocalDateTime = 1100-03-02T01:02:03.234 =>
         * Date = Fri Feb 24 00:56:20 CST 1100
         */
        // String zoneID = TimeZone.getDefault().getID();// 时区偏移
        // ZonedDateTime toLocalDataTime = ZonedDateTime.toLocalDataTime(localDateTime, ZoneId.toLocalDataTime(zoneID));
        // Instant instant = toLocalDataTime.toInstant();
        // Date from = Date.from(instant);
        // return from;
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        // 获取当前时区
        ZoneId zoneId = ZoneId.systemDefault();
        // 先转成LocalDateTime对象
        LocalDateTime local = date.toInstant().atZone(zoneId).toLocalDateTime();
        /*
         * 进行修正,不然会有时间错误。如：
         * date=1700-3-2 1:2:3.234 => localDateTime=1700-03-02T01:07:46
         * date=1100-3-2 1:2:3.234 => localDateTime=1100-03-09T01:07:46.234
         */
        if (date.getYear() != local.getYear()) {
            // Date的年是以1900为基准的，如：1910年为10，1700年为-200
            local = local.withYear(date.getYear() + 1900);
        }
        if (date.getMonth() + 1 != local.getMonthValue()) {
            local = local.withMonth(date.getMonth() + 1);
        }
        if (date.getDate() != local.getDayOfMonth()) {
            local = local.withDayOfMonth(date.getDate());
        }
        if (date.getHours() != local.getHour()) {
            local = local.withHour(date.getHours());
        }
        if (date.getMinutes() != local.getMinute()) {
            local = local.withMinute(date.getMinutes());
        }
        if (date.getSeconds() != local.getSecond()) {
            local = local.withSecond(date.getSeconds());
        }
        return local;
    }

    // ------------------------ 静态内部类 ------------------------

    static {
        // 初始化 日期时间 样式
        patterns = new LinkedHashSet<>();
        for (Formatter formatter : Formatter.values()) {
            patterns.add(formatter.getPattern());
        }

        // 初始化JDK自带的格式化对象
        localDateTimeFormatters = new LinkedHashSet<>();
        for (Field field : DateTimeFormatter.class.getDeclaredFields()) {
            DateTimeFormatter formatter = getFieldValue(field, DateTimeFormatter.class);
            if (formatter != null) {
                localDateTimeFormatters.add(formatter);
            }
        }
    }

    private static <T> T getFieldValue(Field field, Class<T> clazz) {
        if (field != null) {
            field.setAccessible(true);
            if (field.getType().equals(clazz)) {
                try {
                    return (T) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    // ------------------------ 静态内部类 ------------------------

    public static enum Formatter {

        /**
         * 默认样式：yyyy-MM-dd HH:mm:ss
         */
        DEFAULT("yyyy-MM-dd HH:mm:ss"),

        /**
         * yyyy-MM-dd HH:mm:ss
         */
        DATE_TIME("yyyy-MM-dd HH:mm:ss"),
        /**
         * yyyy-MM-dd HH:mm:ss +08:00
         */
        DATE_TIME_ZONE("yyyy-MM-dd HH:mm:ss XXX"),

        /**
         * yyyy-MM-dd HH:mm:ss.SSS
         */
        DATE_TIME_MILL("yyyy-MM-dd HH:mm:ss.SSS"),

        /**
         * yyyy-MM-dd HH:mm:ss.SSS +08:00
         */
        DATE_TIME_MILL_ZONE("yyyy-MM-dd HH:mm:ss.SSS XXX"),
        /**
         * yyyy-MM-dd HH:mm
         */
        DATE_TIME_SIMPLE("yyyy-MM-dd HH:mm"),

        /**
         * yyyy-MM-dd HH:mm +08:00
         */
        DATE_TIME_SIMPLE_ZONE("yyyy-MM-dd HH:mm XXX"),

        /**
         * yyyy-MM-ddTHH:mm:ss.SS
         */
        ISO("yyyy-MM-dd'T'HH:mm:ss.SSS"),

        /**
         * yyyy-MM-ddTHH:mm:ss.SS
         */
        ISO_ZONE("yyyy-MM-dd'T'HH:mm:ss.SSS XXX"),

        /**
         * Sat Sep 19 23:47:49 CST 2020 星期六 九月 19 23:46:51 CST 2020
         */
        DATE_STRING("EEE MMM dd HH:mm:ss z yyyy"),

        /**
         * yyyyMMddHHmmssSSS
         */
        DATE_TIME_NO_MARK("yyyyMMddHHmmssSSS"),

        /**
         * yyyyMMddHHmmssSSS +08:00
         */
        DATE_TIME_NO_MARK_ZONE("yyyyMMddHHmmssSSS XXX"),

        /**
         * yyyyMMddHHmmss
         */
        DATE_TIME_NO_MARK_MILL_SECONDS("yyyyMMddHHmmss"),

        /**
         * yyyyMMddHHmmss +08:00
         */
        DATE_TIME_NO_MARK_MILL_SECONDS_ZONE("yyyyMMddHHmmss XXX"),

        /**
         * yyyy/MM/dd HH:mm:ss.SSS
         */
        DATE_TIME_SLASH("yyyy/MM/dd HH:mm:ss.SSS"),

        /**
         * yyyy/MM/dd HH:mm:ss.SSS +08:00
         */
        DATE_TIME_SLASH_ZONE("yyyy/MM/dd HH:mm:ss.SSS XXX"),

        /**
         * yyyy/MM/dd HH:mm:ss
         */
        DATE_TIME_SLASH_NO_MILL_SECONDS("yyyy/MM/dd HH:mm:ss"),

        /**
         * yyyy/MM/dd HH:mm:ss +08:00
         */
        DATE_TIME_SLASH_NO_MILL_SECONDS_ZONE("yyyy/MM/dd HH:mm:ss XXX"),

        /**
         * yyyy/MM/dd HH:mm
         */
        DATE_TIME_SLASH_SIMPLE("yyyy/MM/dd HH:mm"),

        /**
         * yyyy/MM/dd HH:mm +08:00
         */
        DATE_TIME_SLASH_SIMPLE_ZONE("yyyy/MM/dd HH:mm XXX"),

        /**
         * yyyy年MM月dd日 HH时mm分ss秒SSS毫秒
         */
        DATE_TIME_CHINESE("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒"),

        /**
         * yyyy年MM月dd日 HH时mm分ss秒
         */
        DATE_TIME_CHINESE_NO_MILL_SECONDS("yyyy年MM月dd日 HH时mm分ss秒"),

        /**
         * yyyy年MM月dd日 HH时mm分
         */
        DATE_TIME_CHINESE_SIMPLE("yyyy年MM月dd日 HH时mm分"),

        /**
         * yyyy年MM月dd日HH时mm分ss秒SSS毫秒
         */
        DATE_TIME_CHINESE_NO_SPACE("yyyy年MM月dd日HH时mm分ss秒SSS毫秒"),

        /**
         * yyyy年MM月dd日HH时mm分ss秒
         */
        DATE_TIME_CHINESE_NO_SPACE_MILL_SECONDS("yyyy年MM月dd日HH时mm分ss秒"),

        /**
         * yyyy年MM月dd日HH时mm分
         */
        DATE_TIME_CHINESE_NO_SPACE_MILL_SECONDS_SIMPLE("yyyy年MM月dd日HH时mm分"),

        /**
         * yyyy.MM.dd 公元 'at' HH:mm:ss CST(时区)
         */
        DATE_TIME_AT("yyyy.MM.dd G 'at' HH:mm:ss z"),

        /**
         * 02001.July.04 AD 12:08 PM
         */
        DATE_TIME_AM_PM("yyyyy.MMMMM.dd GGG hh:mm aaa"),

        /**
         * Wed, 4 Jul 2001 12:08:56 -0700 星期六, 19 九月 2020 23:46:51 +08:00
         */
        DATE_TIME_WEEKEND("EEE, d MMM yyyy HH:mm:ss XXX");

        // A("yyyyMMdd HH:mm:ss"),
        // B("yyyyMMdd HH:mm:ss.SSS");

        private final String pattern;

        private Formatter(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    private static enum DateFormatter {
        /**
         * yyyy-MM-dd
         */
        DEFAULT("yyyy-MM-dd"),

        /**
         * yyyy/MM/dd
         */
        DATE_SLASH("yyyy/MM/dd"),

        /**
         * yyyy年MM月dd日
         */
        DATE_CHINESE("yyyy年MM月dd日"),

        /**
         * yyyyMMdd
         */
        DATE_NO_MARK("yyyyMMdd"),

        /**
         * Wed, Jul 4, '01 星期六, 九月 19, '20
         */
        WEEK("EEE, MMM d, ''yy");

        private final String pattern;

        private DateFormatter(String pattern) {
            this.pattern = pattern;
        }
    }

    private static enum TimeFormatter {
        /**
         * HH:mm:ss.SSS
         */
        DEFAULT("HH:mm:ss.SSS"),

        /**
         * HH:mm:ss
         */
        TIME_NO_MILL_SECONDS("HH:mm:ss"),

        // /**
        //  * HHmmssSSS(不使用这个样式，格式化会和年月日混淆)
        //  */
        // TIME_NO_MARK ("HHmmssSSS"),

        /**
         * HHmmss
         */
        TIME_NO_MARK_MILL_SECONDS("HHmmss"),

        /**
         * HH时mm分ss秒SSS毫秒
         */
        TIME_CHINESE("HH时mm分ss秒SSS毫秒"),

        /**
         * HH时mm分ss秒
         */
        TIME_CHINESE_NO_MILL_SECONDS("HH时mm分ss秒"),

        /**
         * 12:08 PM 12:08 下午
         */
        A("h:mm a"),

        /**
         * 12 o'clock PM, Pacific Daylight Time 12 o'clock 下午, 中国标准时间
         */
        CLOCK("hh 'o''clock' a, zzzz"),

        /**
         * 0:08 PM, PDT
         */
        ZONE("K:mm a, z");


        private final String pattern;

        private TimeFormatter(String pattern) {
            this.pattern = pattern;
        }
    }


}
