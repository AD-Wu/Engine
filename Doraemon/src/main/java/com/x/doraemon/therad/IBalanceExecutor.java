package com.x.doraemon.therad;

import java.util.concurrent.ExecutorService;

/**
 * @author AD
 * @date 2022/6/22 12:16
 */
public interface IBalanceExecutor<KEY> extends ExecutorService {

    void execute(KEY key, Runnable command);

}
