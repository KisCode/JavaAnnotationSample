package com.kiscode.compiler;


import com.google.auto.service.AutoService;
import com.kiscode.annotation.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/****
 * Description: BindView注解处理器
 * Author:  Administrator
 * CreateDate: 2020/8/27 18:50
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private static final String PARAMETER_NAME_TARGET = "target";
    private static final String METHOD_NAME_BIND = "bind";
    private static final String INTERFACE_FULL_NAME = "com.kiscode.apt.library.ViewBinder";

    private Messager mMessager;
    private Elements elementUitls;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        elementUitls = processingEnv.getElementUtils();

        mMessager.printMessage(Diagnostic.Kind.NOTE, "start BindViewProcessor init()");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //返回支持的java版本
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        mMessager.printMessage(Diagnostic.Kind.NOTE, "SupportedAnnotationType:" + BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        //获取所有被BindView注解的元素Element,可以是 接口、类、枚举、构造函数、方法、字段等等各种element
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        HashMap<TypeElement, List<Element>> hashMap = convertToMap(elements);

        mMessager.printMessage(Diagnostic.Kind.NOTE, "process type size:" + hashMap.size());
        writeToFile(hashMap);
        return false;
    }


    /***
     * 将所用满足BindView注解的元素进行组装
     * 以所属的类 TypeElement作为key
     * 以指定类下所有的FieldElement集合作为Value
     * @param elements   满足BindView注解的元素集合
     * @return HashMap
     */
    private HashMap<TypeElement, List<Element>> convertToMap(Set<? extends Element> elements) {
        HashMap<TypeElement, List<Element>> hashMap = new HashMap<>();
        for (Element element : elements) {
            // BindView注解作用在FIELD上，element.getKind ==ElementKind.FIELD
            //element.getEnclosingElement();  获取包围字段的对象 即该字段所属的类
            TypeElement classElement = (TypeElement) element.getEnclosingElement();

            if (hashMap.containsKey(classElement)) {
                hashMap.get(classElement).add(element);
            } else {
                List<Element> fields = new ArrayList<>();
                fields.add(element);
                hashMap.put(classElement, fields);
            }
        }

        return hashMap;
    }


    /***
     *
     * @param hashMap
     */
    private void writeToFile(HashMap<TypeElement, List<Element>> hashMap) {

        for (Map.Entry<TypeElement, List<Element>> entry : hashMap.entrySet()) {
            List<Element> elements = entry.getValue();
            TypeElement typeElement = entry.getKey();
            ClassName className = ClassName.get(typeElement);


            //创建参数 参数类型Class当前注解所在Activty.class ,参数名为target
            ParameterSpec parameterSpec = ParameterSpec.builder(className, PARAMETER_NAME_TARGET).build();

            //拼接方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_NAME_BIND)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec);

            for (Element element : elements) {
                String fieldName = element.getSimpleName().toString();
                int viewId = element.getAnnotation(BindView.class).value();
                //拼接成target.tv=target.findViewById(R.id.tv)
                String methodContent = "$N." + fieldName + " = $N.findViewById($L)";
                //添加方法中具体代码
                methodBuilder.addStatement(methodContent, PARAMETER_NAME_TARGET, PARAMETER_NAME_TARGET, viewId);

            }

            MethodSpec methodSpec = methodBuilder.build();

            //通过具体包名+类获取ViewBinder接口 ，即类型为com.kiscode.apt.library.ViewBinder的 TypeElement
            TypeElement
                    viewBinderType = elementUitls.getTypeElement(INTERFACE_FULL_NAME);
            // 获取 ViewBinder<className>
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(viewBinderType), className);

            TypeSpec typeSpec = TypeSpec.classBuilder(className.simpleName() + "$$ViewBinder")
                    .addMethod(methodSpec)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(parameterizedTypeName)  //设置继承的接口
                    .build();

            mMessager.printMessage(Diagnostic.Kind.NOTE, "className:" + className.toString());


            try {
                JavaFile.builder(className.packageName(), typeSpec)
                        .build()
                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void bind(int viewId) {

    }
}
