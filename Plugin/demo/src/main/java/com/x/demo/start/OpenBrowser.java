package com.x.demo.start;

import com.x.plugin.start.BaseOpenBrowser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author AD
 * @date 2022/5/28 18:05
 */
@Component
// @ConditionalOnProperty(prefix = "plugin", name = "open", havingValue = "true")
public class OpenBrowser extends BaseOpenBrowser {

    @Value("${server.port}")
    private int port;

    @Override
    public String[] getBrowseUrl() {
        return new String[]{"http://localhost:" + port + "/api/index.html"};
    }
}

