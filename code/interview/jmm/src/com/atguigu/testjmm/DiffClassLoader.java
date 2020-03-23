package com.atguigu.testjmm;

import java.util.Arrays;
import java.util.List;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.atguigu.testjmm
 * @ClassName: DiffClassLoader
 * @Description: TODO
 * @Author: Allen
 * @CreateDate: 2020/3/23 23:05
 * @Version: V1.0
 */
public class DiffClassLoader {
    public static void main(String[] args) {
        String bootLoadName = "启动类加载器----加载的目录: ";
        bootClassLoaderLodingPath(bootLoadName);
        String extLoadName = "扩展类加载器----加载的目录: ";
        extClassLoaderLodingPath(extLoadName);
        String appLoadName = "系统类加载器----加载的目录: ";
        appClassLoaderLodingPath(appLoadName);
    }

    /**
     * 启动类加载器加载的职责
     * @MethodName: bootClassLoaderLodingPath
     */

    private static void bootClassLoaderLodingPath(String loadName){
        //获取启动类加载器加载的目录
        String bootLodingPath = System.getProperty("sun.boot.class.path");
        printLoaderPath(loadName, bootLodingPath);
    }

    /**
     * 扩展类加载器加载的职责
     * @MethodName: extClassLoaderLodingPath
     */
    private static void extClassLoaderLodingPath(String loadName){
        //获取扩展类加载器加载的目录
        String extLodingPath = System.getProperty("java.ext.dirs");
        printLoaderPath(loadName, extLodingPath);
    }

    /**
     * 系统类加载器加载的职责
     * @MethodName: appClassLoaderLodingPath
     */
    private static void appClassLoaderLodingPath(String loadName){
        //获取系统类加载器加载的目录
        String appLodingPath = System.getProperty("java.class.path");
        printLoaderPath(loadName, appLodingPath);
    }


    private static void printLoaderPath(String loadName, String lodingPath) {
        ///把加载的目录转为集合
        List<String> lodingPathList = Arrays.asList(lodingPath.split(";"));
        for (String path : lodingPathList) {
            System.out.println(loadName+path);
        }
    }
}
