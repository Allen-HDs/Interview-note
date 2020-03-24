package com.atguigu.jmm;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.atguigu.testjmm
 * @ClassName: Math
 * @Description: TODO
 * @Author: Allen
 * @CreateDate: 2020/3/18 21:11
 * @Version: V1.0
 */
public class Math {
    private static final int initData = 666;
    private static User user = new User("张三",20);

    //一个方法对应一块栈帧内存区域
    public int compute(){
        int a = 1;
        int b = 2;
        int c = (a+b)*10;
        return c;
    }

    public static void main(String[] args) {
        Math math = new Math();
        math.compute();
        System.out.println("test");
    }
}
