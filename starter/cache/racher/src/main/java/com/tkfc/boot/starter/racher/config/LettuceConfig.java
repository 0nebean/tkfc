package com.tkfc.boot.starter.racher.config;

import com.tkfc.boot.starter.racher.dto.RedisConnectConfigProps;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 链接配置
 *
 * @author 0neBean
 * @since 2022-09-06 23:49:14
 */
@Slf4j
@Configuration
public class LettuceConfig {

    private static final String HOST_NAME_KEY = "spring.redis.hostName";
    private static final String PORT_KEY = "spring.redis.port";
    private static final String PASSWORD_KEY = "spring.redis.password";
    private static final String DB_SELECT_KEY = "spring.redis.database";
    private static final String USE_MODEL_KEY = "spring.redis.useModel";
    private static final String TIME_OUT_KEY = "spring.redis.timeout";


    @Bean(name = "redisConnectConfigProps")
    public RedisConnectConfigProps redisConnectConfigProps() {
        String useModel = PropUtil.getInstance().getConfig(USE_MODEL_KEY);
        String hostName = PropUtil.getInstance().getConfig(HOST_NAME_KEY);
        String password = PropUtil.getInstance().getConfig(PASSWORD_KEY);
        int database = ParseUtil.toInt(PropUtil.getInstance().getConfig(DB_SELECT_KEY));
        int port = ParseUtil.toInt(PropUtil.getInstance().getConfig(PORT_KEY));
        int timeout = ParseUtil.toInt(PropUtil.getInstance().getConfig(TIME_OUT_KEY));
        String address = String.format("redis://%s:%s", hostName, port);
        RedisConnectConfigProps configProps = RedisConnectConfigProps.builder().hostName(hostName).database(database).port(port).useModel(useModel).setTimeout(timeout).address(address).build();
        if (StringUtil.isNotBlank(password)) {
            configProps.setPassword(password);
        }
        return configProps;

    }

    @Bean(name = "lettuceConnectionFactory")
    public LettuceConnectionFactory initLettuceConnectionFactory(@Qualifier("redisConnectConfigProps") RedisConnectConfigProps redisConnectConfigProps) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConnectConfigProps.getHostName());
        redisStandaloneConfiguration.setPort(redisConnectConfigProps.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisConnectConfigProps.getPassword()));
        redisStandaloneConfiguration.setDatabase(redisConnectConfigProps.getDatabase());
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        log.info("LettuceConnectionFactory bean init success , redis host = {} , redis port ={}", lettuceConnectionFactory.getHostName(), lettuceConnectionFactory.getPort());
        return lettuceConnectionFactory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
        return new StringRedisTemplate(lettuceConnectionFactory);
    }
}
