
- 注解概要
- java内置注解
- 元注解
- 自定义注解
- 反射解析注解
- 运行时注解
- java 8 注解增强


# 注解概要
注解是
注解的声明使用 ==@interface== 关键字


# java内置注解
java内置了5个注解，其中Deprecated、Override、SuppressWarnings使用最多
- Deprecated 标记方法或类已过时 可能存在风险，作用于方法、字段、构造函数、类、包
- Override 重写覆盖父类的方法,仅坐拥于方法
- SuppressWarnings 关闭编译器对类、方法、成员变量、变量初始化的警告
- SafeVarargs 安全的参数类型
- FunctionalInterface 函数接口

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE})
public @interface Deprecated {
}
```


```java
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface Override {
}
```

```java
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
public @interface SuppressWarnings {
    String[] value();
}
```


# 元注解
元注解是一种用于作用于注解上的注解
- **@Target**  标记这个注解的使用范围，如字段、方法、构造函数、类、包等；@Target取值(ElementType)：
    - CONSTRUCTOR: 用于描述构造器
    - FIELD: 用于描述字段
    - LOCAL_VARIABLE: 用于描述局部变量
    - METHOD: 用于描述方法
    - PACKAGE: 用于描述包
    - PARAMETER: 用于描述参数
    - TYPE: 用于描述类、接口(包括注解类型) 或enum声明
- **@Retention ** 标识注解如何保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问；Retention取值RetentionPolicy：
    -  SOURCE: 源文件中保存
    -  CLASS：在编译后.class文件保存
    -  RUNTIME：运行时保存
- @Documented  标记这些注解是否包含在用户文档中
- @Inherited  标记这个注解是继承于哪个注解类(默认 注解并没有继承于任何子类)


# 自定义注解
自定义注解格式 **@interface 注解名{}** ，编译时自动继承java.lang.annotation.Annotation接口
### 声明自定义注解

```java
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
```
### 使用自定义注解
```java
@Override
    @MyAnnotation(value = "Jordan",id = 10)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
```



