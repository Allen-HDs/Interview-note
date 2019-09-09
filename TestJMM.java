package com.gtstar.mis.web;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TestJMM
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/2 18:24
 **/
public class TestJMM {
    public static void main(String[] args) {

        testVisibility();

        /**
         * 测试原子性
         */
//        testAtomic();

    }

    private static void testVisibility() {
        /**
         * 测试可见性
         */
        MyData myData = new MyData();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myData.addTo60();
            System.out.println(Thread.currentThread().getName() + "operate finished");
        }, "线程一").start();

        while (myData.number != 60) {
            // looping
        }
        System.out.println(Thread.currentThread().getName() + "number value is:" + myData.number);
    }

    private static void testAtomic() {
        /**
         * 测试原子性
         */
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                    myData.incrementAndGet();
                }
            }, String.valueOf(i)).start();
        }
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + "\t value :" + myData.number);
        System.out.println(Thread.currentThread().getName() + "\t value :" + myData.atomicInteger);
    }
}

class MyData {
    volatile int number = 0;

    public void addTo60() {
        this.number = 60;
    }

    //保证原子性的解决办法之一 加上synchronized
    public void addPlusPlus() {
        number++;
    }

    //保证原子性的解决办法之二
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public void incrementAndGet() {
        atomicInteger.incrementAndGet();
    }
}