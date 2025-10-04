package com.tkfc.welus.handler;

import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.AssertFailException;
import com.tkfc.core.throwable.base.ErrorCode;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.WebUtil;
import com.tkfc.welus.definition.abstracts.BaseMethodChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ResponseErrorHandler extends BaseMethodChecker {

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws ServletException, IOException {
        String path = request.getRequestURI();
        String httpMethodName = Objects.requireNonNull(request.getMethod()).toString();
        Object param = WebUtil.getHttpServletRequest().getAttribute(TEMP_PARAM_KEY);
        String methodName = WebUtil.getHttpServletRequest().getMethod();
        log.error("action [" + path + "] access got an err = ", ex);
        boolean notAjax = !Objects.equals(WebUtil.getRequestHeader("content-type"), "application/json");
        boolean htmlReq = Optional.ofNullable(request.getHeader("Accept")).orElse(StringPool.EMPTY).startsWith("text/html");
        if (htmlReq) {
            request.setAttribute("REQUEST_FORWARD_EXCEPTION", ex);
            switch (ex.getClass().getName()) {
                case "org.springframework.security.access.AccessDeniedException":
                    request.getRequestDispatcher("/error/403").forward(request, response);
                    break;
                default:
                    Integer status = Optional.ofNullable(response.getStatus()).orElse(HttpStatus.OK.value());
                    if (status >= 400) {
                        request.getRequestDispatcher(String.format("/error/%s", status)).forward(request, response);
                    } else {
                        request.getRequestDispatcher("/error/500").forward(request, response);
                    }
                    break;
            }
            return null;
        }
        BaseResponse<Object> error = BaseResponse.error(ErrorCode.INTERNAL_ERROR.getCode());
        if (ex.getClass().getSimpleName().toLowerCase().contains("sql")) {
            error.setError("server error");
        } else {
            error.setError(ex.getMessage());
        }
        error.setPath(path);
        error.setData(new Object());
        error.setTimestamp(System.currentTimeMillis());
        if (ex instanceof AssertFailException) {
            AssertFailException assertFailException = (AssertFailException) ex;
            String simpleMessage = assertFailException.getSimpleMessage();
            String regex = "^(\\d+)-(.*)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(simpleMessage);
            if (matcher.matches()) {
                String errorCode = matcher.group(1);   // 连字符前：错误码
                String errorMessage = matcher.group(2); // 连字符后：错误信息
                error.setMessage(errorMessage);
                error.setError(errorCode);
            } else {
                error.setMessage(simpleMessage);
            }
        }
        //打印日志
        printErr(httpMethodName, path, param, methodName);
        return JsonUtil.toJsonObject(error);
    }

}