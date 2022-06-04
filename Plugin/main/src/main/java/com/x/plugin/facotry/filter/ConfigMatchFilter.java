package com.x.plugin.facotry.filter;

import com.x.plugin.core.filter.RegexMatchFilter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author AD
 * @date 2022/4/26 17:05
 */
public class ConfigMatchFilter extends RegexMatchFilter {


    public static final String[] patterns = Stream
        .concat(Arrays.stream(ApplicationYmlMatchFilter.patterns), Arrays.stream(ApplicationPropertiesMatchFilter.patterns)).collect(Collectors.toList())
        .toArray(new String[0]);

    @Override
    protected String[] getPatterns() {
        return patterns;
    }

}
