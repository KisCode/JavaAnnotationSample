package com.kiscode.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * Description: 点击事件绑定
 * Author:  kisCode
 * CreateDate: 2020/8/27 20:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnClick {
    int value();
}

