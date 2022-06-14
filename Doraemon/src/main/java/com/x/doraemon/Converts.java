package com.x.doraemon;

import com.google.common.base.CaseFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author AD
 * @date 2022/1/13 12:32
 */
public final class Converts {

    /**
     * 16进制字符串(大写)
     */
    public static final String HEX = "0123456789ABCDEF";

    private Converts() {}

    public static boolean toBoolean(Object o) {
        return toBoolean(o, null);
    }

    public static boolean toBoolean(Object o, String... trueValues) {
        if (o == null) {
            return false;
        }
        String s = o.toString().trim();
        if (Strings.equalsAnyIgnoreCase(s, trueValues)) {
            return true;
        }
        if (Strings.equalsAnyIgnoreCase(s, "", "0", "N", "No", "Null", "false")) {
            return false;
        }
        return true;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, N = bytes.length; i < N; i++) {
                byte b = bytes[i];
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() < 2) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString().toUpperCase();
        }
        return null;
    }

    public static byte[] hexToBytes(String hex) {
        if (Strings.isNull(hex)) {
            return Arrayx.EMPTY_BYTE_ARRAY;
        }
        hex = hex.length() == 1 ? "0" + hex : hex;
        char[] cs = hex.toUpperCase().toCharArray();
        byte[] bs = new byte[cs.length / 2];
        for (int b = 0, c = 0; b < bs.length; b++, c += 2) {
            bs[b] = (byte) (HEX.indexOf(cs[c]) << 4 | HEX.indexOf(cs[c + 1]));
        }
        return bs;
    }

    public static byte[] serializableToBytes(Serializable serializable) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(serializable);
            oos.flush();
            return bos.toByteArray();
        }
    }

    /**
     * "_", "-" -> 驼峰
     * @param str
     * @return
     */
    public static String toCamel(String str) {
        String replace = str.replace("_", "-");
        return hyphenToCamel(replace);
    }

    /**
     * 把驼峰,下划线 -> 小写连字符
     * @param str
     * @return
     */
    public static String toLowerHyphen(String str) {
        String replace = str.replace("_", "-");
        return camelToLowerHyphen(replace);
    }

    /**
     * 把驼峰,连字符 -> 小写下划线
     * @param str
     * @return
     */
    public static String toLowerUnderscore(String str) {
        String replace = str.replace("-", "_");
        return camelToLowerUnderscore(replace);
    }

    // ---------------------------- 私有方法 ----------------------------

    /**
     * 驼峰 -> 小写下划线.如:testData|TestData -> test_data
     * @param str 需要转换的字符
     * @return
     */
    private static String camelToLowerUnderscore(String str) {
        return caseFormat(CaseFormat.LOWER_CAMEL, str, CaseFormat.LOWER_UNDERSCORE);
    }

    /**
     * 驼峰 -> 小写连字符.如:testData|TestData -> test-data
     * @param str 需要转换的字符
     * @return
     */
    private static String camelToLowerHyphen(String str) {
        return caseFormat(CaseFormat.LOWER_CAMEL, str, CaseFormat.LOWER_HYPHEN);
    }

    /**
     * 连字符 -> 驼峰.如:test-data -> testData
     * @param str
     * @return
     */
    private static String hyphenToCamel(String str) {
        return caseFormat(CaseFormat.LOWER_HYPHEN, str, CaseFormat.LOWER_CAMEL);
    }

    /**
     * 下滑线 -> 驼峰.如:test_data -> testData
     * @param str
     * @return
     */
    private static String underscoreToCamel(String str) {
        return caseFormat(CaseFormat.LOWER_UNDERSCORE, str, CaseFormat.LOWER_CAMEL);
    }

    /**
     * 命名格式化,如:下划线、连字符命名 <-> 驼峰命名
     * @param src    源格式,即str的格式
     * @param str    需转换的字符串
     * @param target 目标格式
     * @return
     */
    private static String caseFormat(CaseFormat src, String str, CaseFormat target) {
        return src.to(target, str);
    }


}
