package com.tkfc.welus.definition.abstracts;

import com.alibaba.fastjson2.JSONWriter;
import com.tkfc.core.common.annotations.web.response.Enum;
import com.tkfc.core.common.annotations.web.response.JsonMapping;
import com.tkfc.core.common.annotations.web.response.MaxShowLength;
import com.tkfc.core.common.annotations.web.response.Wrap;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.toolkit.*;
import com.tkfc.welus.definition.interfaces.FieldCovertWrapper;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 字段包装类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/9 16:21
 */
public abstract class BaseCovertWrapper extends BaseMethodChecker implements FieldCovertWrapper {

    @Override
    public void wrapFieldValue(Object target) {
        Object tempTarget = target;
        if (Objects.isNull(tempTarget)) {
            return;
        }
        if (tempTarget.getClass().equals(BaseResponse.class)) {
            tempTarget = ((BaseResponse<?>) tempTarget).getData();
        }

        Class<?> targetClazz = tempTarget.getClass();

        if (List.class.isAssignableFrom(targetClazz)) {
            if (CollectionUtil.isEmpty((Collection<?>) tempTarget)) {
                return;
            } else {
                List<?> tempList = (List<?>) tempTarget;
                targetClazz = tempList.stream().filter(Objects::nonNull).findFirst().map(Object::getClass).orElse(null);
            }
        }
        if (Objects.isNull(targetClazz)) {
            return;
        }
        if (!targetClazz.isAnnotationPresent(Wrap.class)) {
            return;
        }

        if (List.class.isAssignableFrom(tempTarget.getClass())) {
            CollectionUtil.forEachArray((List<?>) tempTarget, this::doCovertField);
        } else {
            doCovertField(tempTarget);
        }
    }

    @Override
    public void warpFieldJsonValue(Object target) {
        for (Field field : ReflectionUtil.getAccessibleFields(target.getClass())) {
            if (field.isAnnotationPresent(JsonMapping.class)) {
                JsonMapping jsonMapping = field.getAnnotation(JsonMapping.class);
                String mappingField = StringUtil.isNotBlank(jsonMapping.mapping()) ? jsonMapping.mapping() : field.getName() + "Json";
                Object fieldVal = ReflectionUtil.invokeGetterMethod(target, field.getName());
                ReflectionUtil.invokeSetterMethod(target, mappingField, JsonUtil.toJsonWithFeatures(fieldVal, JSONWriter.Feature.PrettyFormat));
            }
        }
    }

    private void doCovertField(Object tempTarget) {
        List<Field> fields = Optional.ofNullable(tempTarget).map(Object::getClass).map(Class::getDeclaredFields).map(f -> Arrays.asList(f)).orElse(Collections.EMPTY_LIST);
        if (CollectionUtil.isEmpty(fields)) {
            return;
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Wrap.class)) {
                Object fieldValue = ReflectionUtil.getFieldValue(tempTarget, field.getName());

                if (fieldValue instanceof List) {
                    List<?> listValue = (List<?>) fieldValue;
                    for (Object val : listValue) {
                        List<Field> fieldList = Optional.ofNullable(val).map(Object::getClass).map(Class::getDeclaredFields).map(f -> Arrays.asList(f)).orElse(Collections.EMPTY_LIST);
                        for (Field f : fieldList) {
                            doSetEnumValue(val, f);
                            doSetMaxLengthValue(val, f);
                        }
                    }
                } else {
                    List<Field> fieldList = Optional.ofNullable(fieldValue).map(Object::getClass).map(Class::getDeclaredFields).map(f -> Arrays.asList(f)).orElse(Collections.EMPTY_LIST);
                    for (Field f : fieldList) {
                        doSetEnumValue(fieldValue, f);
                        doSetMaxLengthValue(fieldValue, f);
                    }
                }
            } else {
                doSetEnumValue(tempTarget, field);
                doSetMaxLengthValue(tempTarget, field);
            }
        }
    }

    /**
     * 包装枚举字段具体逻辑
     *
     * @param target 目标
     * @param field  字段
     */
    private static void doSetEnumValue(Object target, Field field) {
        Object enumValue = null;
        // 判断字段上是否有次注解
        if (field.isAnnotationPresent(Enum.class)) {
            // 获取注解
            Enum fieldAnnotation = field.getAnnotation(Enum.class);
            Class<?> enumClz = fieldAnnotation.using();
            Object val = ReflectionUtil.invokeGetterMethod(target, field.getName());
            if (Objects.isNull(val)) {
                return;
            }
            String value = val.toString();
            try {

                enumValue = EnumsUtil.getDescriptionByValue(enumClz, value);
//                enumValue = ReflectionUtil.invokeMethod(enumClz, GET_DESCRIPTION_BY_VALUE_METHOD_NAME, CollectionUtil.asArray(String.class), CollectionUtil.asArray(value));
            } catch (Exception ignore) {
            }
            if (StringUtil.isEmpty(enumValue)) {
                enumValue = "-";
            }
            ReflectionUtil.invokeSetterMethod(target, field.getName(), enumValue);
        }
    }

    /**
     * 设置过长的字段最终截取的内容
     *
     * @param target 目标
     * @param field  字段
     */
    private static void doSetMaxLengthValue(Object target, Field field) {
        if (field.isAnnotationPresent(MaxShowLength.class)) {
            MaxShowLength annotation = field.getAnnotation(MaxShowLength.class);
            int maxlength = annotation.maxlength();
            Object val = ReflectionUtil.invokeGetterMethod(target, field.getName());
            String strVal = val.toString();
            String finalValue = (StringUtil.isNotBlank(strVal) && strVal.length() > maxlength) ? StringUtil.concat(strVal.substring(0, maxlength), "...") : strVal;
            ReflectionUtil.invokeSetterMethod(target, field.getName(), finalValue);
        }
    }

}
