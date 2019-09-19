package com.atguigu.interview.testJMM;

/**
 * @ClassName CountDownLatchDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/17 10:56
 **/

import java.util.concurrent.CountDownLatch;

/**
 * 场景:下班之后,老总必须等所有员工都走完才能走并锁门
 */
public class CountDownLatchDemo {
    private static final int COUNT = 10;
    private static CountDownLatch countDownLatch = new CountDownLatch(COUNT);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+"\t 下班了,离开公司");
            }, String.valueOf(i)).start();
        }
        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName()+"\t 老总最后关门,离开公司");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
