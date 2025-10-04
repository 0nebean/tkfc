package com.tkfc.welus.resolver;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.param.UrlParam;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.core.toolkit.WebUtil;
import com.tkfc.welus.definition.abstracts.BaseFieldValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * url参数解析器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/28 10:49
 */
@Slf4j
public class UrlParamResolver extends BaseFieldValid implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(UrlParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws ServletRequestBindingException {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        //获取参数信息
        assert request != null;
        markRequestAccessTime(request);
        JSONObject param = WebUtil.getParamVoFromHttpServletRequest(JSONObject.class);
        markRequestAccessParam(request, param);
        UrlParam urlParam = methodParameter.getParameterAnnotation(UrlParam.class);
        String key = methodParameter.getParameterName();
        Class<?> type = null;
        Parameter[] parameters = methodParameter.getExecutable().getParameters();
        for (Parameter parameter : parameters) {
            if (Objects.equals(parameter.getName(), key)) {
                type = parameter.getType();
            }
        }
        Assert.notNull(type, String.format("un know type of param key [%s]", key));
        Object value = param.get(key);
        assert urlParam != null;
        if (urlParam.required() && Objects.isNull(value)) {
            handleMissingValue(key, methodParameter);
        } else if (!urlParam.required() && Objects.isNull(value) && StringUtil.isNotBlank(urlParam.defaultValue())) {
            return urlParam.defaultValue();
        } else if (!urlParam.required() && Objects.isNull(value) && StringUtil.isBlank(urlParam.defaultValue())) {
            return null;
        }
        checkValidAnnotations(methodParameter, key, value);
        log.debug("UrlParamResolver do resolveArgument , param = {} , key = {} , value = {}", param, key, value);
        return basicTypeCast(type, value);
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingPathVariableException(name, parameter);
    }

}