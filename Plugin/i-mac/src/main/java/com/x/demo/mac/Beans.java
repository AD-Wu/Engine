package com.x.demo.mac;

import com.x.demo.core.IActor;
import com.x.plugin.anno.NoModify;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author AD
 * @date 2022/5/15 20:05
 */
@Configuration
public class Beans {

    @NoModify
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("linux")
    public IActor linuxActor() {
        return new IActor() {

            @Override
            public String getOS(Map<String, Object> param) {
                return "Mac-linux : " + LocalTime.now();
            }

        };
    }

    @Bean("window")
    public IActor windowActor() {
        return new IActor() {
            @Override
            public String getOS(Map<String, Object> param) {
                return "Mac-window : " + LocalTime.now();
            }
        };
    }
}
