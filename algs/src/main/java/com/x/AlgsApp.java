package com.x;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *
 * @author AD
 * @date 2021/11/24 14:38
 */
@SpringBootApplication
public class AlgsApp {

    public static void main(String[] args) {
        SpringApplication.run(AlgsApp.class, args);

        ThreadPoolExecutor es = (ThreadPoolExecutor)Executors.newFixedThreadPool(2);

    }
}
