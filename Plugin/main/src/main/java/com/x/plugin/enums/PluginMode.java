package com.x.plugin.enums;

/**
 * @author AD
 * @date 2022/5/17 22:40
 */
public enum PluginMode {

    src("src"),
    abs("abs");

    private final String type;

    private PluginMode(String type) {this.type = type;}

    public String getType() {
        return type;
    }
}
