package com.atguigu.testjmm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BlockingQueueDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/18 15:31
 **/
public class BlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException{
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t put 1");
                blockingQueue.put("1");

                System.out.println(Thread.currentThread().getName()+"\t put 2");
                blockingQueue.put("2");

                System.out.println(Thread.currentThread().getName()+"\t put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"AAA").start();


        new Thread(()->{
            try {
                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"BBB").start();
    }
}
