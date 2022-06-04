package com.x.jdk8.file;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.filechooser.FileSystemView;

/**
 * @author AD
 * @date 2022/5/8 15:26
 */
public class FilesDemo {

    private static final String filename = "io-test.txt";

    public static void main(String[] args) throws Exception {
        walk();
    }

    private static void walk() throws URISyntaxException, IOException {
        // Files.walk()可以遍历出所有的层级
        try (Stream<Path> paths = Files.walk(getResourceFile("a"))) {
           /*
                D:\GitCode\Engine\JDK8\target\classes\a
                D:\GitCode\Engine\JDK8\target\classes\a\b
                D:\GitCode\Engine\JDK8\target\classes\a\b\MSC验证.txt
                D:\GitCode\Engine\JDK8\target\classes\a\io-test.txt
                D:\GitCode\Engine\JDK8\target\classes\a\加班.txt
            */
            paths.forEach(System.out::println);
        }

    }

    private static void list() throws URISyntaxException, IOException {
        // Files.list()只能遍历出a的一层子目录,多层遍历使用walk
        try (Stream<Path> paths = Files.list(getResourceFile("a"))) {
            /*
                D:\GitCode\Engine\JDK8\target\classes\a
                D:\GitCode\Engine\JDK8\target\classes\a\b
                D:\GitCode\Engine\JDK8\target\classes\a\io-test.txt
                D:\GitCode\Engine\JDK8\target\classes\a\加班.txt
            */
            paths.forEach(System.out::println);
        }
    }

    private static void readAttributes() throws URISyntaxException, IOException {
        BasicFileAttributes attrs = Files.readAttributes(getResourceFile(filename), BasicFileAttributes.class);
        System.out.println(attrs);
        Object fileKey = attrs.fileKey();
        FileTime creationTime = attrs.creationTime();
        FileTime accessTime = attrs.lastAccessTime();
        FileTime modifiedTime = attrs.lastModifiedTime();
        System.out.println(fileKey);
        System.out.println(creationTime);
        System.out.println(accessTime);
        System.out.println(modifiedTime);

    }

    private static void readAllLines() throws IOException, URISyntaxException {
        List<String> lines = Files.readAllLines(getResourceFile(filename));
        lines.stream().forEach(System.out::println);
    }

    private static void createDir() throws IOException {
        Path desktop = getDesktop();
        Path path = Paths.get(desktop.toAbsolutePath().toString(), "a");
        // 只能创建单层目录
        Path dir = Files.createDirectory(path);
        System.out.println(dir);
    }

    private static void createDirs() throws IOException {
        Path desktop = getDesktop();
        Path path = Paths.get(desktop.toAbsolutePath().toString(), "a", "b", "c");
        // 可创建多层目录
        Path dirs = Files.createDirectories(path);
        System.out.println(dirs);
    }

    private static void copy() {
        try (InputStream in = FilesDemo.class.getClassLoader().getResourceAsStream(filename)) {
            Path desktop = getDesktop();
            Path target = Paths.get(desktop.toAbsolutePath().toString(), filename);
            Files.copy(in, target, REPLACE_EXISTING);
            System.out.println(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getResourceFile(String filename) throws URISyntaxException {
        URL url = FilesDemo.class.getClassLoader().getResource(filename);
        return Paths.get(url.toURI());
    }

    private static Path getDesktop() {
        return Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
    }
}
