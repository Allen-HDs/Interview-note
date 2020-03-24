package com.atguigu.classloader;


/**
 * @ClassName APPClassLoaderIsCustomerClassLoaderParent
 * @Description TODO
 * @Author yuxiang
 * @Date 2020/3/24 17:07
 **/
public class APPClassLoaderIsCustomerClassLoaderParent {
    public static void main(String[] args) throws ClassNotFoundException{
        test1();
    }
    public static void test1()throws ClassNotFoundException{
        CustomizeClassLoader customizeClassLoader = new CustomizeClassLoader("customizeClassLoader");
        //设置加载路径
        customizeClassLoader.setLoadPath("F:\\ClassLoaderTest\\");
        //通过自定义类加载器加载 AppisCustParentDemo
        Class<?> targetClass = customizeClassLoader.loadClass("com.atguigu.classloader.AppisCustParentDemo");
        System.out.println("targetClass 被class加载器加载..."+targetClass.getClassLoader());
    }
}
