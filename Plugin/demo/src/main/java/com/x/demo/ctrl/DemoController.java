package com.x.demo.ctrl;

import com.x.demo.core.IActor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示接口
 * @author AD
 * @date 2022/4/22 17:47
 */
@RestController
@RequestMapping("/hello")
public class DemoController {

    @Autowired
    private IActor actor;

    /**
     * Aop
     * @param sceneNo    场景编号|linux,mac,window
     * @param mgtOrgCode 管理单位编码|linux,mac,window
     * @return
     */
    @GetMapping("auto")
    public String auto(@RequestParam String sceneNo, @RequestParam String mgtOrgCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("sceneNo", sceneNo);
        param.put("mgtOrgCode", mgtOrgCode);
        return actor.getOS(param);
    }


}
