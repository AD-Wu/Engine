package com.x.plugin.core;

/**
 * @author AD
 * @date 2022/5/14 16:05
 */
public interface IBeanNameModifier {

    String FLAG = ":";

    String modify(String envId, String srcName);
}
