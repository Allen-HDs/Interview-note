package com.atguigu.testjmm;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName ReentrantLockDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/16 17:49
 *
 * */
public class ReentrantLockDemo {
    private Lock lock = new ReentrantLock();//默认参数是false,即非公平锁

    public void print(){
        try {
            lock.lock();
            System.out.println("invoice print()...");
            add();
        }finally {
            lock.unlock();
        }
    }

    public void add() {
        try {
            lock.lock();
            lock.lock();
            System.out.println("invoice add()...");
        }finally {
            lock.unlock();
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        reentrantLockDemo.print();
        //invoice print()...
        //invoice add()...
    }
}
