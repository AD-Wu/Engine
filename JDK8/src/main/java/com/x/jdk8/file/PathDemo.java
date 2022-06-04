package com.x.jdk8.file;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author AD
 * @date 2022/4/30 16:51
 */
public class PathDemo {

    public static void main(String[] args) {
        relative();
    }

    /**
     * 找出相对路径
     */
    private static void relative() {
        Path path = Paths.get("a", "b");
        Path relativize = path.relativize(Paths.get("a", "b", "c"));
       /*
            D:\GitCode\Engine\a\b
            D:\GitCode\Engine\c
        */
        System.out.println(path.toAbsolutePath());
        System.out.println(relativize.toAbsolutePath());
    }

    private static void sibling() {
        Path path = Paths.get("a");
        // D:\GitCode\Engine\a
        System.out.println(path.toFile().getAbsoluteFile());

        // 创建path的同级目录(即兄弟目录)
        Path siblingPath = path.resolveSibling("b");
        // D:\GitCode\Engine\b
        System.out.println(siblingPath.toFile().getAbsoluteFile());

        // 当前缀带根路径时,则不返回兄弟路径,而是返回根路径下的c目录
        Path sibPath = path.resolveSibling("/c");
        // D:\c
        System.out.println(sibPath.toFile().getAbsoluteFile());

    }

    private static void resolve() {
        Path path = Paths.get("a");
        // 如果resolve里的参数是相对路径,则返回a下面的的子路径b.否则返回绝对路径
        Path b = path.resolve("b");
        // D:\GitCode\Engine\a\b
        System.out.println(b.toFile().getAbsoluteFile());

        // D:\c
        Path c = path.resolve("/c");
        System.out.println(c.toFile().getAbsoluteFile());
    }

    private static void get() {
        // first路径,不带/作为前缀时,表示相对路径,反之则是绝对路径
        Path relativePath = Paths.get("a", "b", "c");
        String absFilePath = relativePath.toFile().getAbsolutePath();
        // D:\GitCode\Engine\a\b\c
        System.out.println(absFilePath);

        Path rootPath = Paths.get("/a", "b", "c");
        String rootFilePath = rootPath.toFile().getAbsolutePath();
        // D:\a\b\c
        System.out.println(rootFilePath);
    }


}
