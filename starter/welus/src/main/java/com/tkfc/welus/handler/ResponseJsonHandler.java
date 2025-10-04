package com.tkfc.welus.handler;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.report.RecordeAccessLog;
import com.tkfc.core.common.annotations.web.response.NoWrapEnum;
import com.tkfc.core.common.annotations.web.response.NoWrapResponse;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.WebUtil;
import com.tkfc.welus.definition.abstracts.BaseCovertWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * 通用返回拦截
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/22 10:11
 */
@ControllerAdvice
@Slf4j
@SuppressWarnings("all")
public class ResponseJsonHandler extends BaseCovertWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return isJsonAccess(returnType);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //包装json字段
        warpFieldJsonValue(body);
        if (Objects.isNull(returnType.getMethod().getAnnotation(NoWrapEnum.class))) {
            wrapFieldValue(body);
        }
        //打印日志
        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
        HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();
        Type genericParameterType = returnType.getGenericParameterType();
        Long accessStartTimestamp = Optional.of(servletRequest).map(r -> r.getAttribute(TEMP_TIMESTAMP)).map(ParseUtil::toLong).orElse(System.currentTimeMillis());
        long now = System.currentTimeMillis();
        long executionTime = now - accessStartTimestamp;
        String path = request.getURI().getPath();
        String accessIp = WebUtil.getIpAddress();
        String methodName = returnType.getMethod().getName();
        String httpMethodName = Objects.requireNonNull(request.getMethod()).toString();
        String accessDateStr = DateUtil.getCurrentDateFormatString();
        Object param = servletRequest.getAttribute(TEMP_PARAM_KEY);
        HttpHeaders headers = request.getHeaders();
        JSONObject headersJson = JsonUtil.toJsonObject(headers);
        boolean recordeAccessLog = returnType.getMethod().isAnnotationPresent(RecordeAccessLog.class);
        //打印日志
        print(httpMethodName, path, param, methodName, executionTime, body, headersJson, accessIp, accessDateStr, recordeAccessLog);

        if (Objects.isNull(body)) {
            body = new JSONObject();
        }

        //如果是BaseResponse直接返回
        Class<?> returnClazz = body.getClass();
        if (returnClazz.isAssignableFrom(BaseResponse.class)) {
            return body;
        }

        BaseResponse<Object> ok = BaseResponse.ok();
        //包装实体类
        NoWrapResponse noWrapResponse = Optional.of(returnType).map(MethodParameter::getMethod).map(m -> m.getAnnotation(NoWrapResponse.class)).orElse(null);
        if (Objects.isNull(body)) {
            body = new JSONObject();
        }
        if (Objects.nonNull(noWrapResponse)) {
            return body;
        }
        if (Objects.equals(body.getClass(), BaseResponse.class)) {
            ok = (BaseResponse) body;
        } else {
            log.debug("ResponseBodyAnalysis beforeBodyWrite returnType is {} , do wrap", body.getClass().getSimpleName());
            ok.setData(body);
        }
        ok.setPath(path);
        ok.setTimestamp(now);
        return ok;
    }

}