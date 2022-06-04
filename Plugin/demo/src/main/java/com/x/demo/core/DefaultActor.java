package com.x.demo.core;

import com.x.plugin.anno.PluginParam;
import com.x.plugin.anno.RunPlugin;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/5/28 17:41
 */
@Component
@Primary
public class DefaultActor implements IActor {


    @RunPlugin(sceneNo = "sceneNo", mgtOrgCode = "mgtOrgCode")
    @Override
    public String getOS(@PluginParam Map<String, Object> param) {
        return "Default:" + LocalTime.now();
    }

}
