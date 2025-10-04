package com.tkfc.sdk.listener;

import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(value = 7)
public class DevopsListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SuppressWarnings("all")
    private final static String REPORT_API_URL_DEFAULT_PREFIX = "http://";
    private final static String REPORT_API_URL_HTTPS_PREFIX = "https://";
    private final static String GATEWAY_REPORT_HEALTH_APP_KEY = "gateway.report.appKey";
    private final static String GATEWAY_REPORT_HEALTH_VERSION = "gateway.report.version";
    private final static String GATEWAY_REPORT_HEALTH_INFO_FLAG = "gateway.report.health.flag";
    private final static String GATEWAY_REPORT_HEALTH_SERVER_HOST = "gateway.report.health.host";
    private static final String REPORT_API_BASE = "/gatewayApp/gatewayAppInstNode/health?version=%s&appKey=%s";
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("devops tool initializing, report node health info");
        try {
            SpringUtil.pushReadyTask(this::reportHealthInfo);
        } catch (Exception e) {
            log.error("devops tool initializing got err, system will exit ...", e);
            System.exit(0);
        }
    }

    /**
     * 上报健康信息
     */
    public void reportHealthInfo() {
        String reportFlag = PropUtil.getInstance().getConfig(GATEWAY_REPORT_HEALTH_INFO_FLAG);
        Boolean reportFlagBool = ParseUtil.toBoolean(reportFlag);
        if (reportFlagBool) {
            String requestUrl = handleHealthReportUrl();
            log.info("reporting node health info to gateway management server, requestUrl = {}", requestUrl);
            BaseResponse<Boolean> response = RestUtil.getInstance().doGetForRef(requestUrl, null, new ParameterizedTypeReference<>() {
            });
            log.info("report resp = {}", JsonUtil.toJson(response));
            Assert.isTrue(response.getData(), "report node health info to gateway failure");
        }
    }

    /**
     * 获取报告 url
     *
     * @return url
     */
    private static String handleHealthReportUrl() {
        String appKey = PropUtil.getInstance().getConfig(GATEWAY_REPORT_HEALTH_APP_KEY);
        String version = PropUtil.getInstance().getConfig(GATEWAY_REPORT_HEALTH_VERSION);
        String configUrl = PropUtil.getInstance().getConfig(GATEWAY_REPORT_HEALTH_SERVER_HOST);
        Assert.notBlank(appKey, "report api appKey config can not be empty");
        Assert.notBlank(configUrl, "report api url config can not be empty");
        Assert.notBlank(version, "report version url config can not be empty");
        if (!configUrl.startsWith("http")) {
            // 判断是否为IP地址，如果是IP则使用http，如果是域名则使用https
            if (StringUtil.isIpAddress(configUrl)) {
                configUrl = REPORT_API_URL_DEFAULT_PREFIX + configUrl;
            } else {
                configUrl = REPORT_API_URL_HTTPS_PREFIX + configUrl;
            }
        }
        return String.format(configUrl + REPORT_API_BASE, version, appKey);
    }

}
