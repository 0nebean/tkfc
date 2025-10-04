package com.tkfc.boot.starter.mybatis.listener;

import com.tkfc.boot.starter.mybatis.init.MybatisDataSourceConfigInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(value = 2)
public class MybatisDataSourceConfigApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("MybatisDataSourceConfigInitializer initializing, will exclude spring data source");
        MybatisDataSourceConfigInitializer.init();
    }
}