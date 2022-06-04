package com.x.start;

import java.net.URL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author AD
 * @date 2022/4/26 11:43
 */
// @EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.x.start", "com.x.plugin", "com.x.demo"},
                       exclude = {},
                       excludeName = {"com.x.demo.window.Beans"})
public class StartApp {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext appCtx = SpringApplication.run(StartApp.class, args);
        // 来自springboot的jar包【jar:file:/D:/GitCode/Engine/Plugin/target/Plugin-1.0.0.jar!/BOOT-INF/classes!/】
        URL classURL = StartApp.class.getProtectionDomain().getCodeSource().getLocation();
    }
}
