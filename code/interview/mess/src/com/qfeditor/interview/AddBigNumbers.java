package com.qfeditor.interview;

import org.junit.Test;

/**
 * @BelongProjecet: interview
 * @BelongPackage: com.qfeditor.interview
 * @ClassName: AddBigNumbers
 * @Description: 两个大数字相加  位数均大于1000位
 *              不能直接用数据类型相加  因为已经超过范围了
 * @Author: Allen
 * @CreateDate: 2020/3/10 9:21
 * @Version: V1.0
 */
public class AddBigNumbers {

    @Test
    public void addBigNumbers() {
        String num1 = "852963741123654789";
        String num2 = "6352147896512365874";

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
        int sums [] = new int[large.length+1];

        for (int i = 0; i < large.length; i++) {
            //char转换成int  只需要减去0的ASCII
            sums[i] = large[large.length-i-1]-'0';
        }

        for (int i = 0; i < small.length; i++) {
            //char转换成int  只需要减去0的ASCII
            sums[i] += small[small.length-i-1]-'0';
        }
        for (int i = 0; i < sums.length-1; i++) {
            System.out.println(sums[i]);
            if (sums[i]>9){
                //进位
                sums[i+1] += sums[i]/10;
                sums[i] = sums[i]%10;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (int i = sums.length-1; i >=0; i--) {
            sb.append(sums[i]);
        }
        String result = sb.toString();
        if (result.startsWith("0")){
            result = result.substring(1);
        }
        System.out.println(result);
    }

}
