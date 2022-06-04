package com.x.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author AD
 * @date 2022/5/8 11:08
 */
@SpringBootApplication(scanBasePackages = {"com.x.redis", "com.x.plugin", "com.x.loader"})
public class RedisApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisApp.class, args);
    }
}
