package com.x.plugin.core.filter;

import com.x.plugin.enums.UrlProtocol;
import java.net.URL;

/**
 * @author AD
 * @date 2022/5/17 11:12
 */
public class JarUrlFilter implements IFilter<URL>{

    @Override
    public boolean accept(URL url) {
        return UrlProtocol.jar.toString().equalsIgnoreCase(url.getProtocol());
    }
}
