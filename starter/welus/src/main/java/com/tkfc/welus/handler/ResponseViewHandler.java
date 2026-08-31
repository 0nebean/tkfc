package com.tkfc.welus.handler;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.report.RecordeAccessLog;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.core.toolkit.WebUtil;
import com.tkfc.welus.definition.abstracts.BaseMethodChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * 返回结果解析器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/28 15:50
 */
@Slf4j
@SuppressWarnings("all")
public class ResponseViewHandler extends BaseMethodChecker implements HandlerMethodReturnValueHandler {


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return isViewAccess(returnType);
    }

    @Override
    public void handleReturnValue(Object body, MethodParameter returnType, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        HttpServletRequest servletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Type genericParameterType = returnType.getGenericParameterType();
        Class<?> ParameterClass = ReflectionUtil.getClass(genericParameterType.getTypeName());
        Long accessStartTimestamp = Optional.of(servletRequest).map(r -> r.getAttribute(TEMP_TIMESTAMP)).map(ParseUtil::toLong).orElse(System.currentTimeMillis());
        long now = System.currentTimeMillis();
        long executionTime = now - accessStartTimestamp;
        String path = servletRequest.getRequestURI();
        String accessIp = WebUtil.getIpAddress();
        String methodName = returnType.getMethod().getName();
        String httpMethodName = Objects.requireNonNull(servletRequest.getMethod()).toString();
        String accessDateStr = DateUtil.getCurrentDateFormatString();
        Object param = servletRequest.getAttribute(TEMP_PARAM_KEY);
        JSONObject headersJson = WebUtil.getAllRequestHeader();
        boolean recordeAccessLog = returnType.getMethod().isAnnotationPresent(RecordeAccessLog.class);
        //打印日志
        print(httpMethodName, path, param, methodName, executionTime, body, headersJson, accessIp, accessDateStr, recordeAccessLog);
        modelAndViewContainer.setViewName(body.toString());
        modelAndViewContainer.setStatus(HttpStatus.OK);
        modelAndViewContainer.setRequestHandled(false);
    }


}
