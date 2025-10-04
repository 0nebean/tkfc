package com.tkfc.boot.starter.cacher.config;

import com.tkfc.boot.starter.cacher.service.CacherServiceImpl;
import com.tkfc.boot.starter.cacher.task.KeyCleaner;
import com.tkfc.cache.base.interfaces.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Timer;

/**
 * 缓存配置类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 19:31
 */
@Slf4j
@Configuration
public class CacherConfig {

    private final static int CLEAN_EXPIRE_KEY_INTERVAL = 1000;

    @Bean(name = "iCacheService")
    public ICacheService iCacheService() {
        log.info("init bean ICacheService by cacher implements");
        return new CacherServiceImpl();
    }


    @Bean
    public InitializingBean initializingClientHB() {
        return () -> {
            Timer timer = new Timer("starting run Cacher KeyCleaner ...");
            KeyCleaner task = new KeyCleaner();
            Date date = new Date(System.currentTimeMillis());
            //每分钟执行过期键清理任务
            timer.schedule(task, date, CLEAN_EXPIRE_KEY_INTERVAL);
        };
    }
}
