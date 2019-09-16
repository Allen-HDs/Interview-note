package com.atguigu.interview.testJMM;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName SpinLockDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/16 18:40
 **/
public class SpinLockDemo {
    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName()+"\t come in...");
        while (!atomicReference.compareAndSet(null,thread)){
            //loop
        }
    }

    public void myUnLock(){
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread,null);
        System.out.println(thread.getName()+"\t invoked myUnLock()");
    }

    public static void main(String[] args) {
        SpinLockDemo spinLockDemo = new SpinLockDemo();

        new Thread(()->{
            spinLockDemo.myLock();
            //AA进来后休眠五秒再释放  这样BB就一直自旋
            try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace();}
            spinLockDemo.myUnLock();
        },"AA").start();

        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace();}

        new Thread(()->{
            spinLockDemo.myLock();
            //为了演示不那么快  休眠一秒
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace();}
            spinLockDemo.myUnLock();
        },"BB").start();

    }
}
