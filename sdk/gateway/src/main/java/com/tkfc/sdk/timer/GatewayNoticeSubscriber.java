package com.tkfc.sdk.timer;

import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.sdk.biz.GatewaySyncAccessInfoBiz;
import com.tkfc.sdk.pojo.dto.GatewayNoticeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 网关发布信息订阅
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 16:14:36
 */
@Slf4j
@Configuration
public class GatewayNoticeSubscriber {

    @Bean
    @SuppressWarnings("all")
    public InitializingBean subGatewayMsg(@Autowired ICacheService cacheService, @Autowired GatewaySyncAccessInfoBiz noticeBiz) {
        Timer timer = new Timer();
        // 定义任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                GatewayNoticeMsg msg = cacheService.rpop("GATEWAY_NOTICE_MSG", GatewayNoticeMsg.class);
                while (Objects.nonNull(msg)) {
                    log.debug("GatewayNoticeSubscriber gateway notice msg = {}", msg);
                    try {
                        noticeBiz.updateCallTimesInfo(msg);
                    } catch (Exception e) {
                        log.error("GatewayNoticeSubscriber gateway got an error", e);
                    }
                    msg = cacheService.rpop("GATEWAY_NOTICE_MSG", GatewayNoticeMsg.class);
                }
            }
        };
        return () -> timer.schedule(task, 1000 * 60, 1000 * 60);
    }
}
