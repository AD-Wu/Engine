package com.x.plugin.core.filter;

/**
 * @author AD
 * @date 2022/5/17 12:38
 */
public class EndJarFilter implements IFilter<String>{

    @Override
    public boolean accept(String s) {
        return s.endsWith(".jar");
    }
}
