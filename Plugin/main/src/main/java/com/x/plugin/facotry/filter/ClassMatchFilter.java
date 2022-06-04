package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/4/26 17:00
 */
public class ClassMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"/*.class$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }

}
