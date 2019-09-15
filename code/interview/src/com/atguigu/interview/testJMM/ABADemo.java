package com.atguigu.interview.testJMM;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ABADemo {
    private static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    public static void main(String[] args) {
        new Thread(()->{
            atomicReference.compareAndSet(100,101);
            atomicReference.compareAndSet(101,100);
        },"线程一").start();

        new Thread(()->{
            //保证上面的线程先执行
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean change = atomicReference.compareAndSet(100, 2019);

            System.out.println(change+"\t"+atomicReference.get()); // 2019
        },"线程二").start();
    }
}
