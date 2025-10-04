package com.tkfc.core.toolkit;

import com.tkfc.core.throwable.base.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 类型转换
 *
 * @author 0neBean
 */
public class ParseUtil {

    /**
     * 转换成基础类型
     *
     * @param value 目标值
     * @param clazz 转换的类型
     * @return T
     * @author liwenbin
     * @since 2022/6/16 0:16
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBasicTypeValue(Object value, Class<T> clazz) {
        if (Objects.equals(clazz, BigDecimal.class)) {
            return (T) toBigDecimal(value.toString());
        }
        if (Objects.equals(clazz, Double.class)) {
            return (T) toDouble(value.toString());
        }
        if (Objects.equals(clazz, Integer.class)) {
            return (T) toInt(value.toString());
        }
        if (Objects.equals(clazz, Boolean.class)) {
            return (T) toBoolean(value.toString());
        }
        if (Objects.equals(clazz, Long.class)) {
            return (T) toLong(value.toString());
        }
        if (Objects.equals(clazz, Float.class)) {
            return (T) toFloat(value.toString());
        }
        return null;
    }

    private static Boolean toBoolean(String value) {
        Assert.notEmpty(value, "empty string value can not cast to boolean value");
        return Boolean.parseBoolean(value);
    }

    /**
     * 将任意类型强制转换成double型, 如果值为空返回零
     *
     * @param value 目标值
     * @return double
     */
    public static Double toDouble(Object value) {
        if (value == null) {
            return 0D;
        }
        double d = 0;
        String temp = value.toString();
        value = temp.replace(",", "");
        try {
            d = Double.parseDouble(String.valueOf(value));
        } catch (Exception ignored) {
        }
        return d;
    }

    /**
     * 无损转换
     *
     * @param value 目标值
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Object value) {
        return Objects.nonNull(value) ? new BigDecimal(value.toString()) : null;
    }

    /**
     * 无损转换
     *
     * @param value 目标值
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }

    /**
     * 无损转换
     *
     * @param value 目标值
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Double value) {
        return new BigDecimal(value.toString());
    }

    /**
     * 无损转换
     *
     * @param value 目标值
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Integer value) {
        return new BigDecimal(value.toString());
    }

    /**
     * 转换成double
     *
     * @param value 目标值
     * @param dig   指定小数位数
     * @return double
     */
    public static Double toDouble(Object value, int dig) {
        double d = toDouble(value);
        return toDouble(d, dig);
    }

    /**
     * 四舍五入保留小数
     *
     * @param value 目标值
     * @param dig   指定小数位数
     * @return double
     */
    public static Double toDouble(double value, int dig) {
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd1 = bd.setScale(dig, RoundingMode.HALF_UP);
        value = bd1.doubleValue();
        return toBigDecimal(value, dig).doubleValue();
    }

    /**
     * 四舍五入保留小数
     *
     * @param value 目标值
     * @param dig   指定小数位数
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(double value, int dig) {
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(dig, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入保留小数
     *
     * @param value 目标值
     * @param dig   指定小数位数
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(BigDecimal value, int dig) {
        return toBigDecimal(value.doubleValue(), dig);
    }

    /**
     * 将任意类型强制转换成int型, 如果值为空返回零
     *
     * @param value 目标值
     * @return int
     */
    public static Integer toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Double) {
            value = BigDecimal.valueOf((Double) value);
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        }
        int d = 0;
        String temp = value.toString();
        value = temp.replace(",", "");
        try {
            d = Integer.parseInt(value + "");
        } catch (Exception ignored) {
        }
        return d;
    }

    /**
     * 字符串转布尔
     *
     * @param value 目标值
     * @return boolean
     */
    public static Boolean toBoolean(Object value) {
        boolean b = false;
        try {
            b = Boolean.parseBoolean(String.valueOf(value));
        } catch (Exception ignored) {
        }
        return b;
    }

    /**
     * 将任意类型强制转换成long型, 如果值为空返回零
     *
     * @param value 目标值
     * @return long
     */
    public static Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        long l = 0L;
        try {
            l = Long.parseLong(value.toString());
        } catch (Exception ignored) {
        }
        return l;
    }

    /**
     * 将任意类型强制转换成long型, 如果值为空返回零
     *
     * @param value 目标值
     * @return float
     */
    public static Float toFloat(Object value) {
        float f = 0;
        try {
            f = Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return f;
        }
        return f;
    }

    /**
     * 转换成String
     *
     * @param value 目标值
     * @return string
     */
    public static String toString(Object value) {
        return String.valueOf(value);
    }
}
