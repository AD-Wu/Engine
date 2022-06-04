package com.x.doraemon;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AD
 * @date 2022/4/10 17:20
 */
public final class Printer {

    private final Map<Integer, Integer> columnMaxLength;
    private final List<List<String>> rows;

    public Printer() {
        this.columnMaxLength = new HashMap<>();
        this.rows = new ArrayList<>();
    }

    public synchronized void add(Object... os) {
        List<String> row = new ArrayList<>();
        for (int i = 0; i < os.length; i++) {
            Object o = os[i];
            String s = o == null ? "null" : o.toString();
            int L = s.length();
            Integer len = columnMaxLength.get(i);
            if (len != null) {
                L = Math.max(L, len);
            }
            columnMaxLength.put(i, L);
            row.add(s);
        }
        rows.add(row);
    }

    public synchronized void print() {
        String format = getFormat();
        for (int i = 0, N = rows.size(); i < N; i++) {
            System.out.printf(format, rows.get(i).toArray(new String[0]));
        }
        rows.clear();
    }

    public synchronized String getContent() {
        String pattern = getFormat();
        StringBuilder content = new StringBuilder();
        for (int i = 0, N = rows.size(); i < N; i++) {
            // Formatter不能重用
            try (Formatter formatter = new Formatter()) {
                String row = formatter.format(pattern, rows.get(i).toArray(new String[0])).toString();
                content.append(row);
            }
        }
        return content.toString();
    }

    private synchronized String getFormat() {
        int columnCount = columnMaxLength.size();
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < columnCount; i++) {
            int maxLength = columnMaxLength.get(i);
            // 只有一列
            if (columnCount == 1) {
                format.append("%-").append(maxLength).append("s%n");
            } else {
                // // 最后一列(如果长度超过100,则左对齐)
                // if (i == columnCount - 1 && maxLength < 1000) {
                //     format.append("%").append(maxLength).append("s%n");
                // } else {
                //     if (maxLength > 1000) {
                //         format.append("%-").append(maxLength).append("s%n");
                //     } else {
                //         format.append("%-").append(maxLength).append("s     ");
                //     }
                // }

                // 最后一行(非第一行)
                if (i != 0 && i == columnCount - 1) {
                    format.append("%").append(maxLength).append("s%n");
                } else {
                    format.append("%-").append(maxLength + 4).append("s");
                }

            }
        }
        return format.toString();
    }
}
