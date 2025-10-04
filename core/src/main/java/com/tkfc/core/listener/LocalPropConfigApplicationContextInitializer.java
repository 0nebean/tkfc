package com.tkfc.core.listener;

import com.tkfc.core.toolkit.PropUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

/**
 * 初始化本地配置文件
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@Slf4j
@Order(value = 1)
public class LocalPropConfigApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("LocalPropConfigApplicationContextInitializer initializing! will load config from classpath");
        init();
    }

    /**
     * config
     */
    private final static String SPRING_CONFIG_NAME = "spring.config.name";

    /**
     * 初始化本地配置文件
     */
    public static void init() {
        String springConfigName = PropUtil.getInstance().getConfig(SPRING_CONFIG_NAME, PropUtil.PUBLIC_CONF_SYSTEM);
        System.getProperties().setProperty(SPRING_CONFIG_NAME, springConfigName);
        log.info("{} is setting {}", SPRING_CONFIG_NAME, springConfigName);
        log.info("LocalConfigInitializer init config form local file done !!!");
    }
}