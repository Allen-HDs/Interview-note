package com.atguigu.jmm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName CASDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/9 18:34
 **/
public class CASDemo {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        // 获取真实值，并替换为相应的值
        boolean compareAndSet = atomicInteger.compareAndSet(5, 2019);//期望值和真实值一样就修改
        System.out.println(compareAndSet+"\t current data:"+atomicInteger.get());

        boolean compareAndSet1 = atomicInteger.compareAndSet(5, 1024);//期望值和真实值一样就修改
        System.out.println(compareAndSet1+"\t current data:"+atomicInteger.get());
    }
}
