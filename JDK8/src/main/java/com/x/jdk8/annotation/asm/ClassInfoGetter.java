package com.x.jdk8.annotation.asm;

// 不适用jdk内置,没有源码,不方便阅读参数语义
// import jdk.internal.org.objectweb.asm.ClassVisitor;

import com.x.doraemon.Strings;
import java.util.Arrays;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * @author AD
 * @date 2022/6/4 10:54
 */
public class ClassInfoGetter extends ClassVisitor {

    public ClassInfoGetter() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String format = Strings.replace("【{}】: version【{}】,access【{}】,name【{}】,signature【{}】,superName【{}】,interfaces【{}】",
                                        "visit",
                                        version,
                                        access,
                                        name,
                                        signature,
                                        superName,
                                        Arrays.toString(interfaces));
        System.out.println(format);
    }


    @Override
    public void visitSource(String source, String debug) {
        String format = Strings.replace("【{}】: source【{}】, debug【{}】", "visitSource", source, debug);
        System.out.println(format);
        super.visitSource(source, debug);
    }


    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String format = Strings.replace("【{}】: desc【{}】, visible【{}】", "visitAnnotation", desc, visible);
        System.out.println(format);
        return super.visitAnnotation(desc, visible);
    }


    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        String format = Strings.replace("【{}】: name【{}】, outerName【{}】, innerName【{}】, access【{}】",
                                        "visitInnerClass",
                                        name,
                                        outerName,
                                        innerName,
                                        access);
        System.out.println(format);
        super.visitInnerClass(name, outerName, innerName, access);
    }


    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        String format = Strings.replace("【{}】: access【{}】, name【{}】, desc【{}】, signature【{}】, value【{}】",
                                        "visitField",
                                        access,
                                        name,
                                        desc,
                                        signature,
                                        value);
        System.out.println(format);
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String format = Strings.replace("【{}】: access【{}】, name【{}】, desc【{}】, signature【{}】, exceptions【{}】",
                                        "visitMethod",
                                        access,
                                        name,
                                        desc,
                                        signature,
                                        Arrays.toString(exceptions));
        System.out.println(format);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        String format = Strings.replace("【{}】: typeRef【{}】, typePath【{}】, desc【{}】, visible【{}】",
                                        "visitTypeAnnotation",
                                        typeRef,
                                        typePath,
                                        desc,
                                        visible);
        System.out.println(format);
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        String format = Strings.replace("【{}】: attr【{}】", "visitAttribute", attr);
        System.out.println(format);
        super.visitAttribute(attr);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        String format = Strings.replace("【{}】: owner【{}】, name【{}】, desc【{}】", "visitOuterClass", owner, name, desc);
        System.out.println(format);
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public void visitEnd() {
        String format = Strings.replace("【{}】", "visitEnd");
        System.out.println(format);
        super.visitEnd();
    }

}
