package com.x.plugin.facotry;

import com.x.plugin.core.IBeanNameModifier;

/**
 * @author AD
 * @date 2022/5/14 16:06
 */
public class BeanNameModifier implements IBeanNameModifier {

    @Override
    public String modify(String envId, String srcName) {
        return envId.concat(FLAG).concat(srcName);
    }
}
