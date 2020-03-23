package com.atguigu.testjmm;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @ClassName CallableDemo
 * @Description TODO
 * @Author yuxiang
 * @Date 2019/9/20 15:15
 **/
public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 在 FutureTask 中传入 Callable 的实现类
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            private volatile int num = 10;
            @Override
            public Integer call() throws Exception {
                num --;
                return num;
            }
        });

        //把 futureTask 放入线程中
        new Thread(futureTask).start();
        //获取结果
        Integer result = futureTask.get();
        System.out.println(result);
    }
}
