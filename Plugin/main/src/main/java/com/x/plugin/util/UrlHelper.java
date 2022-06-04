package com.x.plugin.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author AD
 * @date 2022/5/17 1:23
 */
public class UrlHelper {
    private static final String JAR_URL_PATTERN = "jar:{}!/";

    public static URL toJarURL(File file) throws MalformedURLException {
        if(!file.exists()){
            return null;
        }
        return new URL(JAR_URL_PATTERN.replace("{}",file.toURI().toURL().toString()));
    }

}
