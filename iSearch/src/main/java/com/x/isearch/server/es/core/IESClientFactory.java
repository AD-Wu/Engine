package com.x.isearch.server.es.core;

/**
 * @author AD
 * @date 2022/3/4 13:55
 */
public interface IESClientFactory {

    IESClient getClient(String host, int port);

    IESClient getClient(String host, int port, String user, String pwd);

    String getESVersion();
}
