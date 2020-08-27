package com.kiscode.apt.library;


/**** 
 * Description: 自定义实现轻量版的ButterKnife
 * Author:  Administrator
 * CreateDate: 2020/8/27 20:27
 */
public class ButterKnife {
    public static <T> void bind(T object) {
        String className = object.getClass().getName() + "$$ViewBinder";
        try {
            Object viewBinderObj = Class.forName(className).newInstance();
            if (viewBinderObj instanceof ViewBinder) {
                ViewBinder<T> viewBinder = (ViewBinder<T>) viewBinderObj;
                viewBinder.bind(object);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
