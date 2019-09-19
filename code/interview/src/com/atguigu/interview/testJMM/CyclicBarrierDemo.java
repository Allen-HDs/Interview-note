package com.atguigu.interview.testJMM;

/**
 * @ClassName CyclicBarrierDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/17 12:12
 **/

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 场景:收集七颗龙珠才能召唤神龙
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()-> System.out.println("***龙珠收齐-召唤神龙***"));

        for (int i = 0; i < 7; i++) {
            final int intTemp = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"\t 收集到第"+intTemp+"颗龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
