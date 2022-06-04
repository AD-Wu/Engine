package com.x.plugin.spring.filter.exclude;

import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 包含springboot主程序注解的启动类过滤器
 * @author AD
 * @date 2022/5/7 21:03
 */
public class SpringbootAppFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader reader, MetadataReaderFactory factory) throws IOException {
        boolean isMainClass = reader.getAnnotationMetadata()
            .hasAnnotation("org.springframework.boot.autoconfigure.SpringBootApplication");
        return isMainClass;
    }
}
