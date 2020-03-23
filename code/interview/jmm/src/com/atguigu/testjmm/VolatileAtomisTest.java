package com.atguigu.testjmm;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.atguigu.testjmm
 * @ClassName: VolatileAtomisTest
 * @Description: volatile不保证原子性
 * @Author: Allen
 * @CreateDate: 2020/3/19 10:24
 * @Version: V1.0
 */
public class VolatileAtomisTest {
    private static volatile int num = 0;
    private static void  increase(){
        num++;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread [] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    increase();
                }
            });
            threads[i].start();
        }

        for (Thread t :threads){
            t.join();
        }

        //num = 1000*10 = 10000 ??
        System.out.println(num);
    }
}
