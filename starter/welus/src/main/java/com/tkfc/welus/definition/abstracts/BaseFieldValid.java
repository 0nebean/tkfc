package com.tkfc.welus.definition.abstracts;

import com.tkfc.core.common.annotations.web.validation.*;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.welus.definition.interfaces.FieldValidChecker;
import com.tkfc.welus.throwable.ValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 注解字段校验
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 11:33
 */
public abstract class BaseFieldValid extends BaseCovertWrapper implements FieldValidChecker {

    @Override
    public void checkValidAnnotations(MethodParameter methodParameter, String key, Object value) {
        CollectionSize collectionSize = methodParameter.getParameterAnnotation(CollectionSize.class);
        NotBlankString notBlankString = methodParameter.getParameterAnnotation(NotBlankString.class);
        NotEmptyCollection notEmptyCollection = methodParameter.getParameterAnnotation(NotEmptyCollection.class);
        NotNullValue notNullValue = methodParameter.getParameterAnnotation(NotNullValue.class);
        NumberRange numberRange = methodParameter.getParameterAnnotation(NumberRange.class);
        doValid(key, value, collectionSize, notBlankString, notEmptyCollection, notNullValue, numberRange);
    }

    @Override
    public void checkValidAnnotations(Field field, String key, Object value) {
        CollectionSize collectionSize = field.getAnnotation(CollectionSize.class);
        NotBlankString notBlankString = field.getAnnotation(NotBlankString.class);
        NotEmptyCollection notEmptyCollection = field.getAnnotation(NotEmptyCollection.class);
        NotNullValue notNullValue = field.getAnnotation(NotNullValue.class);
        NumberRange numberRange = field.getAnnotation(NumberRange.class);
        doValid(key, value, collectionSize, notBlankString, notEmptyCollection, notNullValue, numberRange);
    }

    @Override
    public Object basicTypeCast(Class<?> clazz, Object target) {
        if (Objects.isNull(target)) {
            return null;
        }
        String value = target.toString();
        if (value.equalsIgnoreCase("null") || value.equalsIgnoreCase("undefined")) {
            return null;
        }
        if (clazz.isAssignableFrom(Byte.class)) {
            return Byte.parseByte(value);
        } else if (clazz.isAssignableFrom(Short.class)) {
            return Short.parseShort(value);
        } else if (clazz.isAssignableFrom(Integer.class)) {
            return Integer.parseInt(value);
        } else if (clazz.isAssignableFrom(Long.class)) {
            return Long.parseLong(value);
        } else if (clazz.isAssignableFrom(Float.class)) {
            return Float.parseFloat(value);
        } else if (clazz.isAssignableFrom(Double.class)) {
            return Double.parseDouble(value);
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (clazz.isAssignableFrom(Character.class)) {
            return value;
        } else if (clazz.isAssignableFrom(Date.class)) {
            return DateUtil.stringToDate(value);
        } else if (clazz.isAssignableFrom(LocalDateTime.class)) {
            return DateUtil.dateToLocalDateTime(Objects.requireNonNull(DateUtil.stringToDate(value)));
        } else if (clazz.isAssignableFrom(LocalDate.class)) {
            return DateUtil.dateToLocalDate(Objects.requireNonNull(DateUtil.stringToDate(value)));
        }
        return value;
    }

    private void doValid(String key, Object value, CollectionSize collectionSize, NotBlankString notBlankString,
                         NotEmptyCollection notEmptyCollection, NotNullValue notNullValue, NumberRange numberRange) {
        if (Objects.nonNull(collectionSize)) {
            collectionSize(collectionSize, key, value);
        }
        if (Objects.nonNull(notBlankString)) {
            notBlankString(notBlankString, key, value);
        }
        if (Objects.nonNull(notEmptyCollection)) {
            notEmptyCollection(notEmptyCollection, key, value);
        }
        if (Objects.nonNull(notNullValue)) {
            notNullValue(notNullValue, key, value);
        }
        if (Objects.nonNull(numberRange)) {
            numberRange(numberRange, key, value);
        }
    }

    private void numberRange(NumberRange numberRange, String key, Object value) {
        long min = numberRange.min();
        long max = numberRange.max();
        String errMsg = numberRange.errMsg();
        long valLong = ParseUtil.toLong(value);
        if (valLong < min || valLong > max) {
            throw new ValidException(String.format((errMsg), key, max, min));
        }
    }

    private void notNullValue(NotNullValue notNullValue, String key, Object value) {
        String errMsg = notNullValue.errMsg();
        if (Objects.isNull(value)) {
            throw new ValidException(String.format((errMsg), key));
        }
    }

    private void notEmptyCollection(NotEmptyCollection notEmptyCollection, String key, Object value) {
        String errMsg = notEmptyCollection.errMsg();
        if (CollectionUtil.isEmpty((Collection<?>) value)) {
            throw new ValidException(String.format((errMsg), key));
        }
    }

    private void notBlankString(NotBlankString notBlankString, String key, Object value) {
        String errMsg = notBlankString.errMsg();
        if (Objects.isNull(value) || StringUtil.isBlank(value.toString())) {
            throw new ValidException(String.format((errMsg), key));
        }
    }


    private void collectionSize(CollectionSize collectionSize, String key, Object value) {
        int min = collectionSize.min();
        int max = collectionSize.max();
        String errMsg = collectionSize.errMsg();
        if (Objects.isNull(value)) {
            throw new ValidException(String.format("key [%s] can not be null", key));
        }
        if (!CollectionUtil.isArray(value)) {
            throw new ValidException(String.format("key [%s] is not list type", key));
        }
        List<?> list = (List<?>) value;
        if (list.size() < min || list.size() > max) {
            throw new ValidException(String.format((errMsg), key, max, min));
        }
    }


}
