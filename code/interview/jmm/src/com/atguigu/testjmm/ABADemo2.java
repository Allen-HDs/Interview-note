package com.atguigu.testjmm;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABADemo2 {
    private static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100,1);

    public static void main(String[] args) {
        new Thread(()->{
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName()+"\t 第一次版本号为："+stamp);
            //保证两个线程的初始版本为一致
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

            //制造ABA问题
            atomicStampedReference.compareAndSet(100,101
                    ,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"\t 第二次版本号为："+atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(101,100
                    ,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"\t 第三次版本号为："+atomicStampedReference.getStamp());

        },"线程一").start();

        new Thread(()->{
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t 第一次版本号为：" + stamp);
            //等待线程一执行完
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019
                    , stamp, stamp + 1);
            System.out.println("是否已更改："+result);
            System.out.println("最终版本号为："+atomicStampedReference.getStamp()
                    +"\t当前的值为："+atomicStampedReference.getReference());
        },"线程二").start();

    }

    private static void run() {

    }
}
