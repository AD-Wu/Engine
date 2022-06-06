package com.x.jdk8.reflect;

/**
 * TODO
 *
 * @author AD
 * @date 2022/6/6 13:20
 */
public class ClassDemo {
    
    public static void main(String[] args) {
        classCompare();
    }
    
    private static void classCompare() {
        ClassDemo cd = new ClassDemo();
        System.out.println(cd.getClass() == ClassDemo.class);
        System.out.println(cd.getClass().hashCode());
        System.out.println(ClassDemo.class.hashCode());
    }
    
    private static void typeShow() {
        print(byte.class);
        print(Byte.class);
        print(byte[].class);
        print(Byte[].class);
        
        print(short.class);
        print(short[].class);
        print(Short.class);
        print(Short[].class);
        
        print(int.class);
        print(int[].class);
        print(Integer.class);
        print(Integer[].class);
        
        print(long.class);
        print(long[].class);
        print(Long.class);
        print(Long[].class);
        
        print(float.class);
        print(float[].class);
        print(Float.class);
        print(Float[].class);
        
        print(double.class);
        print(double[].class);
        print(Double.class);
        print(Double[].class);
        
        print(char.class);
        print(char[].class);
        print(Character.class);
        print(Character[].class);
        
        print(boolean.class);
        print(boolean[].class);
        print(Boolean.class);
        print(Boolean[].class);
    }
    
    private static void print(Class<?> clazz) {
        System.out.println(clazz.getName());
    }
    
}
