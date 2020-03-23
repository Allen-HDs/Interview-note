package com.qfeditor.interview;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.qfeditor.interview
 * @ClassName: Singleton
 * @Description:
 * <p>
 * 单例模式:单例类在整个程序中只能有一个实例,这个类负责创建自己的对象,并确保只有一个对象被创建
 * 使用场景:全局使用的类、会消耗很多系统资源的，比如数据库连接池、工厂类和数据库连接池等
 * 实现要点:1.私有构造器;2.持有该类的属性;3.对外提供获取实例的方法
 * <p>
 *
 * <p>
 * 饿汉式:线程安全、反射不安全、反序列化不安全
 * 登记式:线程安全、防止反射攻击、反序列化不安全
 * 枚举式:线程安全、支持序列化、防止反射攻击、反序列化安全
 * 懒汉式:线程不安全、延迟加载（两种加同步，效率低）
 * 双检锁:线程安全、volatile
 * ThreadLocal:不加锁,以空间换时间,为每个线程提供变量的独立副本,可以保证各自线程中是单例的,但是不同线程之间不保证
 * CAS:无锁乐观策略,线程安全
 * <p>
 * @Author: Allen
 * @CreateDate: 2020/3/8 20:56
 * @Version: V1.0
 */
public class Singleton {
    //懒汉式
    private static Singleton instance = new Singleton();

    private Singleton() {

    }

    public static Singleton getInstance() {
        return instance;
    }

    private Object readResolve() {
        //防止反序列化
        return instance;
    }
}

class Singleton1 {
    //登记式
    private static class SingletonHolder {
        private static Singleton1 instance1 = new Singleton1();
    }

    private Singleton1() {
        if (SingletonHolder.instance1 != null) {
            throw new IllegalStateException();
        }
    }

    public static Singleton1 getInstance() {
        return SingletonHolder.instance1;
    }

    private Object readResolve() {
        //防止反序列化
        return SingletonHolder.instance1;
    }
}

enum Singleton2 {
    //枚举式
    INSTANCE {
        @Override
        protected void getInstance() {
            System.out.println("do Something");
        }
    };

    protected abstract void getInstance();
}

class Singleton3 {
    //懒汉式1(线程不安全)
    private static Singleton3 instance = null;

    private Singleton3() {

    }

    public static Singleton3 getInstance() {
        if (instance == null) {
            instance = new Singleton3();
        }
        return instance;
    }
}

class Singleton4 {
    //懒汉式2(线程安全)
    private static Singleton4 instance = null;

    private Singleton4() {

    }

    public static synchronized Singleton4 getInstance() {
        if (instance == null) {
            instance = new Singleton4();
        }
        return instance;
    }
}

class Singleton5 {
    //懒汉式3(线程安全)
    private static Singleton5 instance = null;

    private Singleton5() {

    }

    public static Singleton5 getInstance() {
        synchronized (Singleton5.class) {
            if (instance == null) {
                instance = new Singleton5();
            }
        }
        return instance;
    }
}

class Singleton6 {
    //懒汉式4(线程安全) 双重检索
    private static volatile Singleton6 instance = null;

    private Singleton6() {

    }

    public static Singleton6 getInstance() {
        if (instance == null) {
            synchronized (Singleton6.class) {
                if (instance == null) {
                    instance = new Singleton6();
                }
            }
        }
        return instance;
    }
}

class Singleton7 {
    //ThreadLocal
    private static Singleton7 instance = null;

    private Singleton7() {

    }

    private final static ThreadLocal<Singleton7> threadLocalSingleton = new ThreadLocal<Singleton7>() {
        @Override
        protected Singleton7 initialValue() {
            return new Singleton7();
        }
    };
    public static Singleton7 getInstance() {
        return threadLocalSingleton.get();
    }
}

class Singleton8{
    //CAS
    private static final AtomicReference<Singleton8> instance = new AtomicReference<>();

    private Singleton8(){

    }

    public static final Singleton8 getInstance(){
        while (true){
            Singleton8 current = instance.get();
            if (current != null){
                return current;
            }
            current = new Singleton8();
            if (instance.compareAndSet(null,current)){
                return current;
            }
        }
    }
}