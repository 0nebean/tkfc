package com.tkfc.boot.starter.racher.config;

import com.tkfc.boot.starter.racher.dto.RedisConnectConfigProps;
import com.tkfc.boot.starter.racher.enums.RedisUseModelEnum;
import com.tkfc.boot.starter.racher.service.RacherServiceImpl;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.toolkit.EnumsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * redis  配置类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/11 15:27
 */
@Slf4j
@Configuration
public class RacherConfig {

    @Bean("redissonClient")
    @SuppressWarnings("all")
    public RedissonClient redissonClient(@Qualifier("redisConnectConfigProps") RedisConnectConfigProps configProps) {
        Config config = new Config();
        RedisUseModelEnum useModelEnum = EnumsUtil.getByValue(RedisUseModelEnum.class, configProps.getUseModel());
        switch (useModelEnum) {
            case USE_SINGLE_SERVER:
                config.useSingleServer().setAddress(configProps.getAddress()).setPassword(configProps.getPassword()).setDatabase(configProps.getDatabase()).setTimeout(configProps.getSetTimeout());
                break;
            default:
                break;
        }
        return Redisson.create(config);
    }

    @Bean(name = "iCacheService")
    @SuppressWarnings("all")
    public ICacheService iCacheService(@Qualifier("redissonClient") RedissonClient redissonClient, @Qualifier("redisTemplate") RedisTemplate<String, String> stringRedisTemplate) {
        log.info("init bean ICacheService by racher implements");
        return new RacherServiceImpl(stringRedisTemplate, redissonClient);
    }
}
