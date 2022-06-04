package com.x.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author AD
 * @date 2022/1/8 17:21
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosApp {

    public static void main(String[] args) {
        try {
            SpringApplication.run(NacosApp.class,args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
