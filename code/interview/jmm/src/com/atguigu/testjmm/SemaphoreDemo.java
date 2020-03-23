package com.atguigu.testjmm;

/**
 * @ClassName SemaphoreDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/17 12:34
 **/

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 场景:假设有3个车位,8个车去抢
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3, true);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();//获取一个许可
                    System.out.println(Thread.currentThread().getName() + "\t 抢到车位");
                    int time = new Random().nextInt(10);
                    try { TimeUnit.SECONDS.sleep(time); } catch (InterruptedException e) { e.printStackTrace(); }
                    System.out.println(Thread.currentThread().getName() + "\t 停车" + time + "秒,离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();//释放一个许可
                }
            }, String.valueOf(i)).start();
        }
    }
}
