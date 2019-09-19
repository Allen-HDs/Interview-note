package com.atguigu.interview.testJMM;

/**
 * @ClassName SyncAndReentrantLockDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/19 14:59
 **/

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 需求:多线程之间按顺序调用,实现 A->B->C 三个线程启动,如下:
 *      AA打印5次,BB打印10次,CC打印15次
 *      紧接着
 *      AA打印5次,BB打印10次,CC打印15次
 *      ....
 *      来10轮
 */
public class SyncAndReentrantLockDemo {
    public static void main(String[] args) {
        PreciseWakeUp preciseWakeUp = new PreciseWakeUp();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print5();
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print10();
            }
        },"BB").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print15();
            }
        },"CC").start();
    }
}

class PreciseWakeUp{
    private int temp = 1; //AA:1 ; BB:2 ; CC:3
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();


    public void print5(){
       lock.lock();
       try {
           //1.判断
           while (temp !=1){
               try {
                   c1.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           //2.干活
           for (int i = 0; i < 5; i++) {
               System.out.println(Thread.currentThread().getName()+"\t"+ i);
           }
           //3.通知
           temp = 2;
           c2.signal();

           
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           lock.unlock();
       }
    }

    public void print10(){
        lock.lock();
        try {
            //1.判断
            while (temp !=2){
                try {
                    c2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //2.干活
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName()+"\t"+ i);
            }
            //3.通知
            temp = 3;
            c2.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();
        try {
            //1.判断
            while (temp !=3){
                try {
                    c1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //2.干活
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName()+"\t"+ i);
            }
            //3.通知
            temp = 1;
            c1.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}