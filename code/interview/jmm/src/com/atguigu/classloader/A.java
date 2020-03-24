package com.atguigu.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName A
 * @Description TODO
 * @Author yuxiang
 * @Date 2020/3/24 18:06
 **/
public class A {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        CustomizeClassLoader customizeClassLoader1 = new CustomizeClassLoader("customizeClassLoader1");
        customizeClassLoader1.setLoadPath("F:\\ClassLoaderTest\\");

        CustomizeClassLoader customizeClassLoader2 = new CustomizeClassLoader("customizeClassLoader2");
        customizeClassLoader2.setLoadPath("F:\\ClassLoaderTest\\");

        Class<?> class1 = customizeClassLoader1.loadClass("com.atguigu.classloader.Person");
        System.out.println("class1 被class加载器加载..."+class1.getClassLoader());

        Class<?> class2 = customizeClassLoader2.loadClass("com.atguigu.classloader.Person");
        System.out.println("class2 被class加载器加载..."+class2.getClassLoader());

        System.out.println("class1 == class2 "+(class1 == class2)); //false

        //模拟问题
        Object person1 = class1.newInstance();
        Object person2 = class2.newInstance();

        Method method = class1.getMethod("setPerson", Object.class);
        method.invoke(person1,person2);
    }
}
