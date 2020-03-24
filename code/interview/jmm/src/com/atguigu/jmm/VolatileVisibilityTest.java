package com.atguigu.jmm;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.atguigu.testjmm
 * @ClassName: VolatileVisibilityTest
 * @Description: volatile 保证可见性
 * @Author: Allen
 * @CreateDate: 2020/3/18 16:03
 * @Version: V1.0
 */
public class VolatileVisibilityTest {
    private static volatile boolean initFlag = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() ->{
            System.out.println("watting data");
            while (!initFlag){

            }
            System.out.println("===========success!");
        }).start();

        Thread.sleep(2000);

        new Thread(() ->{
            prepareData();
        }).start();
    }

    private static void prepareData(){
        System.out.println("preparing data...");
        initFlag = true;
        System.out.println("preparing end...");
    }
}
