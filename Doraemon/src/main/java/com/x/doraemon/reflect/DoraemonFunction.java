package com.x.doraemon.reflect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author AD
 * @date 2022/3/29 13:32
 */
@FunctionalInterface
public interface DoraemonFunction<T,R> extends Function<T,R> , Serializable {

}
