package com.x.plugin.core.filter;

import com.x.plugin.core.PluginChecker;
import com.x.plugin.enums.UrlProtocol;
import java.net.URL;

/**
 * @author AD
 * @date 2022/5/17 12:05
 */
public class JarFileFilter implements IFilter<URL> {

    @Override
    public boolean accept(URL url) {
        // 来自文件系统的url
        return UrlProtocol.file.toString().equalsIgnoreCase(url.getProtocol()) && PluginChecker.endJar.accept(url.getFile());
    }
}
