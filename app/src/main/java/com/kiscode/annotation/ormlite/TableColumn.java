package com.kiscode.annotation.ormlite;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * Description: 
 * Author:  Administrator
 * CreateDate: 2020/8/24 22:05
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableColumn {
    boolean id() default false;

    String name();

    int length() default 10;

    SqlTypeEnum type() default SqlTypeEnum.VARCHAR;
}
