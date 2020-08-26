package com.kiscode.compiler;

import com.google.auto.service.AutoService;
import com.kiscode.annotation.Hello;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HelloProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //设置支持的注解类型
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(Hello.class.getCanonicalName());
        return supportTypes;
    }
/*
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //此处为java版本
        return SourceVersion.RELEASE_8;
    }*/

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "init HelloProcessor");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Hello.class);
//        if (elements.isEmpty()) return false;

        try {
            parseElements(elements);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void parseElements(Set<? extends Element> elements) throws IOException {
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                mMessager.printMessage(Diagnostic.Kind.NOTE, "parse:" + typeElement.getSimpleName().toString());
                String elementName = typeElement.getSimpleName().toString();
                //创建方法
                MethodSpec main = MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        //这里的$T和$S都必须大写否则会报错
                        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!" + elementName)
                        .build();
                //创建类
                TypeSpec helloWorld = TypeSpec.classBuilder("Hello" + elementName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(main)
                        .build();
                JavaFile javaFile = JavaFile.builder("com.kiscode.annotation", helloWorld).build();
                javaFile.writeTo(mFiler);
            }
        }
    }
}