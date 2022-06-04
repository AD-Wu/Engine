package com.x.redis;

import java.util.Set;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

/**
 * @author AD
 * @date 2022/4/20 10:45
 */
public class Gedis {


    public static void main(String[] args) {
        flush();
        put();
    }

    private static void put() {
        DefaultJedisClientConfig conf = DefaultJedisClientConfig.builder().database(1).build();
        Jedis redis = new Jedis("127.0.0.1", 6379, conf);
        redis.set("a", "1");
        redis.set("b", "2");
        redis.set("c", "3");
        redis.set("d", "4");
    }

    private static void flush() {
        DefaultJedisClientConfig conf = DefaultJedisClientConfig.builder().database(1).build();
        Jedis redis = new Jedis("127.0.0.1", 6379, conf);
        int db = redis.getDB();
        System.out.printf("即将清空数据库【%s】数据\n", db);
        Set<String> keys = redis.keys("[a-zA-Z]");
        for (String key : keys) {
            String v = redis.get(key);
            System.out.println("key=" + key + ",value=" + v);
        }
        String result = redis.flushDB();
        System.out.printf("清空结果【%s】", result);
    }
}
