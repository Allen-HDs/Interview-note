package com.atguigu.classloader;

import java.io.*;

/**
 * @ClassName CustomizeClassLoader
 * @Description 自定义类加载器
 * @Author yuxiang
 * @Date 2020/3/24 16:38
 **/
public class CustomizeClassLoader extends ClassLoader {
    private static final String fileSuffixExt = ".class";
    private String classLoaderName;
    private String loadPath;

    public void setLoadPath(String loadPath) {
        this.loadPath = loadPath;
    }

    public CustomizeClassLoader(ClassLoader parent, String classLoaderName) {
        /**
         * 指定当前类加载器的父类加载器
         */
        super(parent);
        this.classLoaderName = classLoaderName;
    }

    public CustomizeClassLoader(String classLoaderName) {
        /**
         * 使用appClassLoader加载器作为本类的加载器
         */
        super();
        this.classLoaderName = classLoaderName;
    }

    public CustomizeClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * 创建class的二进制名称
     *
     * @param name 类的二进制名称
     * @return
     */
    private byte[] loadClassData(String name) {
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            name = name.replace(".", "\\");
            String fileName = loadPath + name + fileSuffixExt;
            File file = new File(fileName);
            is = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int ch;
            while (-1 != (ch = is.read())) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos) {
                    baos.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);
        System.out.println("CustomizeClassLoader 加载类:===>"+name);
        //把字节数组转换为Class对象
        return defineClass(name,data,0,data.length);
    }
}
