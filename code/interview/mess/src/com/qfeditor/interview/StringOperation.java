package com.qfeditor.interview;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 获取出现最多的字符
 * @Author: Allen
 * @CreateDate: 2020/3/11 21:55
 */
class GetMaximumStr {
    /**
     * @MethodName: getMaximumStr
     * @MethodParam: []
     * @Return: void
     * @Description: 获取出现最多的字符 方法一 不推荐
     * @Author: Allen
     * @CreateDate: 2020/3/11 21:30
     */

    public void getMaximumStr1() {
        String str = "abcdcsbfjhsabcndsssssa";
        //统计每一个字符出现的次数
        char res = str.charAt(0);
        //最多出现了多少次
        int max = 0;

        for (int i = 0; i < str.length(); i++) {
            char temp = str.charAt(i);
            int count = 0;
            for (int j = 0; j < str.length(); j++) {
                char temp2 = str.charAt(j);
                if (temp == temp2) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
                res = temp;
            }
        }
        System.out.println(res + "出现次数最多:" + max);
    }


    /**
     * 功能描述
     *
     * @MethodName: getMaximumStr2
     * @MethodParam: []
     * @Return: void
     * @Description: 获取出现最多的字符 方法二
     * @Author: Allen
     * @CreateDate: 2020/3/11 21:54
     */

    public void getMaximumStr2() {
        String str = "abcdcsbfjhsabcndsssssa";
        //统计每一个字符出现的次数
        char res = str.charAt(0);
        //最多出现了多少次
        int max = 0;
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char temp = str.charAt(i);
            Integer count = map.get(temp);
            if (map.get(temp) == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(temp, count);
            if (count > max) {
                max = count;
                res = temp;
            }
        }
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + "出现了" + value + "次");
        }

        System.out.println(res + "出现次数最多:" + max);
    }
}

/**
 * @Description: 获取第一次重复的字符串
 * @Author: Allen
 * @CreateDate: 2020/3/11 22:07
 */

class GetTheFirstNotRepeatStr {

    public void getMaximumStr2() {
        String str = "abcab";
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            //存不进去表示已经有重复的了
            if (!set.add(str.charAt(i))) {
                System.out.println("第一次重复的字符串:" + str.charAt(i));
                break;
            }
        }

        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            //已经有重复的了
            if (map.containsKey(str.charAt(i))) {
                System.out.println("第一次重复的字符串:" + str.charAt(i));
                break;
            } else {
                map.put(str.charAt(i), 1);
            }
        }
    }
}

/**
 * @Description: 获取第一个只出现一次的字符串
 * @Author: Allen
 * @CreateDate: 2020/3/11 22:07
 */
class GetTheFirstOnlyAppearsOnceStr {
    public void getTheFirstOnlyAppearsOnceStr1() {
        String str = "abcab";
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            Integer count = map.get(str.charAt(i));
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(str.charAt(i), count);
        }

        for (int i = 0; i < str.length(); i++) {
            if (map.get(str.charAt(i)) == 1) {
                System.out.println("第一个只出现一次的字符串为:" + str.charAt(i));
                break;
            }
        }
    }

    /**
     * @MethodName: getTheFirstOnlyAppearsOnceStr2
     * @MethodParam: []
     * @Return: void
     * @Description: 字符第一次出现的位置和最后一次出现的位置是同一个位置 代表着只出现了一次
     * @Author: Allen
     * @CreateDate: 2020/3/11 22:14
     */
    public void getTheFirstOnlyAppearsOnceStr2() {
        String str = "abcab";
        for (int i = 0; i < str.length(); i++) {
            if (str.indexOf(str.charAt(i)) == str.lastIndexOf(str.charAt(i))) {
                System.out.println("第一个只出现一次的字符串为:" + str.charAt(i));
                break;
            }
        }
    }
}

/**
 * @Description: 统计手机号各个数字出现的次数, 按照升序输出
 * @Author: Allen
 * @CreateDate: 2020/3/11 22:15
 */
class GetEveryPhoneNumberAppearsCount {
    /**
     * @MethodName: getEveryPhoneNumberAppearsCount
     * @MethodParam: []
     * @Return: void
     * @Description: 桶排序  将手机号(0-9)看成数组下标  数组存的就是次数
     * @Author: Allen
     * @CreateDate: 2020/3/11 22:23
     */

    public void getEveryPhoneNumberAppearsCount() {
        String phone = "15556538951";
        int[] counts = new int[10];
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            //存的是该数组下标的次数  数组下标就表示手机号的数字
            counts[c - '0']++;
        }

        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                //代表该下标有数字出现
                System.out.println(i + "出现了" + counts[i] + "次");
            }
        }
    }
}

/**
 * @Description: 字符串反转
 * @Author: Allen
 * @CreateDate: 2020/3/11 22:20
 */
class ReverseStr {

    public void reverseStr1() {
        StringBuffer sb = new StringBuffer("abcdef");
        sb.reverse();
        System.out.println(sb.toString());
    }

    public void reverseStr2() {
        String str = "abcdef";
        StringBuffer sb = new StringBuffer();
        for (int i = str.length() - 1; i >= 0; i--) {
            sb.append(str.charAt(i));
        }
        System.out.println(sb.toString());
    }

    /**
     * @MethodName: reverseStr3
     * @MethodParam: []
     * @Return: void
     * @Description: 下标互换
     * @Author: Allen
     * @CreateDate: 2020/3/11 22:44
     */
    public void reverseStr3() {
        String str = "abcdef";
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length/2; i++) {
            char temp = chars[i];
            chars[i] = chars[chars.length -i -1];
            chars[chars.length -i -1] = temp;
        }
        System.out.println(new String(chars));
    }
}