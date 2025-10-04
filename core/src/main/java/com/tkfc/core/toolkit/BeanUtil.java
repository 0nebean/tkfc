package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * bean工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class BeanUtil {

    private final static String ID = "id";

    private final static String[] CHAR_TYPE_LOWER_CASE_ARRAY = {"char", "varchar", "text", "longtext", "tinytext", "mediumtext", "blob", "tinyblob", "mediumblob", "longblob"};
    private final static String JSON_TYPE_LOWER_CASE = "json";
    private final static String INT_TYPE_LOWER_CASE = "int";
    private final static String BIGINT_TYPE_LOWER_CASE = "bigint";
    private final static String NUMERIC_TYPE_LOWER_CASE = "numeric";
    private final static String DOUBLE_TYPE_LOWER_CASE = "double";
    private final static String DECIMAL_TYPE_LOWER_CASE = "decimal";
    private final static String FLOAT_TYPE_LOWER_CASE = "float";
    private final static String DATE_TYPE_LOWER_CASE = "date";
    private final static String TIME_TYPE_LOWER_CASE = "time";

    private final static String LONG_TYPE_CAMEL_CASE = "Long";
    private final static String DOUBLE_TYPE_CAMEL_CASE = "Double";
    private final static String INTEGER_TYPE_CAMEL_CASE = "Integer";
    private final static String BIG_DECIMAL_TYPE_CAMEL_CASE = "BigDecimal";
    private final static String STRING_TYPE_CAMEL_CASE = "String";
    private final static String JSON_TYPE_CAMEL_CASE = "JSONObject";
    private final static String FLOAT_TYPE_CAMEL_CASE = "Float";
    private final static String LOCAL_DATE_TIME_TYPE_CAMEL_CASE = "LocalDateTime";

    private final static String DATETIME_TYPE_UPPER_CASE = "DATETIME";
    private final static String TIMESTAMP_TYPE_UPPER_CASE = "TIMESTAMP";
    private final static String INT_TYPE_UPPER_CASE = "INT";
    private final static String INTEGER_TYPE_UPPER_CASE = "INTEGER";
    private final static String BLOB_TYPE_UPPER_CASE = "BLOB";
    private final static String TEXT_TYPE_UPPER_CASE = "TEXT";
    private final static String LONG_VARCHAR_TYPE_UPPER_CASE = "LONGVARCHAR";

    /**
     * 从a拷贝属性到b
     *
     * @param a source
     * @param b target
     * @author 0neBean
     * @since 2021-12-05 23:53:21
     */
    public static void copyA2B(Object a, Object b) {
        org.springframework.beans.BeanUtils.copyProperties(a, b);
    }

    /**
     * 判断一个类是否是基本数据类型或包装类
     * @param clazz 类型
     * @return 是否是基础数据类型或包装类
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || isWrapperClass(clazz);
    }

    /**
     * 判断一个类是否是包装类
     * @param clazz 类型
     * @return 是否是包装类
     */
    public static boolean isWrapperClass(Class<?> clazz) {
        return clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Short.class ||
                clazz == Byte.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Character.class ||
                clazz == Boolean.class;
    }

    /**
     * 获取javaType
     *
     * @param jdbcType jdbcType
     * @return javaType
     */
    public static String getJavaTypeByJdbcType(String jdbcType) {
        String javaType = "";
        if (jdbcType.contains(JSON_TYPE_LOWER_CASE)) {
            javaType = JSON_TYPE_CAMEL_CASE;
        }
        if (CollectionUtil.contains(CHAR_TYPE_LOWER_CASE_ARRAY, jdbcType)) {
            javaType = STRING_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(INT_TYPE_LOWER_CASE)) {
            javaType = INTEGER_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(BIGINT_TYPE_LOWER_CASE)) {
            javaType = LONG_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(NUMERIC_TYPE_LOWER_CASE) || jdbcType.contains(DOUBLE_TYPE_LOWER_CASE)) {
            javaType = DOUBLE_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(DECIMAL_TYPE_LOWER_CASE)) {
            javaType = BIG_DECIMAL_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(FLOAT_TYPE_LOWER_CASE)) {
            javaType = FLOAT_TYPE_CAMEL_CASE;
        }
        if (jdbcType.contains(TIME_TYPE_LOWER_CASE) || jdbcType.contains(DATE_TYPE_LOWER_CASE)) {
            javaType = LOCAL_DATE_TIME_TYPE_CAMEL_CASE;
        }
        return javaType;
    }

    /**
     * 获取javaType
     *
     * @param jdbcType   jdbcType
     * @param columnName columnName
     * @return javaType
     */
    public static String getJavaTypeByJdbcType(String jdbcType, String columnName) {
        if (jdbcType.contains(INT_TYPE_LOWER_CASE) && columnName.contains(ID)) {
            return LONG_TYPE_CAMEL_CASE;
        }
        return getJavaTypeByJdbcType(jdbcType);
    }

    /**
     * 转换mybatis类型
     *
     * @param jdbcType jdbcType
     * @return javaType
     */
    public static String covertJdbcType2MybatisType(String jdbcType) {
        jdbcType = jdbcType.toUpperCase();
        if (DATETIME_TYPE_UPPER_CASE.equals(jdbcType)) {
            return TIMESTAMP_TYPE_UPPER_CASE;
        }
        if (jdbcType.contains(INT_TYPE_UPPER_CASE)) {
            return INTEGER_TYPE_UPPER_CASE;
        }
        if (jdbcType.contains(BLOB_TYPE_UPPER_CASE)) {
            return BLOB_TYPE_UPPER_CASE;
        }
        if (jdbcType.contains(TEXT_TYPE_UPPER_CASE)) {
            return LONG_VARCHAR_TYPE_UPPER_CASE;
        }
        return jdbcType;
    }
}
