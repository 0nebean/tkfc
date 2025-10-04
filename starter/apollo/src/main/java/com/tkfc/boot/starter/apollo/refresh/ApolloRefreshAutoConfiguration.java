package com.tkfc.boot.starter.apollo.refresh;

import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.tkfc.core.toolkit.PropUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * apollo 自动刷新配置 自动刷新日志级别
 * 
 * @author 0neBean
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApolloRefreshAutoConfiguration {

    /**
     * 监听关键字，当配置中心的依次开头的配置发生变化时，日志级别刷新
     */
    private static final String LOGGER_TAG = "logging.level.root";

    /**
     * 可以指定具体的namespace，未指定时使用的是 application这个namespace
     */
    @ApolloConfig
    private Config config;

    final private LoggingSystem loggingSystem;

    /**
     * 这里指定Apollo的namespace，非常重要，如果不指定，默认只使用application
     * @param changeEvent 事件
     */
    @ApolloConfigChangeListener
    public void onChange(ConfigChangeEvent changeEvent) {
        refreshLoggingLevels();
    }

    @PostConstruct
    private void refreshLoggingLevels() {
        Set<String> changedKeys = config.getPropertyNames();
        changedKeys.forEach(key -> {
            if (Objects.equals(key, LOGGER_TAG)) {
                String strLevel = PropUtil.getInstance().getConfig(key, PropUtil.DEFAULT_NAME_SPACE);
                LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());
                // 重置日志级别，马上生效
                loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), level);
                log.info("apollo refresh logger level to = {}", level);
            }
        });
    }

}