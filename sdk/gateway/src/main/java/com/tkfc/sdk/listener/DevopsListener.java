package com.tkfc.sdk.listener;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import com.tkfc.sdk.biz.GatewayFileBiz;
import com.tkfc.sdk.consts.CommonApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.util.Objects;

@Slf4j
@Order(value = 7)
public class DevopsListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    private final static String GATEWAY_REPORT_HEALTH_INFO_FLAG = "gateway.report.health.flag";


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
    public void reportHealthInfo()  {
        String reportFlag = PropUtil.getInstance().getConfig(GATEWAY_REPORT_HEALTH_INFO_FLAG);
        Boolean reportFlagBool = ParseUtil.toBoolean(reportFlag);
        if (reportFlagBool) {
            GatewayFileBiz fileBiz = SpringUtil.getBean(GatewayFileBiz.class);
            String machineId = EnvUtil.getEnvProps("machineId");
            String port = EnvUtil.getEnvProps("port");
            CommonApi.getCurrentAppReportJson((reportHealthJsonArr, fileName, remotePath)->{
                Assert.notNull(reportHealthJsonArr, "un know app version");
                try {
                    for (int i = 0; i < reportHealthJsonArr.size(); i++) {
                        JSONObject jsonObject = reportHealthJsonArr.getJSONObject(i);
                        boolean samePort = StringUtil.isNotBlank(jsonObject.getString("port")) && Objects.equals(jsonObject.getString("port"), port);
                        boolean sameMachineId = StringUtil.isNotBlank(jsonObject.getString("machineId")) &&  Objects.equals(jsonObject.getString("machineId"), machineId);
                        if (samePort && sameMachineId) {
                            jsonObject.put("startup", "done");
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
        }
    }

}
