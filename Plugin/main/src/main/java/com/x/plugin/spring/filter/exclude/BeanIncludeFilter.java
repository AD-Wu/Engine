package com.x.plugin.spring.filter.exclude;

import java.io.IOException;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 包含springboot主程序注解的启动类过滤器
 * @author AD
 * @date 2022/5/7 21:03
 */
public class BeanIncludeFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader reader, MetadataReaderFactory factory) throws IOException {
        String name = Bean.class.getName();
        AnnotationMetadata am = reader.getAnnotationMetadata();

        Set<MethodMetadata> annotatedMethods = am.getAnnotatedMethods(name);
        Set<String> annotationTypes = am.getAnnotationTypes();
        Set<String> metaAnnotationTypes = am.getMetaAnnotationTypes(name);
        boolean b1 = am.hasMetaAnnotation(name);
        boolean b = am.hasAnnotatedMethods(name);
        boolean isBean = am.hasAnnotation(name);
        return isBean;
    }
}
