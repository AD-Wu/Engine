package com.x.isearch.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author AD
 * @date 2022/1/19 14:08
 */
// @Component
public class OpenBrowser extends Starter {

    @Value("${server.port}")
    private int port;

    @Override
    public String[] getBrowseUrl() {
        String smartDoc = "http://localhost:" + port + "/api/index.html";
        return new String[]{smartDoc};
    }
}
