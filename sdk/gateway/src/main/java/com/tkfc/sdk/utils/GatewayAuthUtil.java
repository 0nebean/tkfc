package com.tkfc.sdk.utils;

import com.tkfc.core.toolkit.WebUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 网关鉴权工具类
 *
 * @author 0neBean
 * @since 2023-03-24 19:30:43
 */
public class GatewayAuthUtil {


    private final static String GATEWAY_USER_OPEN_ID_KEY = "gateway-user-open-id";
    private final static String GATEWAY_ACCESS_TOKEN_KEY = "gateway-access-token";
    private final static String GATEWAY_DEVICE_TOKEN_KEY = "gateway-device-token";
    private final static String GATEWAY_SITE_KEY_KEY = "Gateway-site-key";
    private final static String GATEWAY_TICKET_ID_KEY = "gateway-ticket-id";
    private final static String GATEWAY_USERNAME_KEY = "gateway-username";
    private final static String GATEWAY_TENANT_NAME_KEY = "gateway-tenant-name";
    private final static String GATEWAY_USER_REAL_NAME_KEY = "gateway-user-real-name";
    private final static String GATEWAY_SITE_DOMAIN_KEY = "gateway-site-domain";
    private final static String GATEWAY_NATIVE_IP_KEY = "gateway-native-ip";
    private final static String GATEWAY_EMAIL_KEY = "gateway-email";
    private final static String GATEWAY_MOBILE_KEY = "gateway-mobile";
    private final static String GATEWAY_TENANT_ID_KEY = "gateway-tenant-id";
    private final static String GATEWAY_HTTP_REFERER = "GATEWAY-HTTP-REFERER";
    private final static String CF_IP_COUNTRY = "CF-IPCountry";

    /**
     * 获取cloudflare的IP国家
     *
     * @return IP 国家
     */
    public static String getCfIpCountry() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), CF_IP_COUNTRY);
    }

    /**
     * 获取开放平台 mobile
     *
     * @return ticketId
     */
    public static String getMobile() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_MOBILE_KEY);
    }


    /**
     * 获取开放平台 email
     *
     * @return ticketId
     */
    public static String getEmail() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_EMAIL_KEY);
    }

    /**
     * 获取开放平台 凭证Id
     *
     * @return ticketId
     */
    public static String getTicketId() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_TICKET_ID_KEY);
    }

    /**
     * 获取开放平台 站点标识
     *
     * @return ticketId
     */
    public static String getSiteKey() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_SITE_KEY_KEY);
    }

    /**
     * 获取开放平台 租户Id
     *
     * @return tenantId
     */
    public static String getTenantId() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_TENANT_ID_KEY);
    }

    /**
     * 获取开放平台 租户Id 以及主租户ID
     *
     * @return tenantId
     */
    public static List<String> getTenantIdWithMainTenant() {
        Set<String> tenantIds = new HashSet<>();
        tenantIds.add("1");
        try {
            tenantIds.add(getTenantId());
        } catch (Exception ignored) {
        }
        return new ArrayList<>(tenantIds);
    }

    /**
     * 获取开放平台 租户名称
     *
     * @return tenantId
     */
    public static String getTenantName() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_TENANT_NAME_KEY);
    }

    /**
     * 获取开放平台 oauth2 授权码
     *
     * @return accessToken
     */
    public static String getAccessToken() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_ACCESS_TOKEN_KEY);
    }

    /**
     * 获取开放平台 设备标识
     *
     * @return deviceToken
     */
    public static String getDeviceToken() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_DEVICE_TOKEN_KEY);
    }

    /**
     * 获取开放平台用户ID
     *
     * @return openId
     */
    public static String getUserOpenId() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_USER_OPEN_ID_KEY);
    }

    /**
     * 获取开放平台用户名
     *
     * @return username
     */
    public static String getUsername() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_USERNAME_KEY);
    }

    /**
     * 获取开放平台用户真实姓名
     *
     * @return username
     */
    public static String getUserRealName() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_USER_REAL_NAME_KEY);
    }

    /**
     * 获取开放平台用户国家
     *
     * @return username
     */
    public static String getSiteDomain() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_SITE_DOMAIN_KEY);
    }

    /**
     * 获取开放平台用户原生IP
     *
     * @return username
     */
    public static String getNativeIp() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_NATIVE_IP_KEY);
    }

    /**
     * 获取开放平台用户来源地址
     *
     * @return username
     */
    public static String getNativeReferer() {
        return WebUtil.getRequestHeader(WebUtil.getHttpServletRequest(), GATEWAY_HTTP_REFERER);
    }


}
