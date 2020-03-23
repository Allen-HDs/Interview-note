package com.qfeditor.interview;

import org.junit.Test;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.qfeditor.interview
 * @ClassName: AddBigNumbers
 * @Description: 两个大数字相乘  位数均大于1000位
 *              不能直接用数据类型相加  因为已经超过范围了
 * @Author: Allen
 * @CreateDate: 2020/3/10 10:40
 * @Version: V1.0
 */
public class MultiBigNumbers {

    @Test
    public void multiBigNumbers() {
        String num1 = "365";
        String num2 = "23";

        char[] large = null;
        char[] small = null;

        if (num1.length() >= num2.length()){
            large = num1.toCharArray();
            small = num2.toCharArray();
        }else {
            large = num2.toCharArray();
            small = num1.toCharArray();
        }

        //最终结果的位数  最高位可能是0
        int multis [] = new int[small.length+large.length];

        for (int i = small.length-1; i >= 0; i--) {
            for (int j = large.length-1; j >=0 ; j--) {
                int number1 = small[i] -'0';
                int number2 = large[j] -'0';
                multis[(large.length-j-1)+(small.length-i-1)] += number1 * number2;
                // 错位相加
                // i=1 j=2 multi=0
                // i=1 j=1 multi=1
                // i=1 j=0 multi=2
                // i=0 j=2 multi=1
                // i=0 j=1 multi=2
                // i=0 j=0 multi=3
            }
        }

        for (int i = 0; i < multis.length; i++) {
            if (multis[i]>9){
                multis[i+1] += multis[i]/10;
                multis[i] %= 10;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (int i = multis.length-1; i >=0 ; i--) {
            sb.append(multis[i]);
        }
        String result = sb.toString();
        if (result.startsWith("0")){
            result = result.substring(1);
        }
        System.out.println(result);
        System.out.println(23*365);
    }

}
