package com.tkfc.boot.starter.apollo.listener;

import com.tkfc.boot.starter.apollo.init.ApolloPropConfigInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

/**
 * apollo 启动监听
 *
 * @author 0neBean
 * @since 2021-12-06 00:14:17
 */
@Slf4j
@Order(value = 3)
public class ApolloPropConfigApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("ApolloPropConfigApplicationContextInitializer initializing! will load config from apollo");
        ApolloPropConfigInitializer.init();
    }
}