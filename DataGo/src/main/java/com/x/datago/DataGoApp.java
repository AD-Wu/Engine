package com.x.datago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author AD
 * @date 2022/1/8 17:21
 */
@SpringBootApplication
public class DataGoApp {

    public static void main(String[] args) {
        try {
            SpringApplication.run(DataGoApp.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
