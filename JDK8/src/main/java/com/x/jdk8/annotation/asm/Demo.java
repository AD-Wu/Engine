package com.x.jdk8.annotation.asm;

import com.x.jdk8.annotation.processor.ToString;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author AD
 * @date 2022/6/4 11:23
 */
@ToString
public class Demo implements IDemo<String> {

    private String name;
    int age;
    protected int score;
    public LocalDateTime birthday;

    private Demo(){}

    public Demo(String name){
        this.name = name;
    }

    @RunTime
    @Override
    public String now() {
        return LocalTime.now().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    private static class User {

        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
