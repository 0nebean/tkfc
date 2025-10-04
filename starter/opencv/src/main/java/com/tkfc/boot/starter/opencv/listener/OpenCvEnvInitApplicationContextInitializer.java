package com.tkfc.boot.starter.opencv.listener;

import com.tkfc.boot.starter.opencv.constants.OpenCvConstants;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * apollo 启动监听
 *
 * @author 0neBean
 * @since 2021-12-06 00:14:17
 */
@Slf4j
@Order(value = 5)
public class OpenCvEnvInitApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String opencvDll = System.getenv(OpenCvConstants.OPENCV_DLL);
        log.info("OpenCvEnvInitApplicationContextInitializer initializing! will load opencv from {}",opencvDll);
        // 载入dll（必须先加载）
        System.load(opencvDll);
    }
}