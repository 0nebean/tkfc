package com.tkfc.core.toolkit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作cookie 类
 *
 * @author 0neBean
 */
public class CookiesUtil {

    /**
     * 根据名字获取cookie
     *
     * @param name cookie名字
     * @return Cookie
     */
    public static Cookie getCookieByName(String name) {
        Map<String, Cookie> cookieMap = readCookieMap();
        return cookieMap.getOrDefault(name, null);
    }


    /**
     * 保存Cookies
     *
     * @param name  key
     * @param value val
     * @param time  失效时间
     */
    public static void setCookie(String name, String value, int time) throws UnsupportedEncodingException {
        HttpServletResponse response = WebUtil.getHttpServletResponse();
        // new一个Cookie对象,键值对为参数
        Cookie cookie = new Cookie(name, value);
        // tomcat下多应用共享
        cookie.setPath("/");
        // 如果cookie的值中含有中文时，需要对cookie进行编码，不然会产生乱码
        URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8));
        cookie.setMaxAge(time);
        // 将Cookie添加到Response中,使之生效
        // addCookie后，如果已经存在相同名字的cookie，则最新的覆盖旧的cookie
        response.addCookie(cookie);
    }

    /**
     * 将cookie封装到Map里面
     *
     * @return Map
     */
    private static Map<String, Cookie> readCookieMap() {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }


}
