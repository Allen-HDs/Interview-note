package com.atguigu.interview.testJMM;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName SynchronziedDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/16 17:37
 **/
public class SynchronziedDemo {
    public synchronized void print(){
        System.out.println("invoice print()...");
        add();
    }

    public synchronized void add() {
        System.out.println("invoice add()...");
    }

    public static void main(String[] args) {
        SynchronziedDemo sync = new SynchronziedDemo();
        sync.print();
        //invoice print()...
        //invoice add()...
    }
}
