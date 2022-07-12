package com.x.bridge.conf;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import java.util.Map;
import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author AD
 * @date 2022/7/12 10:13
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 动态表名
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor innerInterceptor = new DynamicTableNameInnerInterceptor();
        innerInterceptor.setTableNameHandler((sql, tableName) -> {
            // 获取参数方法
            Map<String, Object> paramMap = RequestDataHelper.getRequestData();
            paramMap.forEach((k, v) -> System.err.println(k + "----" + v));

            String year = "_2018";
            int random = new Random().nextInt(10);
            if (random % 2 == 1) {
                year = "_2019";
            }
            return tableName + year;
        });
        interceptor.addInnerInterceptor(innerInterceptor);
        // 3.4.3.2 作废该方式
        // dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
        return interceptor;
    }

    /**
     * 请求参数传递辅助类
     */
    public static class RequestDataHelper {

        /**
         * 请求参数存取
         */
        private static final ThreadLocal<Map<String, Object>> REQUEST_DATA = new ThreadLocal<>();

        /**
         * 设置请求参数
         * @param requestData 请求参数 MAP 对象
         */
        public static void setRequestData(Map<String, Object> requestData) {
            REQUEST_DATA.set(requestData);
        }

        /**
         * 获取请求参数
         * @param param 请求参数
         * @return 请求参数 MAP 对象
         */
        public static <T> T getRequestData(String param) {
            Map<String, Object> dataMap = getRequestData();
            if (CollectionUtils.isNotEmpty(dataMap)) {
                return (T) dataMap.get(param);
            }
            return null;
        }

        /**
         * 获取请求参数
         * @return 请求参数 MAP 对象
         */
        public static Map<String, Object> getRequestData() {
            return REQUEST_DATA.get();
        }
    }
}
