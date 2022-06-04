package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/4/26 16:58
 */
public class ApplicationPropertiesMainMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"^application.properties$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }
}
