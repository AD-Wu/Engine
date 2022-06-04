package com.x.nacos.bean;

import java.util.StringJoiner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/1/9 22:53
 */
@Component
@ConfigurationProperties(prefix = "author")
public class User {

    /**
     * 名字
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]").add("name='" + name + "'").toString();
    }
}
