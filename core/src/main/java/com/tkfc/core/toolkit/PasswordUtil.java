package com.tkfc.core.toolkit;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码生成工具类
 *
 * @author 0neBean
 * @since 2022-07-19 22:31:46
 */
public class PasswordUtil {


    /**
     * 生成数字密码
     *
     * @param notZero 不包含0
     * @param length  长度
     * @return 密码
     */
    public static String genNumberPassword(boolean notZero, int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        int result;
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            if (notZero) {
                do {
                    result = random.nextInt(10);
                } while (result == 0);
                val.append(result);
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }


    /**
     * 生成密码
     *
     * @param isContainSpecialCharacters 是否包含复杂自负
     * @param length                     长度
     * @return 密码
     */
    public static String genPassword(boolean isContainSpecialCharacters, int length) {
        if (isContainSpecialCharacters) {
            return geneComplexPassword(length);
        } else {
            return genSimplePassword(length);
        }
    }

    /**
     * 生成简单密码
     *
     * @param length 长度
     * @return 密码
     */
    private static String geneComplexPassword(int length) {
        StringBuilder password = new StringBuilder();
        char start;
        String test = "[A-Za-z/d\\-]*";
        Matcher m;
        int min = 33;
        int max = 126;
        while (password.length() != length) {
            start = (char) NumberUtil.getRandom(max, min);
            m = Pattern.compile(test).matcher(start + "");
            if (m.find() && (start != 34 && start != 92)) {
                password.append(start);
            }
        }
        return password.toString();
    }


    /**
     * 生成随机数字和字母
     *
     * @param length 长度
     * @return 密码
     */
    private static String genSimplePassword(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            }
            if ("num".equalsIgnoreCase(charOrNum)) {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }
}
