package com.tkfc.welus.listener;

import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.SpringUtil;
import com.tkfc.welus.verification.OnlyWelusAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(value = 6)
public class WelusReadyListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final static String WELUS_REPORT_DOC_INFO_FLAG = "welus.report.doc.info.flag";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        SpringUtil.pushReadyTask(() -> {
            try {
                //上报扫描到的文档信息
                Boolean docReportFlag = ParseUtil.toBoolean(PropUtil.getInstance().getConfig(WELUS_REPORT_DOC_INFO_FLAG));
                if (docReportFlag) {
                    log.info("welus initializing, verify only welus annotation");
                    OnlyWelusAnnotation.verify();
                }
            } catch (Exception e) {
                log.error("welus initializing got err ,system will exit ...", e);
                System.exit(0);
            }
        });
    }
}
