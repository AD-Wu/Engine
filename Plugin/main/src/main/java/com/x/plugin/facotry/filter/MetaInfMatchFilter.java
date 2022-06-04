package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/4/26 16:59
 */
public class MetaInfMatchFilter extends RegexMatchFilter {
    public static final String[] patterns = {"^META-INF"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }

}
