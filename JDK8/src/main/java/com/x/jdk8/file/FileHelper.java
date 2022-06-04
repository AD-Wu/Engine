package com.x.jdk8.file;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author AD
 * @date 2022/5/10 11:03
 */
public class FileHelper {

    public static URL getResourceURL() {
        URL resource = FileHelper.class.getClassLoader().getResource("");
        return resource;
    }

    public static Path getResource(String name){
        URL resource = FileHelper.class.getClassLoader().getResource(name);
        Path path = null;
        try {
            path = Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }
}
