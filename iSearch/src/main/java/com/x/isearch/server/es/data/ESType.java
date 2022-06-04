package com.x.isearch.server.es.data;

/**
 * 基于7.14
 * @author AD
 * @date 2022/2/19 15:09
 */
public enum ESType {

    // ---------------------- 基本数据类型(常用) ----------------------
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,

    /**
     * 用于full-text搜索
     */
    TEXT,

    /**
     * 默认字段类型
     * 1、用于term-level搜索,适用字段内容 IDs, email addresses, hostnames, status codes, zip codes, or tags
     * 2、用于搜索确切的完整值
     */
    KEYWORD,
    /**
     * 1、通配符,属于keyword家族,用于正则表达式搜索,类似like查找
     * 2、存储量低于keyword,但搜索速度比keyword慢
     */
    /*
    查询示例(内容:This string can be quite lengthy):
    GET my-index-000001/_search
    {
      "query": {
        "wildcard": {
          "my_wildcard": {
            "value": "*quite*lengthy"
          }
        }
      }
    }
    */
    WILDCARD,
    COMPLETION,

    /**
     * 1、日期格式,展示时以字符串展示,查询和排序时则换算成long
     * 2、可以自定义格式,如果没有则默认格式,可以存入3中格式:2015-01-01 | 2015-01-01T12:10:30Z | 1420070400001(毫秒值)
     * 3、自定义格式如下,用"||"表示分割,第一个格式会被用于展示时的格式化(将毫秒格式化成字符串)
     */
    /*
    PUT my-index-000001
        {
          "mappings": {
            "properties": {
              "date": {
                "type":   "date",
                "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis"
              }
            }
          }
        }
    */
    DATE,

    // ---------------------- 其它数据类型(不常用) ----------------------
    RANGE,
    IP,
    POINT,
    // 复杂数据类型
    /**
     * ES中并没有数组这种类型,直接存入数组即可,但数组元素的类型必须一致
     */
    // ARRAYS,
    OBJECT,
    /**
     * 1.嵌套类型是对象数据类型的一个专门版本，它允许对对象数组进行索引，以便它们可以相互独立地查询(对象数组)。
     * 2.如果需要为对象数组编制索引并保持数组中每个对象的独立性，请使用嵌套数据类型而不是对象数据类型。
     * 3.在内部，嵌套对象将数组中的每个对象作为单独的隐藏文档进行索引，这意味着每个嵌套对象都可以通过嵌套查询独立于其他对象进行查询
     */
    NESTED,
    // 地理数据类型
    GEO_POINT,
    GEO_SHAPE,

    // 用于索引的分析器设置
    STOP,
    PATTERN,
    CUSTOM,
    MAPPING;
}
