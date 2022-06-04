package com.x.plugin.data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author AD
 * @date 2022/5/7 10:50
 */
public class PluginConfig {


    private Set<String> commonDirs;
    private Set<String> businessDirs;

    public PluginConfig() {
        this.commonDirs = new LinkedHashSet<>();
        this.businessDirs = new LinkedHashSet<>();
    }

    public Set<String> getCommonDirs() {
        return commonDirs;
    }

    public void setCommonDirs(Set<String> commonDirs) {
        this.commonDirs = commonDirs;
    }

    public Set<String> getBusinessDirs() {
        return businessDirs;
    }

    public void setBusinessDirs(Set<String> businessDirs) {
        this.businessDirs = businessDirs;
    }


    public static class Key{

        private static final String root = "plugin";
        public static final String mode = root + ".mode";
        public static final String commons = root + ".commons";
        public static final String business = root + ".business";

        public static String getIndexKey(String key, int index) {
            return key + "[" + index + "]";
        }

        public static String getCommonsKey(int index) {
            return getIndexKey(commons, index);
        }

        public static String getBusinessKey(int index) {
            return getIndexKey(business, index);
        }
    }
}
