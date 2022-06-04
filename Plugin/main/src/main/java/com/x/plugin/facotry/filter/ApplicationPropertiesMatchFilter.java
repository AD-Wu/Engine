package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/4/26 16:58
 */
public class ApplicationPropertiesMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"^application[-]?[a-z]*.properties$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }

}
