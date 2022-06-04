package com.x.doraemon.reflect.method;

import com.x.doraemon.Strings;
import com.x.doraemon.reflect.DoraemonFunction;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author AD
 * @date 2022/3/29 13:35
 */
public class Methods {

    private Methods() {}

    public static <T, R> String getFieldName(DoraemonFunction<T, R> dr) {
        try {
            Method m = dr.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda o = (SerializedLambda) m.invoke(dr);
            String name = o.getImplMethodName();
            if (name.startsWith("get")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            }
            return Strings.firstToLower(name);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        String name = getFieldName(User::getAge);
        System.out.println(name);
    }

    public class User {

        private String name;
        private int age;

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
    }
}
