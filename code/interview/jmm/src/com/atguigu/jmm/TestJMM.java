package com.atguigu.jmm;

import java.util.concurrent.TimeUnit;

public class TestJMM {
    public static void main(String[] args) {
        /*测试volatile保证可见性
        testVisibility();*/

        /**
         * 2.测试volatile不保证原子性
         */
        MyData myData = new MyData();
        for (int i = 0;i<20;i++){
            new Thread(()->{
                for (int j = 0; j <1000 ; j++) {
                    myData.addPlusPlus();
                }
            },String.valueOf(i)).start();
        }
        //需要等待上面20个线程都全部计算完成之后，再用main线程取得最终结果看是多少
        while (Thread.activeCount()>2){
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName()+"\t finally number value :"+myData.number);
    }

    private static void testVisibility() {
        /**
         * 1.测试volatile保证可见性
         * 线程在自己的工作内存运算完成之后  将值刷新回主内存
         */
        MyData myData = new MyData();
        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"come in");
            try {
                TimeUnit.SECONDS.sleep(3);
                myData.addTo60();
                System.out.println(Thread.currentThread().getName()+"operate over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"线程一").start();
        while (myData.number!=60){

        }
        System.out.println(Thread.currentThread().getName()+"number value is:"+myData.number);
    }
}

class MyData{
    volatile int number = 0;
    public void addTo60(){
        this.number = 60;
    }
    public void addPlusPlus(){
        number++;
    }
}
