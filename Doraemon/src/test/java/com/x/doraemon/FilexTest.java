package com.x.doraemon;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author AD
 * @date 2022/5/9 14:03
 */
@DisplayName("Filex测试")
public class FilexTest {

    private static Path source;
    private static Path target;

    @BeforeAll
    public static void before() throws Exception {
        Path desktop = Systemx.getUserHome();
        // target = desktop.resolve("test/dir");
        // target = desktop;
        target = Paths.get(desktop.toAbsolutePath().toString(),"copy");
        URL srcUrl = Filex.class.getClassLoader().getResource("");
        source = Paths.get(srcUrl.toURI());
        System.out.println("target:\t" + target);
        System.out.println("source:\t" + source);
        System.out.println("-----------------------------");
    }

    @Test
    public void copyFileToFile() throws IOException, URISyntaxException {
        // 拷贝:文件->文件(会被当成文件夹)
        URL confUrl = Filex.class.getClassLoader().getResource("conf.yml");
        Files.createDirectories(target);
        Path targetFile = Paths.get(target.toAbsolutePath().toString(), "conf-copy.yml");
        Files.createFile(targetFile);
        Filex.copy(Paths.get(confUrl.toURI()), targetFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void copyDirToDir() throws IOException, URISyntaxException {
        // 拷贝:文件夹->文件夹
        URL copyUrl = Filex.class.getClassLoader().getResource("copy");
        Filex.copy(Paths.get(copyUrl.toURI()), target);
    }

    @Test
    void copyFileToDir() throws IOException, URISyntaxException {
        // 拷贝:文件->文件夹
        URL confUrl = Filex.class.getClassLoader().getResource("conf.yml");
        Filex.copy(Paths.get(confUrl.toURI()), target);
    }

    @Test
    void walkTree() throws IOException {
        // 需要覆盖postVisitDirectory方法和visitFileFailed方法，否则，访问会在遇到不允许打开的目录或不允许访问的文件时立即失败。
        Path path = Files.walkFileTree(target, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("pre:\t" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visit:\t" + file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("post:\t" + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("failed:\t" + file);
                return FileVisitResult.SKIP_SUBTREE;
            }
        });
    }
}
