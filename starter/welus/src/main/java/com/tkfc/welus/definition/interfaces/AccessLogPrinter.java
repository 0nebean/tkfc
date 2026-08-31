package com.tkfc.welus.definition.interfaces;

import com.alibaba.fastjson2.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 访问日志打印
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/7 17:55
 */
public interface AccessLogPrinter {

    /**
     * 标记请求时间
     *
     * @param request 请求
     */
    void markRequestAccessTime(HttpServletRequest request);

    /**
     * 标记请求参数
     *
     * @param request 请求
     * @param param   参数
     */
    void markRequestAccessParam(HttpServletRequest request, Object param);

    /**
     * 向标记的请求参数对象里添加参数
     *
     * @param request 请求
     * @param key     key
     * @param value   val
     */
    void addMarkRequestAccessParam(HttpServletRequest request, String key, Object value);

    /**
     * 打印日志
     */
    void print(String httpMethodName, String path, Object param, String methodName, Long executionTime, Object body, JSONObject headersJson, String accessIp, String accessDateStr, Boolean recordeAccessLog);

    /**
     * 打印错误日志
     */
    void printErr(String httpMethodName, String path, Object param, String methodName);
}
