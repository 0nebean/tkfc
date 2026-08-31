package com.tkfc.welus.resolver;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.common.annotations.web.response.JsonMapping;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import com.tkfc.welus.definition.abstracts.BaseFieldValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 路径参数解析器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/28 15:50
 */
@Slf4j
public class BodyParamResolver extends BaseFieldValid implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BodyParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull(request);
        markRequestAccessTime(request);
        //过滤没有注解的字段
        Object paramIn = WebUtil.getParamVoFromHttpServletRequestBody();
        if (paramIn instanceof JSONArray) {
            JSONArray paramInArray = (JSONArray) paramIn;
            Class<?> type = ReflectionUtil.getClassByTypeName(methodParameter.getGenericParameterType().getTypeName());
            if (Objects.isNull(type)) {
                return null;
            }
            Body bodyAnnotation = type.getAnnotation(Body.class);
            if (Objects.isNull(bodyAnnotation)) {
                return null;
            }
            List<Object> result = new ArrayList<>();
            List<Field> declaredFields = ReflectionUtil.getAccessibleFields(type);
            for (int i = 0; i < paramInArray.size(); i++) {
                JSONObject paramOut = new JSONObject();
                for (Field field : declaredFields) {
                    setMarkFieldValue(field, new ArrayList<>(), paramInArray.getJSONObject(i), paramOut);
                }
                try {
                    result.add(JsonUtil.toBean(JsonUtil.toJson(paramOut), type));
                } catch (Exception e) {
                    log.error("param json cast java bean failure , e = ", e);
                    return null;
                }
            }
            if (CollectionUtil.isEmpty(result)) {
                return null;
            }
            return result;
        } else {
            Class<?> type = methodParameter.getParameterType();
            Body bodyAnnotation = type.getAnnotation(Body.class);
            if (Objects.isNull(bodyAnnotation) || Objects.isNull(paramIn)) {
                return null;
            }
            List<Field> declaredFields = ReflectionUtil.getAccessibleFields(type);
            JSONObject paramOut = new JSONObject();
            for (Field field : declaredFields) {
                setMarkFieldValue(field, new ArrayList<>(), (JSONObject) paramIn, paramOut);
            }
            log.debug("BodyParamResolver do resolveArgument , paramIn = {},paramOut = {}", paramIn, paramOut);
            markRequestAccessParam(request, paramOut);
            return JsonUtil.toBean(JsonUtil.toJson(paramOut), type);
        }
    }

    /**
     * 给标记字段赋值
     *
     * @param field     目标字段
     * @param fieldKeys keys
     * @param paramIn   输入参数
     * @param paramOut  输出参数
     */
    private void setMarkFieldValue(Field field, List<String> fieldKeys, JSONObject paramIn, JSONObject paramOut) {
        Class<?> fieldType = field.getType();
        if (field.isAnnotationPresent(Body.class)) {
            fieldKeys.add(field.getName());
            for (Field childField : fieldType.getDeclaredFields()) {
                setMarkFieldValue(childField, fieldKeys, paramIn, paramOut);
            }
        } else if (field.isAnnotationPresent(BodyProperty.class)) {
            String key = field.getName();
            List<String> tempKeys = CollectionUtil.mergeList(fieldKeys, Collections.singletonList(key));
            Object value = JsonUtil.map(paramIn, Object.class, CollectionUtil.listToStringArr(tempKeys));
            JsonUtil.mapValue(paramOut, value, CollectionUtil.listToStringArr(tempKeys));
            if (field.isAnnotationPresent(JsonMapping.class)) {
                JsonMapping jsonMapping = field.getAnnotation(JsonMapping.class);
                String mappingField = StringUtil.isNotBlank(jsonMapping.mapping()) ? jsonMapping.mapping() : field.getName() + "Json";
                List<String> tempMappingKeys = CollectionUtil.mergeList(fieldKeys, Collections.singletonList(mappingField));
                String mappingValue = JsonUtil.map(paramIn, String.class, CollectionUtil.listToStringArr(tempMappingKeys));
                if (StringUtil.isNotBlank(mappingValue)) {
                    String fieldSimpleName = field.getType().getSimpleName();
                    if (Objects.equals(fieldSimpleName, JSONArray.class.getSimpleName())) {
                        tempMappingKeys = CollectionUtil.mergeList(fieldKeys, Collections.singletonList(field.getName()));
                        JsonUtil.mapValue(paramOut, JsonUtil.toJsonArray(mappingValue), CollectionUtil.listToStringArr(tempMappingKeys));
                    } else if (Objects.equals(fieldSimpleName, JSONObject.class.getSimpleName())) {
                        tempMappingKeys = CollectionUtil.mergeList(fieldKeys, Collections.singletonList(field.getName()));
                        JsonUtil.mapValue(paramOut, JsonUtil.toJsonObject(mappingValue), CollectionUtil.listToStringArr(tempMappingKeys));
                    }
                }
            }
            checkValidAnnotations(field, key, value);
        }
    }


}
