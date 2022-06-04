package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/5/4 11:03
 */
public class ApplicationYmlMainMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"^application.y[a]?ml$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }
}
