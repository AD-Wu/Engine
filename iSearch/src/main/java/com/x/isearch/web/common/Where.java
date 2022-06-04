package com.x.isearch.web.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @author AD
 * @date 2022/1/19 19:29
 */
public class Where<T> extends LambdaQueryWrapper<T> {

    public static <T> Where<T> get(Class<T> clazz) {
        return new Where<>();
    }
}
