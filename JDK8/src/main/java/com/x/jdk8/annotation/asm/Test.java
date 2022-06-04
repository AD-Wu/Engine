package com.x.jdk8.annotation.asm;

import java.io.FileInputStream;
import java.net.URL;
import org.objectweb.asm.ClassReader;

/**
 * @author AD
 * @date 2022/6/4 11:38
 */
public class Test {

    public static void main(String[] args) {
        URL classesUrl = Demo.class.getProtectionDomain().getCodeSource().getLocation();
        String className = Demo.class.getName().replace(".", "/");
        String path = classesUrl.getFile() + className + ".class";
        System.out.println(path);
        try (FileInputStream in = new FileInputStream(path)) {
            ClassReader reader = new ClassReader(in);
            reader.accept(new ClassInfoGetter(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
