package com.tkfc.welus.definition.abstracts;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.SpringUtil;
import com.tkfc.core.toolkit.WebUtil;
import com.tkfc.welus.definition.interfaces.AccessLogPrinter;
import com.tkfc.welus.pojo.AccessLog;
import com.tkfc.welus.interceptor.logger.AccessLogRecorder;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

/**
 * 基础日志输出
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/5 15:07
 */
@Slf4j
public abstract class BaseLogPrinter implements AccessLogPrinter {

    public final static String TEMP_PARAM_KEY = "TEMP_PARAM_KEY";
    public final static String TEMP_TIMESTAMP = "TEMP_TIMESTAMP";
    private final static String SERVER_SERVLET_PRINT_ACCESS_LOG = "welus.action.access.log.level";
    private final static String ZERO = "0";

    @Override
    public void markRequestAccessTime(HttpServletRequest request) {
        request.setAttribute(TEMP_TIMESTAMP, System.currentTimeMillis());
    }

    @Override
    public void markRequestAccessParam(HttpServletRequest request, Object param) {
        if (Objects.isNull(param)) {
            param = WebUtil.getParamVoFromHttpServletRequest(JSONObject.class);
        }
        if (Objects.isNull(param)) {
            param = new Object();
        }
        request.setAttribute(TEMP_PARAM_KEY, JsonUtil.toJsonWithFeatures(param));
    }

    @Override
    public void addMarkRequestAccessParam(HttpServletRequest request, String key, Object value) {
        Object param = request.getAttribute(TEMP_PARAM_KEY);
        if (Objects.isNull(param)) {
            param = WebUtil.getParamVoFromHttpServletRequest(JSONObject.class);
        }
        if (Objects.nonNull(param)) {
            JSONObject paramJson = JsonUtil.jsonStringToJsonObject(param);
            if (Objects.nonNull(paramJson)) {
                paramJson.put(key, value);
                markRequestAccessParam(request, paramJson);
            }
        }
    }

    @Override
    public void print(String httpMethodName, String path, Object param, String methodName, Long executionTime, Object responseBody, JSONObject headersJson, String accessIp, String accessDateStr, Boolean recordeAccessLog) {
        String print = Optional.ofNullable(PropUtil.getInstance().getConfig(SERVER_SERVLET_PRINT_ACCESS_LOG, PropUtil.DEFAULT_NAME_SPACE)).map(String::toLowerCase).orElse(null);
        String executionTimeStr = executionTime + StringPool.EMPTY;
        if (executionTimeStr.equals(ZERO)) {
            executionTimeStr = "less than 1";
        }
        AccessLogRecorder accessLogger = SpringUtil.getBean(AccessLogRecorder.class);
        if (recordeAccessLog && Objects.nonNull(accessLogger)) {
            AccessLog accessLog = AccessLog.builder()
                    .httpMethodName(httpMethodName)
                    .path(path)
                    .parameter(param)
                    .responseBody(responseBody)
                    .headers(headersJson)
                    .methodName(methodName)
                    .accessIp(accessIp)
                    .accessDateStr(accessDateStr)
                    .executionTimeStr(executionTimeStr)
                    .build();
            accessLogger.recorde(accessLog);
        }

        if (Objects.equals(print, "info")) {
            String servletAccessLog = "\n\n" +
                    "================================= 【servlet accessed info start】 =================================" +
                    "\n\n" +
                    "method         : " + httpMethodName + "\n" +
                    "path           : " + path + "\n" +
                    "parameter      : " + param + "\n" +
                    "method name    : " + methodName + "\n" +
                    "execution      : " + executionTimeStr + " ms" +
                    "\n\n" +
                    "================================= 【 servlet accessed info end 】 =================================" +
                    "\n\n";
            log.info(servletAccessLog);
        } else if (Objects.equals(print, "debug")) {
            String servletAccessLog = "\n\n" +
                    "================================= 【servlet accessed debug start】 =================================" +
                    "\n\n" +
                    "method         : " + httpMethodName + "\n" +
                    "path           : " + path + "\n" +
                    "parameter      : " + param + "\n" +
                    "response body  : " + JsonUtil.toJson(responseBody) + "\n" +
                    "headers        : " + headersJson + "\n" +
                    "method name    : " + methodName + "\n" +
                    "client ip      : " + accessIp + "\n" +
                    "access time    : " + accessDateStr + "\n" +
                    "execution      : " + executionTimeStr + " ms" +
                    "\n\n" +
                    "================================= 【 servlet accessed debug end 】 =================================" +
                    "\n\n";
            log.info(servletAccessLog);
        }
    }

    @Override
    public void printErr(String httpMethodName, String path, Object param, String methodName) {
        String print = Optional.ofNullable(PropUtil.getInstance().getConfig(SERVER_SERVLET_PRINT_ACCESS_LOG, PropUtil.DEFAULT_NAME_SPACE)).map(String::toLowerCase).orElse(null);
        String servletAccessLog = "\n\n" +
                "================================= 【servlet accessed error start】 =================================" +
                "\n\n" +
                "method         : " + httpMethodName + "\n" +
                "path           : " + path + "\n" +
                "parameter      : " + param + "\n" +
                "servlet        : " + methodName +
                "\n\n" +
                "================================= 【 servlet accessed error end 】 =================================" +
                "\n\n";
        if ("info".equalsIgnoreCase(print) || "debug".equalsIgnoreCase(print)) {
            log.error(servletAccessLog);
        }

    }


}
