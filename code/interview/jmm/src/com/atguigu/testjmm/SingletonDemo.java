package com.atguigu.testjmm;

public class SingletonDemo {
    private static SingletonDemo install = null;
    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName()+"\t 我是构造方法SingletonDemo()");
    }

    public static SingletonDemo getInstall() {
        if (null == install){
            synchronized (SingletonDemo.class){
                if (null == install){
                    install = new SingletonDemo();
                }
            }
        }
        return install;
    }

    public static void main(String[] args) {
//        System.out.println(SingletonDemo.getInstall() == SingletonDemo.getInstall());
//        System.out.println(SingletonDemo.getInstall() == SingletonDemo.getInstall());
//        System.out.println(SingletonDemo.getInstall() == SingletonDemo.getInstall());

        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                SingletonDemo.getInstall();
            },String.valueOf(i)).start();
        }
    }
}
