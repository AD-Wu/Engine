# 分析器说明(官方文档-7.14.1)

---
##简单设置
```
    PUT my-index-000001
    {
      "settings": {
        "analysis": {
          "analyzer": {
            "my_custom_analyzer": {
              "type": "custom",
              "tokenizer": "standard",
              "char_filter": [
                "html_strip"
              ],
              "filter": [
                "lowercase",
                "asciifolding"
              ]
            }
          }
        }
      }
    }

    POST my-index-000001/_analyze
    {
      "analyzer": "my_custom_analyzer",
      "text": "Is this <b>déjà vu</b>?"
    }
```
---
## 自定义分析器设置

```
    PUT my-index-000001
    {
      "settings": {
        "analysis": {
          "analyzer": {
            "my_custom_analyzer": {
              "char_filter": [
                "emoticons"
              ],
              "tokenizer": "punctuation",
              "filter": [
                "lowercase",
                "english_stop"
              ]
            }
          },
          "tokenizer": {
            "punctuation": {
              "type": "pattern",
              "pattern": "[ .,!?]"
            }
          },
          "char_filter": {
            "emoticons": {
              "type": "mapping",
              "mappings": [
                ":) => _happy_",
                ":( => _sad_"
              ]
            }
          },
          "filter": {
            "english_stop": {
              "type": "stop",
              "stopwords": "_english_"
            }
          }
        }
      }
    }

    POST my-index-000001/_analyze
    {
      "analyzer": "my_custom_analyzer",
      "text": "I'm a :) person, and you?"
    }
```
