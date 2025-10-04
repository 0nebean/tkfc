package com.tkfc.core.toolkit;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 数字工具类
 *
 * @author 0neBean
 * @since 2021-12-05 23:58:11
 */
@SuppressWarnings("all")
public class NumberUtil {

    /**
     * 是否是数字
     *
     * @param object 入参
     * @return bool
     */
    public static boolean isNum(Object object) {
        boolean emptyString = StringUtil.isEmpty(object);
        Integer dotsCount = StringUtil.countChars(object.toString(), StringPool.DOT);
        boolean tooManyDots = dotsCount > 1;
        boolean allNumber = object.toString().matches("[+-]?[0-9]+(\\.[0-9]+)?");
        boolean numberString = object.toString().startsWith("0") && !Objects.equals("0",object) && dotsCount == 0;
        return !emptyString && !tooManyDots && allNumber && !numberString;
    }

    /**
     * a 小于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aXYb(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    /**
     * a 等于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aEQb(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    /**
     * a 不等于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aNEQb(BigDecimal a, BigDecimal b) {
        return !aEQb(a, b);
    }

    /**
     * a 大于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aDYb(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    /**
     * a 大于等于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aDYEQb(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > -1;
    }

    /**
     * a 小于等于 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aXYEQb(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 1;
    }

    /**
     * 是否是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isMinus(BigDecimal val) {
        return val.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 是否是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isMinus(Integer val) {
        return val < 0;
    }

    /**
     * 是否是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isMinus(Long val) {
        return val < 0;
    }

    /**
     * 不是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isNotMinus(BigDecimal val) {
        return !isMinus(val);
    }

    /**
     * 不是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isNotMinus(Integer val) {
        return !isMinus(val);
    }

    /**
     * 不是负数
     *
     * @param val 值
     * @return bool
     */
    public static Boolean isNotMinus(Long val) {
        return !isMinus(val);
    }

    /**
     * a 是否足够 扣除 b
     *
     * @param a a
     * @param b b
     * @return bool
     */
    public static Boolean aEnoughSubtractionB(BigDecimal a, BigDecimal b) {
        return !isMinus(a.subtract(b));
    }

    /**
     * 获取指定范围随机数
     *
     * @param max 最大值
     * @param min 最小值
     * @return int
     */
    public static int getRandom(int max, int min) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 冒泡排序 获取最大值
     *
     * @param numbers 数字
     * @return max
     */
    public static int max(int... numbers) {
        int max = Integer.MIN_VALUE;
        for (int i : numbers) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    /**
     * 冒泡排序 获取最小值
     *
     * @param numbers 数字
     * @return min
     */
    public static int min(int... numbers) {
        int min = Integer.MAX_VALUE;
        for (int i : numbers) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    /**
     * 冒泡排序 获取最大值
     *
     * @param number 数字
     * @return max
     */
    public static int max(List<Integer> numbers) {
        int max = Integer.MIN_VALUE;
        for (int i : numbers) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    /**
     * 冒泡排序 获取最小值
     *
     * @param numbers 数字
     * @return min
     */
    public static int min(List<Integer> numbers) {
        int min = Integer.MAX_VALUE;
        for (int i : numbers) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    /**
     * 冒泡排序 获取最大值
     *
     * @param number 数字
     * @return max
     */
    public static BigDecimal maxBigDecimal(List<BigDecimal> numbers) {
        Assert.notEmpty(numbers);
        BigDecimal max = numbers.get(0);
        if (numbers.size() == 1){
            return max;
        }
        for (BigDecimal i : numbers) {
            if (aDYb(i,max)) {
                max = i;
            }
        }
        return max;
    }

    /**
     * 冒泡排序 获取最小值
     *
     * @param numbers 数字
     * @return min
     */
    public static BigDecimal minBigDecimal(List<BigDecimal> numbers) {
        Assert.notEmpty(numbers);
        BigDecimal min = numbers.get(0);
        if (numbers.size() == 1){
            return min;
        }
        for (BigDecimal i : numbers) {
            if (aXYb(i,min)) {
                min = i;
            }
        }
        return min;
    }
}
