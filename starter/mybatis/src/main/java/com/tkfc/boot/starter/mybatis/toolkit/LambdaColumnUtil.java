package com.tkfc.boot.starter.mybatis.toolkit;

import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.function.SerializableFunction;
import com.tkfc.core.throwable.RunTimException;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Lambda 字段工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/11 17:24
 */
public class LambdaColumnUtil {

    public static <T> String getName(SerializableFunction<T, ?> fn) {
        String fieldName;
        Field field;
        try {
            // 从function取出序列化方法
            Method writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
            // 从序列化方法取出序列化的lambda信息
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);

            // 从lambda信息取出method、field、class等
            fieldName = serializedLambda.getImplMethodName().substring("get".length());
            fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new RunTimException("获取Lambda表达式字段名称失败", e);
        }

        // 从field取出字段名，可以根据实际情况调整
        FiledName tableField = field.getAnnotation(FiledName.class);
        if (tableField != null && tableField.value().length() > 0) {
            return tableField.value();
        } else {
            return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
        }
    }
}
