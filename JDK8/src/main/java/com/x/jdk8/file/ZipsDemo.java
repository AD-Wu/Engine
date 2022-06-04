package com.x.jdk8.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author AD
 * @date 2022/5/10 10:34
 */
public class ZipsDemo {

    public static void main(String[] args) throws Exception {
        readZip();
    }


    /**
     * 读取有异常发生,暂时不知道该方法有什么用
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void readZip() throws IOException, URISyntaxException {
        URL url = ZipsDemo.class.getClassLoader().getResource("Desktop.zip");
        URI uri = url.toURI();
        FileSystem fs = FileSystems.newFileSystem(Paths.get(uri), null);
        Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
