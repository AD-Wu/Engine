package com.x.plugin.ctrl;

import com.x.plugin.core.IPluginManager;
import com.x.plugin.data.PluginData;
import com.x.plugin.spring.PluginManagerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件接口
 * @author AD
 * @date 2022/4/26 22:56
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    /**
     * 获取插件信息
     * @param envId 环境id|linux,mac,window
     * @return
     */
    @GetMapping("/{envId}")
    @ResponseBody
    public PluginData[] getPlugins(@PathVariable(required = true) String envId) {
        IPluginManager manager = PluginManagerFactory.getPluginManager(envId);
        PluginData[] plugins = manager.getPluginDatas();
        return plugins;
    }

    /**
     * 获取所有插件信息
     * @return
     */
    @GetMapping()
    @ResponseBody
    public Map<String, PluginData[]> getAllPlugins() {
        Set<String> envIds = PluginManagerFactory.getEnvIds();
        Map<String, PluginData[]> datas = new LinkedHashMap<>();
        for (String envId : envIds) {
            IPluginManager manager = PluginManagerFactory.getPluginManager(envId);
            PluginData[] plugins = manager.getPluginDatas();
            datas.put(envId, plugins);
        }
        return datas;
    }

}
