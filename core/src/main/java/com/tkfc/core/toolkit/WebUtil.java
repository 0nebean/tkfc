package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.OsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * web 工具类
 *
 * @author 0neBean
 */
@Slf4j
public class WebUtil {

    private final static String CALL_CLIENT_TYPE_KEY = "CALL_CLIENT_TYPE_KEY";
    private final static String CALL_CLIENT_TYPE_FEIGN = "FEIGN";


    /**
     * 获取cookie值
     *
     * @param key 键
     * @return val
     */
    public static String getCookieVal(String key) {
        return Optional.ofNullable(getCookie(key)).map(Cookie::getValue).orElse(null);
    }

    /**
     * 获取cookie值
     *
     * @param key 键
     * @return val
     */
    public static Cookie getCookie(String key) {
        HttpServletRequest request;
        try {
            request = getHttpServletRequest();
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), key)) {
                    return cookie;
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 判断请求平台
     *
     * @return 平台枚举
     */
    public static OsTypeEnum getPlatform() {
        HttpServletRequest request = getHttpServletRequest();
        String userAgent = request.getHeader("user-agent").toLowerCase();
        if (userAgent.contains("android")) {
            //安卓
            return OsTypeEnum.ANDROID;
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
            //苹果
            return OsTypeEnum.IOS;
        } else if (userAgent.contains("mac")) {
            //电脑
            return OsTypeEnum.MAC;
        } else if (userAgent.contains("windows")) {
            //电脑
            return OsTypeEnum.WINDOWS;
        } else if (userAgent.contains("linux")) {
            //电脑
            return OsTypeEnum.LINUX;
        } else {
            //电脑
            return OsTypeEnum.LINUX;
        }
    }

    /**
     * 是否移动端
     *
     * @return 是否是移动端
     */
    public static Boolean isMobile() {
        OsTypeEnum platform = getPlatform();
        switch (platform) {
            case IOS:
            case ANDROID:
                return true;
            default:
                return false;
        }
    }

    /**
     * 带上query string 重定向
     *
     * @param url 请求链接
     * @return 最终重定向的url
     * @throws IOException IO异常
     */
    public static String sendRedirectWithQueryString(String url) throws IOException {
        String result;
        if (StringUtil.isNotBlank(getHttpServletRequest().getQueryString())) {
            result = String.format("%s?%s", url, getHttpServletRequest().getQueryString());
        } else {
            result = url;
        }
        getHttpServletResponse().sendRedirect(result);
        return result;
    }

    /**
     * 从HttpServletRequest 中获取指定key参数值
     *
     * @param key key
     * @return val
     */
    public static String getParameterValue(String key) {
        if (StringUtil.isBlank(key)) {
            return null;
        }
        return WebUtil.getHttpServletRequest().getParameter(key);
    }

    /**
     * 从HttpServletRequest body中获取参数对象
     *
     * @return obj
     */
    public static Object getParamVoFromHttpServletRequestBody() {
        HttpServletRequest request = getHttpServletRequest();
        BufferedReader br;
        try {
            br = request.getReader();
            String str;
            StringBuilder wholeStr = new StringBuilder();
            while ((str = br.readLine()) != null) {
                wholeStr.append(str);
            }
            if (StringUtil.isNotEmpty(wholeStr.toString())) {
                String json = wholeStr.toString();
                if (json.startsWith(StringPool.LEFT_SQ_BRACKET)) {
                    return JsonUtil.toJsonArray(json);
                } else {
                    return JsonUtil.toJsonObject(json);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }


    /**
     * 从HttpServletRequest中获取参数对象
     *
     * @param clazz 类型
     * @param <T>   泛型类型
     * @return obj
     */
    public static <T> T getParamVoFromHttpServletRequest(Class<T> clazz) {
        HttpServletRequest request = getHttpServletRequest();
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 0) {
                String paramValue = paramValues[0];
                if (!paramValue.isEmpty()) {
                    jsonObject.put(paramName, paramValue);
                }
            }
        }
        return JSONObject.parseObject(jsonObject.toJSONString(), clazz);
    }

    /**
     * 获取所有request头部参数
     *
     * @return header
     */
    public static JSONObject getAllRequestHeader() {
        return getAllRequestHeader(getHttpServletRequest());
    }

    /**
     * 获取所有request头部参数
     *
     * @return header
     */
    public static JSONObject getAllRequestHeader(HttpServletRequest request) {
        JSONObject header = new JSONObject();
        //获取所有请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            //根据名称获取请求头的值
            String value = request.getHeader(name);
            header.put(name, value);
        }
        return header;
    }

    /**
     * 获取request头部参数
     *
     * @param key header key
     * @return header
     */
    public static String getRequestHeader(String key) {
        return getRequestHeader(getHttpServletRequest(), key);
    }


    /**
     * 获取request头部参数
     *
     * @param request 请求
     * @param key     key
     * @return header
     */
    public static String getRequestHeader(HttpServletRequest request, String key) {
        return request.getHeader(key);

    }

    /**
     * 是否是spring cloud调用
     *
     * @return bool
     */
    public static Boolean isFeignCalling() {
        HttpServletRequest request = null;
        try {
            request = getHttpServletRequest();
        } catch (Exception e) {
            //do nothing
        }
        String temp = Optional.ofNullable(request).map(r -> r.getHeader(CALL_CLIENT_TYPE_KEY)).orElse("");
        return temp.equals(CALL_CLIENT_TYPE_FEIGN);
    }

    /**
     * 获取IP地址
     *
     * @return ip地址
     */
    public static String getIpAddress() {
        HttpServletRequest request = getHttpServletRequest();
        String remoteAddress = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");
        String gatewayNativeIp = request.getHeader("gateway-native-ip");
        return doGetIpAddress(forwarded, realIp, remoteAddress, gatewayNativeIp);
    }

    /**
     * 获取真实IP
     *
     * @param forwarded       转发来源地址
     * @param realIp          代理前访问地址
     * @param remoteAddr      request对象获取的访问地址
     * @param gatewayNativeIp 网关获取的原生IP
     * @return 真实IP
     */
    private static String doGetIpAddress(String forwarded, String realIp, String remoteAddr, String gatewayNativeIp) {
        if (StringUtil.isNotBlank(gatewayNativeIp)) {
            return gatewayNativeIp;
        } else if (StringUtil.isNotBlank(forwarded)) {
            return forwarded;
        } else if (StringUtil.isNotBlank(realIp)) {
            return realIp;
        } else {
            return remoteAddr;
        }
    }


    /**
     * 获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(r -> (ServletRequestAttributes) r).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    /**
     * 获取HttpServletResponse
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getHttpServletResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(r -> (ServletRequestAttributes) r).map(ServletRequestAttributes::getResponse).orElse(null);
    }

    /**
     * 获取session属性值
     *
     * @param key key
     * @return val
     */
    public static Object getSessionAttribute(String key) {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
            return request.getSession().getAttribute(key);
        }
        return null;
    }

    /**
     * 设置session属性值
     *
     * @param key key
     * @param val val
     */
    public static void setSessionAttribute(String key, Object val) {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
            request.getSession().setAttribute(key, val);
        }
    }

    /**
     * 移除session属性值
     *
     * @param key key
     */
    public static void delSessionAttribute(String key) {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
            request.getSession().removeAttribute(key);
        }
    }

    /**
     * 获取session id
     *
     * @return sessionId
     */
    public static String getSessionId() {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
            return request.getSession().getId();
        }
        return null;
    }

    /**
     * 合并URL参数
     *
     * @param requestURL  带参数的url
     * @param queryString 查寻条件
     * @return 最终的结果
     */
    public static String mergeQueryString(String requestURL, String queryString) throws Exception {
        // Parse the URI and URL
        URL currentUrl = new URL(requestURL);
        // Extract query parameters from currentUrl and tagAttrUrl
        Map<String, String> param = new HashMap<>();
        addQueryParams(currentUrl.getQuery(), param);
        addQueryParams(queryString, param);
        URI uri = new URI(currentUrl.getProtocol(), currentUrl.getAuthority(), currentUrl.getPath(), buildQueryString(param), null);
        String result = uri.toString();
        param = null;
        currentUrl = null;
        uri = null;
        return result;
    }

    // Helper method to parse query parameters and put them in the map
    private static void addQueryParams(String query, Map<String, String> paramMap) {
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && !Objects.equals(keyValue[0], "null")) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    /**
     * 构建query string
     *
     * @param params query参数
     * @return query string
     */
    public static String buildQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();
        params.forEach((key, value) -> {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(key).append("=").append(value);
        });
        return queryString.toString();
    }

    /**
     * 构建query string
     *
     * @return query string
     */
    public static String buildQueryString() {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = getHttpServletRequest().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            params.put(key, getHttpServletRequest().getParameter(parameterNames.nextElement()));
        }
        return buildQueryString(params);
    }


}
