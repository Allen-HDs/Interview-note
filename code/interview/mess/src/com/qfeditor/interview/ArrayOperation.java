package com.qfeditor.interview;

import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.qfeditor.interview
 * @ClassName: ArrayOperation
 * @Description: 数组相关算法
 * @Author: Allen
 * @CreateDate: 2020/3/14 11:23
 * @Version: V1.0
 */
public class ArrayOperation {

    /**
     * @MethodName: largestSubsequence
     * @Return: void
     * @Description: 求和最大的子序列
     * 给定一整数数列A1.A2,A3..An(可能有负数),求A1~An的一个子序列Ai~Aj的和最大,并输出子序列的内容
     * @Author: Allen
     * @CreateDate: 2020/3/14 11:24
     */

    @Test
    public void largestSubsequence() {
        int[] nums = {-1, -2, 1, 6, 1, -10, 100};
        int max = nums[0];

        //以第i个元素结尾的和最大的子序列的和 sums[1]:以角标为1的元素结尾的最大子序列的和
        int[] sums = new int[nums.length];
        sums[0] = nums[0];

        int end = 0;
        // 以第i个元素结尾的和最大的子序列 从第几开始的
        int[] starts = new int[nums.length];

        for (int i = 1; i < nums.length; i++) {
            //sums前面的子序列和大于0  只要大于0那么加上本身 肯定比其本身要大
            if (sums[i - 1] > 0) {
                sums[i] = sums[i - 1] + nums[i];
                starts[i] = starts[i - 1];
            } else {
                sums[i] = nums[i];
                starts[i] = i;
            }
            if (sums[i] > max) {
                max = sums[i];
                end = i;
            }
        }
        System.out.println(max + " start: " + starts[end] + ";end: " + end);
    }

    /**
     * @MethodName: deduplicationAscendingOrder1
     * @Description: 去重升序 方法1 冒泡
     * @Author: Allen
     * @CreateDate: 2020/3/14 12:14
     */
    @Test
    public void deduplicationAscendingOrder1() {
        int[] nums = {8, 3, 4, 4, 8, 7, 6, 2, 1, 4, 3, 0, 5, 6, 9, 5, 9};
        //排序
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] > nums[j]) {
                    int temp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = temp;
                }
            }
        }
        System.out.print("排好序的数组" + Arrays.toString(nums));
        System.out.println();
        //去重 最后一位单独判断
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] != nums[i + 1]) {
                System.out.print(nums[i]);
            }
        }
        System.out.print(nums[nums.length - 1]);
    }

    /**
     * @MethodName: deduplicationAscendingOrder2
     * @Description: 去重升序 方法2 利用Set集合
     * @Author: Allen
     * @CreateDate: 2020/3/14 12:14
     */
    @Test
    public void deduplicationAscendingOrder2() {
        int[] nums = {8, 3, 4, 4, 8, 7, 6, 2, 1, 4, 3, 0, 5, 6, 9, 5, 9};
        Set<Integer> set = new TreeSet<>();
        for (int num : nums) {
            set.add(num);
        }
        set.forEach((x) -> System.out.print(x));
    }

    /**
     * @MethodName: orderValueAndSequence
     * @Description: 给出随机的100个数, 序号为 1-100,按从小到大顺序输出,并输出相应序号
     *               定义两个数组 序号和值一一对应  值排序的时候序号也跟着动
     * @Author: Allen
     * @CreateDate: 2020/3/14 22:29
     */

    @Test
    public void orderValueAndSequence() {
        int[] nums = {8, 3, 4, 4, 8, 7, 6, 2, 1, 4, 3, 0, 5, 6, 9, 5, 9};
        int[] orders = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            //序号为 1-100
            orders[i] = i+1;
        }

        for (int i = 0; i < nums.length; i++) {
            for (int j = i+1;j<nums.length;j++){
                if (nums[i]>nums[j]){
                    int temp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = temp;
                    //因为已经一一对应 所以序号跟着交换
                    temp = orders[i];
                    orders[i] = orders[j];
                    orders[j] = temp;
                }
            }
        }
        for (int i = 0; i < nums.length; i++) {
            System.out.println("值: "+nums[i]+",序号:"+orders[i]);
        }

    }
}
