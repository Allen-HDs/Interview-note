package com.atguigu.testjmm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName MyThreadPool
 * @Description TODO 第4种获得/使用java 多线程的方式: 线程池
 * @Author yuxiang
 * @Date 2019/9/20 11:20
 **/
public class MyThreadPoolDemo {
    public static void main(String[] args) {
        //底层CPU核数
//        System.out.println(Runtime.getRuntime().availableProcessors());

        //一池5个处理线程
//        ExecutorService executor = Executors.newFixedThreadPool(5);
        //一池1个处理线程
//        ExecutorService executor = Executors.newSingleThreadExecutor();
        //一池N个处理线程
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i <10 ; i++) {
                executor.execute(()->{
                    System.out.println(Thread.currentThread().getName()+"处理任务");
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
    }
}
