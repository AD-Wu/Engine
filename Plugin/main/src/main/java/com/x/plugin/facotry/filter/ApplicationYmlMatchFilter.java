package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;

/**
 * @author AD
 * @date 2022/4/26 16:57
 */
public class ApplicationYmlMatchFilter extends RegexMatchFilter {

    public static final String[] patterns = {"^application[-]?[a-z]*.y[a]?ml$"};

    @Override
    protected String[] getPatterns() {
        return patterns;
    }
}
