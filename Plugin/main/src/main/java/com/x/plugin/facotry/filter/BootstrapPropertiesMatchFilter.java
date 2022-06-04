package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/5/5 10:43
 */
public class BootstrapPropertiesMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"^bootstrap.properties$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }
}
