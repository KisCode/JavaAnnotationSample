package com.kiscode.compiler;


import com.google.auto.service.AutoService;
import com.kiscode.annotation.OnClick;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/****
 * Description: 
 * Author:  OnClick注解处理器
 * CreateDate: 2020/8/27 21:00
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class OnClickProcessor extends AbstractProcessor {

    private static final String FIELD_NAME_TARGET = "target";
    private static final String PARAMETER_NAME_TARGET = "target";
    private static final String PARAMETER_NAME_VIEW = "view";
    private static final String INTERFACE_FULL_NAME = "com.kiscode.apt.library.OnEvent";

    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;

        //获取所有OnClick注解的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnClick.class);
        HashMap<TypeElement, List<Element>> typeElementListHashMap = convertToMap(elements);

        writeToFile(typeElementListHashMap);
        return false;
    }

    private void writeToFile(HashMap<TypeElement, List<Element>> typeElementListHashMap) {
        for (Map.Entry<TypeElement, List<Element>> entry : typeElementListHashMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            List<Element> elementList = entry.getValue();
            ClassName className = ClassName.get(typeElement);

            //声明一个类属性 private className target
            FieldSpec fieldSpec = FieldSpec.builder(className, FIELD_NAME_TARGET)
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            //创建参数 参数类型Class当前注解所在Activty.class ,参数名为target
            ParameterSpec parameterSpec = ParameterSpec.builder(className, PARAMETER_NAME_TARGET).build();

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setOnClickListener")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec);
            //setOnClickListener 对类属性 target进行赋值
            methodBuilder.addStatement("this.$N = $N", FIELD_NAME_TARGET, PARAMETER_NAME_TARGET);

            TypeElement viewTypeElement = mElementUtils.getTypeElement("android.view.View");
            TypeName viewTypeName = TypeName.get(viewTypeElement.asType());
            ParameterSpec parameterSpecOnClick = ParameterSpec.builder(viewTypeName, PARAMETER_NAME_VIEW).build();

            // Override onClick方法
            MethodSpec.Builder onclickMethodBuilder = MethodSpec.methodBuilder("onClick")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(parameterSpecOnClick);

            CodeBlock.Builder switchCodeBlockBuilder = CodeBlock.builder();
//            switchCodeBlockBuilder.add("switch (view.getId()) {");
            switchCodeBlockBuilder.beginControlFlow("switch (view.getId()) ");
            for (Element element : elementList) {
                OnClick onClickAnnotation = element.getAnnotation(OnClick.class);
                String methodName = element.getSimpleName().toString();
                int viewId = onClickAnnotation.value();
                String methodContent = "$N.findViewById($L).setOnClickListener(this)";
                methodBuilder.addStatement(methodContent, PARAMETER_NAME_TARGET, viewId);

                String onClickCaseContent = "\ncase $L:\n $N.$N();\n\tbreak;";
                switchCodeBlockBuilder.add(onClickCaseContent, viewId, FIELD_NAME_TARGET, methodName);
            }
            switchCodeBlockBuilder.add("\ndefault:").add("\n\tbreak;\n");
//            switchCodeBlockBuilder.add("}");
            switchCodeBlockBuilder.endControlFlow();
            onclickMethodBuilder.addStatement(switchCodeBlockBuilder.build());

            TypeElement supperTypeElement = mElementUtils.getTypeElement("android.view.View.OnClickListener");
            TypeName supperTypeName = TypeName.get(supperTypeElement.asType());
            MethodSpec methodSpec = methodBuilder.build();

            MethodSpec onClickMethodSpec = onclickMethodBuilder.build();

            //通过具体包名+类获取ViewBinder接口 ，即类型为com.kiscode.apt.library.ViewBinder的 TypeElement
            TypeElement
                    viewBinderType = mElementUtils.getTypeElement(INTERFACE_FULL_NAME);
            // 获取 OnEvent<className>
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(viewBinderType), className);

            //声明类
            TypeSpec typeSpec = TypeSpec.classBuilder(className.simpleName() + "$$OnClick")
                    .addModifiers(Modifier.PUBLIC)
                    .addField(fieldSpec)
                    .addMethod(methodSpec)
                    .addMethod(onClickMethodSpec)
                    .addSuperinterface(supperTypeName)
                    .addSuperinterface(parameterizedTypeName)
                    .build();
            try {
                JavaFile.builder(className.packageName(), typeSpec).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
