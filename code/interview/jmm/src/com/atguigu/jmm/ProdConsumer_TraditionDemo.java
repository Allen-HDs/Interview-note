package com.atguigu.jmm;

/**
 * @ClassName ProdConsumer_TraditionDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/19 10:37
 **/

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 需求:一个初始值为0的变量,两个线程对其交替操作,一个加1,一个减1,来5轮
 */
public class  ProdConsumer_TraditionDemo {
    /**
     * 1.线程   操作  资源类
     * 2.判断   干活  通知
     * 3.防止虚假唤醒机制
     * @param args
     */
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                shareData.increment();
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                shareData.decrement();
            }
        },"BB").start();
    }
}

class ShareData{
    private volatile int number;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment(){
        lock.lock();
        try {
            while (number !=0){
                //不能生产 等待
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number++;
            System.out.println(Thread.currentThread().getName()+"\t"+number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }


    public void decrement(){
        lock.lock();
        try {
            while (number ==0){
                //不能消费 等待
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number--;
            System.out.println(Thread.currentThread().getName()+"\t"+number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
