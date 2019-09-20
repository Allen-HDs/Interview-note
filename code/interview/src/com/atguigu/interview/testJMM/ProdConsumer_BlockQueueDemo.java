package com.atguigu.interview.testJMM;

/**
 * @ClassName ProdConsumer_BlockQueueDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/19 15:47
 **/

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 需求:
 * 生产蛋糕,生产一个取一个,5秒钟之后大老板叫停,停止生产,停止消费
 */
public class ProdConsumer_BlockQueueDemo {
    public static void main(String[] args) {
        ProdConsumer prodConsumer = new ProdConsumer(new ArrayBlockingQueue<String>(10));
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "生产线程启动..");
                prodConsumer.myProd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Prod").start();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "消费线程启动..\n");
                prodConsumer.myConsumer();
                System.out.println();
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        //5秒钟之后停止生产消费
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace();}
        prodConsumer.stop();
    }

}

class ProdConsumer {
    private volatile boolean FLAG = true;

    private BlockingQueue<String> blockingQueue = null;

    private AtomicInteger atomicInteger = new AtomicInteger();

    public ProdConsumer(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println("使用" + blockingQueue.getClass().getName() + "消费队列\n");

    }

    public void myProd() throws Exception {
        String data = null;
        boolean retResult;
        while (FLAG) {
            data = String.valueOf(atomicInteger.getAndIncrement());
            retResult = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retResult) {
                System.out.println(Thread.currentThread().getName() + "\t 生产了" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 生产了" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + "老板叫停,不允许生产,表示FLAG= false,生产结束");
    }

    public void myConsumer() throws Exception {
        String data = null;
        while (FLAG) {
            data = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (data == null || data.equalsIgnoreCase("")) {
                System.out.println(Thread.currentThread().getName() + "\t 超过2秒没有取到蛋糕,消费退出");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 消费队列蛋糕" + data + "成功!");
            }
        }
    }

    public void stop(){
        this.FLAG = false;
    }
}