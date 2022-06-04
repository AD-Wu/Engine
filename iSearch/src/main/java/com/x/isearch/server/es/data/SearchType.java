package com.x.isearch.server.es.data;

/**
 * @author AD
 * @date 2022/3/4 20:53
 */
public enum SearchType {
    /**
     * 单字查询
     */
    SIMPLE,

    /**
     * 中文分词检索
     */
    TEXT,

    /**
     * 自动补全
     */
    COMPLETION,
}
