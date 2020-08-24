package com.kiscode.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * Description: 
 * Author:  kisCode
 * CreateDate: 2020/8/24 20:57
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
    //注解参数类型 类型为String[] 参数名为value,默认值为""
    String[] value() default "";

    //注解参数类型为 int ,参数名为id,无默认值则要求使用注解时强制 填写注解参数
    int id();

/*    注解参数的可支持数据类型：
       1.基本数据类型（int,float,boolean,byte,double,char,long,short)
       2.String类型
　　　　3.Class类型
　　　　4.enum类型
　　　　5.Annotation类型
　　　　6.以上所有类型的数组
    */
}
