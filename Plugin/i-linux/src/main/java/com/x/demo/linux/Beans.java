package com.x.demo.linux;

import com.x.demo.core.IActor;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author AD
 * @date 2022/5/15 20:05
 */
@Configuration
public class Beans {

    @Bean("mac")
    public IActor macActor() {
        return new IActor() {
            @Override
            public String getOS(Map<String, Object> param) {
                return "Linux-mac : " + LocalTime.now();
            }
        };
    }

    @Bean("window")
    public IActor windowActor() {
        return new IActor() {
            @Override
            public String getOS(Map<String, Object> param) {
                return "Linux-window : " + LocalTime.now();
            }
        };
    }
}
