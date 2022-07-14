package com.x.bridge;

import com.x.doraemon.therad.BalanceExecutor;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;

/**
 * 代理管理者
 * @author AD
 * @date 2022/6/21 12:07
 */
public class ProxyManager {

    public static void main(String[] args) {
        BalanceExecutor<String> executor = new BalanceExecutor<>(2);
        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("---------- 1 -----------");
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("---------- 2 -----------");
            }
        });
    }
}
