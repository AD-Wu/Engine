package com.x.plugin.core.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AD
 * @date 2022/5/17 19:28
 */
public abstract class RegexFindFilter implements IFilter<String> {

    @Override
    public boolean accept(String name) {
        Pattern p = Pattern.compile(getPattern());
        Matcher m = p.matcher(name.toString());
        // 禁止使用find()
        if (m.find()) {
            return true;
        }
        return false;
    }

    protected abstract String getPattern();
}
