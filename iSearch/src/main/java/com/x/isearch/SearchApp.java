package com.x.isearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author AD
 * @date 2022/1/12 0:00
 */
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class SearchApp {

    public static void main(String[] args) {
        try {
            SpringApplication.run(SearchApp.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
