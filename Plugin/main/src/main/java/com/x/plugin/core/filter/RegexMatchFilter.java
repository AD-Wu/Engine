package com.x.plugin.core.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AD
 * @date 2022/4/26 16:54
 */
public abstract class RegexMatchFilter implements IFilter<String> {

    @Override
    public boolean accept(String name) {
        for (String pattern : getPatterns()) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(name.toString());
            // 禁止使用find()
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    protected abstract String[] getPatterns();
}
