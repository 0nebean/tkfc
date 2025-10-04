package com.tkfc.sdk.timer;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.RestUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.biz.GatewayNoticeBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 网关心跳发送任务
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 16:14:36
 */
@Slf4j
@Configuration
public class GatewayHeartbeatSender {


    private final static String REPORT_API_URL_default_PREFIX = "http://";
    private final static String REPORT_API_URL_HTTPS_PREFIX = "https://";
    private static final String DOC_REPORT_API_BASE = "/gatewayApp/gatewayAppInstNode/heartbeat";

    @Bean
    @SuppressWarnings("all")
    public InitializingBean sendHeartbeat(@Autowired ICacheService cacheService, @Autowired GatewayNoticeBiz noticeBiz) {
        String instAddr = System.getProperty("instAddr");
        String instVersion = System.getProperty("instVersion");
        String appKey = System.getProperty("appKey");
        if (StringUtil.isBlank(instAddr) || StringUtil.isBlank(instVersion) || StringUtil.isBlank(appKey)) {
            return () -> {
            };
        }
        Timer timer = new Timer();
        // 定义任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    JSONObject header = new JSONObject();
                    header.put("Content-Type", "application/json");
                    header.put("Accept", "application/json");
                    BaseResponse<Boolean> response = RestUtil.getInstance().doGetForRef(handleReportUrl(instAddr, appKey, instVersion), new JSONObject(), new JSONObject(), header, new ParameterizedTypeReference<BaseResponse<Boolean>>() {
                    });
                    log.info("sendHeartbeat result = {}", JsonUtil.toJson(response));
                } catch (Exception e) {
                    log.error("sendHeartbeat got an error = ", e);
                }
            }
        };
        return () -> timer.schedule(task, 0, 1000 * 30);
    }

    /**
     * 获取请求地址
     * @return 请求地址
     */
    private static String handleReportUrl(String instAddr, String appKey, String instVersion) {
        String configUrl = PropUtil.getInstance().getConfig("welus.report.doc.server.host");
        Assert.notBlank(configUrl, "report api url config can not be empty");
        if (!configUrl.startsWith("http")) {
            // 判断是否为IP地址，如果是IP则使用http，如果是域名则使用https
            if (StringUtil.isIpAddress(configUrl)) {
                configUrl = REPORT_API_URL_default_PREFIX + configUrl;
            } else {
                configUrl = REPORT_API_URL_HTTPS_PREFIX + configUrl;
            }
        }
        String result = configUrl + DOC_REPORT_API_BASE;

        return String.format("%s?appKey=%s&version=%s&instAddr=%s", result, appKey, instVersion, instAddr);
    }
}
