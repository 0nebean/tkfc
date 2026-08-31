package com.tkfc.sdk.timer;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import com.tkfc.sdk.biz.GatewayFileBiz;
import com.tkfc.sdk.biz.GatewaySyncAccessInfoBiz;
import com.tkfc.sdk.consts.CommonApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Objects;
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

    @Bean
    @SuppressWarnings("all")
    public InitializingBean sendHeartbeat(@Autowired ICacheService cacheService, @Autowired GatewaySyncAccessInfoBiz noticeBiz) {
        Timer timer = new Timer();
        // 定义任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    // 如果是 local 环境，跳过健康检查
                    if (Objects.equals(EnvUtil.getEnvProps("spring.profiles.active"), "local")) {
                        return;
                    }
                    GatewayFileBiz fileBiz = SpringUtil.getBean(GatewayFileBiz.class);
                    String machineId = EnvUtil.getEnvProps("machineId");
                    String port = EnvUtil.getEnvProps("port");
                    CommonApi.getCurrentAppReportJson((reportHealthJsonArr, fileName, remotePath) -> {
                        Assert.notNull(reportHealthJsonArr, "un know app version");
                        try {
                            for (int i = 0; i < reportHealthJsonArr.size(); i++) {
                                JSONObject jsonObject = reportHealthJsonArr.getJSONObject(i);
                                boolean samePort = StringUtil.isNotBlank(jsonObject.getString("port")) && Objects.equals(jsonObject.getString("port"), port);
                                boolean sameMachineId = StringUtil.isNotBlank(jsonObject.getString("machineId")) && Objects.equals(jsonObject.getString("machineId"), machineId);
                                if (samePort && sameMachineId) {
                                    jsonObject.put("heartBeat", DateUtil.getTimeStampString());
                                    String path = String.format("%s%s.json", GatewayFileBiz.getUploadTempPath(), fileName);
                                    IoUtil.writeFile(reportHealthJsonArr.toJSONString(), path);
                                    File uploadFile = new File(path);
                                    fileBiz.uploadFile(uploadFile, remotePath, fileName);
                                    IoUtil.deleteQuietly(uploadFile);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Assert.fail(e, "report node health info to gateway failure");
                        }
                    });
                } catch (Exception e) {
                    log.error("sendHeartbeat got an error = ", e);
                }
            }
        };
        return () -> timer.schedule(task, 1000 * 60 * 5, 1000 * 30);
    }

}
