package com.kiscode.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * Description: BindView注解，用于标记View 实现自动 view= findviewById(id)
 * Author:  Administrator
 * CreateDate: 2020/8/27 18:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface BindView {
    int value();
}
