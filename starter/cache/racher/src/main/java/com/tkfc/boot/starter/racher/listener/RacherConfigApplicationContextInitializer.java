package com.tkfc.boot.starter.racher.listener;

import com.tkfc.boot.starter.racher.init.RacherConfigInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(value = 4)
public class RacherConfigApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("RacherConfigApplicationContextInitializer initializing, will exclude redis AutoConfiguration ");
        RacherConfigInitializer.init();
    }

}
