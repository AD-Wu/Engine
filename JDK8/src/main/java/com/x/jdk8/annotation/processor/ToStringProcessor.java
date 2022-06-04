package com.x.jdk8.annotation.processor;

import java.util.Iterator;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

/**
 * 不知道该怎么用
 * @author AD
 * @date 2022/6/3 17:21
 */
// *表示通配符,处理所有注解
@SupportedAnnotationTypes("com.x.jdk8.annotation.processor.ToString")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ToStringProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment currentRound) {
        int size = annotations.size();
        System.out.println(size);
        Iterator<? extends TypeElement> it = annotations.iterator();
        while (it.hasNext()) {
            TypeElement next = it.next();
            Name name = next.getSimpleName();
            Element element = next.getEnclosingElement();
        }
        return false;
    }
}
