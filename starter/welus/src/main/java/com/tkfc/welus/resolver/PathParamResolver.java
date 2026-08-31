package com.tkfc.welus.resolver;

import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.action.RestAction;
import com.tkfc.core.common.annotations.web.param.PathParam;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.welus.definition.abstracts.BaseFieldValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Executable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路径参数解析器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/28 15:50
 */
@Slf4j
public class PathParamResolver extends BaseFieldValid implements HandlerMethodArgumentResolver {


    private final static String START_EXPRESSION = "[";
    private final static String END_EXPRESSION = "]";
    private final static String SPLIT_EXPRESSION = "/";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull(request);
        markRequestAccessTime(request);
        //获取参数信息
        List<String> uris = Optional.of(request).map(HttpServletRequest::getRequestURI).map(u -> u.split(SPLIT_EXPRESSION)).map(Arrays::asList).orElse(new ArrayList<>()).stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
        String[] methodMappings = getMethodMappings(methodParameter);
        String[] actionMappings = getActionMappings(methodParameter);
        String key = methodParameter.getParameterName();
        Class<?> type = methodParameter.getParameterType();
        log.debug("PathParamResolver do resolveArgument , uris = {} , methodMappings = {} , actionMappings = {}, field = {}", JsonUtil.toJson(uris), JsonUtil.toJson(methodMappings), JsonUtil.toJson(actionMappings), key);
        //去除action的mapping
        for (String actionMapping : actionMappings) {
            uris.remove(actionMapping);
        }
        //找到参数对应的占位信息
        for (String methodMapping : methodMappings) {
            if (methodMapping.contains(START_EXPRESSION) && methodMapping.contains(END_EXPRESSION)) {
                List<String> split = Arrays.stream(Optional.of(methodMapping).map(s -> s.split(SPLIT_EXPRESSION)).orElse(new String[0])).filter(StringUtil::isNotBlank).collect(Collectors.toList());
                for (int i = 0; i < split.size(); i++) {
                    String pathParamValue = split.get(i);
                    pathParamValue = pathParamValue.replace(START_EXPRESSION, StringPool.EMPTY);
                    pathParamValue = pathParamValue.replace(END_EXPRESSION, StringPool.EMPTY);
                    if (Objects.equals(key, pathParamValue)) {
                        String value = uris.get(i);
                        addMarkRequestAccessParam(request, key, value);
                        checkValidAnnotations(methodParameter, key, value);
                        Assert.notNull(type);
                        return basicTypeCast(type,value);
                    }
                }
            }
        }
        return null;
    }

    //获取action的mapping
    private static String[] getActionMappings(MethodParameter methodParameter) {
        String[] paths = new String[0];
        RestAction restAction = Optional.ofNullable(methodParameter).map(MethodParameter::getExecutable).map(Executable::getDeclaringClass).map(clazz -> clazz.getAnnotation(RestAction.class)).orElse(null);
        Action action = Optional.ofNullable(methodParameter).map(MethodParameter::getExecutable).map(Executable::getDeclaringClass).map(clazz -> clazz.getAnnotation(Action.class)).orElse(null);
        if (Objects.nonNull(restAction)) {
            paths = restAction.path();
        }
        if (Objects.nonNull(action)) {
            paths = action.path();
        }
        return paths;
    }


}
