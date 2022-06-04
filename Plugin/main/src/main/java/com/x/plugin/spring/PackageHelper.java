package com.x.plugin.spring;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author AD
 * @date 2022/6/1 15:49
 */
class PackageHelper {

    private final Set<String> packageNames;

    private final Set<String> excludeClassNames;

    PackageHelper(AnnotationMetadata metadata) {
        Set<String> pkgs1 = getBySpringBootApplication(metadata);
        Set<String> pkgs2 = getByAutoConfigurationPackage(metadata);
        Set<String> pkgs3 = getByComponentScan(metadata);
        Set<String> pkgs = new HashSet<>();
        pkgs.addAll(pkgs1);
        pkgs.addAll(pkgs2);
        pkgs.addAll(pkgs3);
        this.packageNames = Collections.unmodifiableSet(pkgs);
        this.excludeClassNames = getExcludeClassNames(metadata);
    }

    Set<String> getPackageNames() {
        return this.packageNames;
    }

    public Set<String> getExcludeClassNames() {
        return excludeClassNames;
    }

    private static Set<String> getExcludeClassNames(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes
            .fromMap(metadata.getAnnotationAttributes(SpringBootApplication.class.getName(), false));
        String[] excludeNames = attributes.getStringArray("excludeName");
        Set<String> names = Arrays.stream(excludeNames).collect(Collectors.toSet());
        for (Class<?> clazz : attributes.getClassArray("exclude")) {
            names.add(clazz.getName());
        }
        if (names.isEmpty()) {
            return new HashSet<>();
        }
        return names;
    }

    private static Set<String> getBySpringBootApplication(AnnotationMetadata metadata) {
        return getPackageNames(metadata, SpringBootApplication.class, "scanBasePackages", "scanBasePackageClasses");
    }

    private static Set<String> getByComponentScan(AnnotationMetadata metadata) {
        return getPackageNames(metadata, ComponentScan.class, "basePackages", "basePackageClasses");
    }

    private static Set<String> getByAutoConfigurationPackage(AnnotationMetadata metadata) {
        return getPackageNames(metadata, AutoConfigurationPackage.class, "basePackages", "basePackageClasses");
    }

    private static Set<String> getPackageNames(AnnotationMetadata metadata, Class<? extends Annotation> anno, String annoPackageField, String annoPackageClassesField) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(anno.getName(), false));
        String[] pkgs = attributes.getStringArray(annoPackageField);
        Set<String> pkgNames = Arrays.stream(pkgs).collect(Collectors.toSet());
        for (Class<?> clazz : attributes.getClassArray(annoPackageClassesField)) {
            pkgNames.add(clazz.getPackage().getName());
        }
        if (pkgNames.isEmpty()) {
            pkgNames.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return pkgNames;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.packageNames.equals(((PackageHelper) obj).packageNames);
    }

    @Override
    public int hashCode() {
        return this.packageNames.hashCode();
    }

    @Override
    public String toString() {
        return "Package Imports " + this.packageNames;
    }

}
