package com.tkfc.boot.starter.russer.config;

import com.tkfc.boot.starter.mus.abstracts.AbstractActor;
import com.tkfc.boot.starter.mus.annotations.ActorSync;
import com.tkfc.boot.starter.mus.annotations.EnableMus;
import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import com.tkfc.boot.starter.russer.event.RusserMailBox;
import com.tkfc.boot.starter.russer.event.RusserMessageEvent;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.*;

/**
 * actor 配置
 *
 * @author 0neBean
 * @since 2022-04-26 20:37:22
 */
@Slf4j
@Configuration
@ConditionalOnClass(value = EnableMus.class)
@ConditionalOnProperty(prefix = "sync.redis", name = "hostName")
public class RusserSyncConfig {

    @Value("${sync.redis.port}")
    private int port;
    @Value("${sync.redis.database}")
    private int database;
    @Value("${sync.redis.hostName}")
    private String hostName;
    @Value("${sync.redis.password}")
    private String password;

    @Bean(name = "syncClient")
    public RedisClient redisClient() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        if (StringUtil.isNotBlank(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        redisStandaloneConfiguration.setDatabase(database);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return (RedisClient) lettuceConnectionFactory.getNativeClient();
    }

    @Bean(name = "syncPubConnection")
    public StatefulRedisPubSubConnection<String, String> syncPubConnection(@Qualifier("syncClient") RedisClient redisClient) {
        return redisClient.connectPubSub();
    }

    @Bean(name = "syncSubConnection")
    public StatefulRedisPubSubConnection<String, String> syncSubConnection(@Qualifier("syncClient") RedisClient redisClient) {
        return redisClient.connectPubSub();
    }

    @Bean(name = "syncMessageEvent")
    public RusserMessageEvent syncMessageEvent(@Qualifier("syncSubConnection") StatefulRedisPubSubConnection<String, String> redisSubConnection, ICacheService cacheService) {
        RusserMessageEvent russerMessageEvent = new RusserMessageEvent(cacheService);
        redisSubConnection.addListener(russerMessageEvent);
        return russerMessageEvent;
    }

    @Bean(name = "syncMailbox")
    public IMailBox syncMailbox(@Qualifier("syncMessageEvent") RusserMessageEvent russerMessageEvent, @Qualifier("syncPubConnection") StatefulRedisPubSubConnection<String, String> redisPubConnection, @Qualifier("syncSubConnection") StatefulRedisPubSubConnection<String, String> redisSubConnection) {
        String errorMessage = "Russer cannot be referenced with Musser at the same time, you probably depended Racher contains Russer";
        Assert.notTrue(Objects.equals(EnvUtil.getEnvProps(IMailBox.I_MAIL_BOX_IMPL_SYNC_KEY), IMailBox.I_MAIL_BOX_IMPL_SYNC_RUSSER), errorMessage);
        EnvUtil.setEnvProps(IMailBox.I_MAIL_BOX_IMPL_SYNC_KEY, IMailBox.I_MAIL_BOX_IMPL_SYNC_RUSSER);
        RusserMailBox mailBox = new RusserMailBox(redisPubConnection);
        SpringUtil.pushReadyTask(() -> bindActorConsumer(redisSubConnection, russerMessageEvent));
        return mailBox;
    }

    /**
     * 绑定 actor 和 mail box 的消费关系
     *
     * @param redisSubConnection redis 链接
     */
    @SneakyThrows
    public void bindActorConsumer(StatefulRedisPubSubConnection<String, String> redisSubConnection, RusserMessageEvent russerMessageEvent) {
        log.info("actor start binding mail box consumer ...");
        //装在资源
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        //获取main方法上 EnableActor 注解的 package
        Class<?> springBootMainClass = ReflectionUtil.getSpringBootMainClass();
        EnableMus enableMus = Optional.ofNullable(springBootMainClass).map(c -> c.getAnnotation(EnableMus.class)).orElse(null);
        if (Objects.isNull(enableMus)) {
            return;
        }
        List<String> actorPaths = Optional.of(enableMus).map(EnableMus::actorPackages).map(Arrays::asList).orElse(Collections.emptyList());
        try {
            Assert.notEmpty(actorPaths, "@EnableMus @interface can not be null");
        } catch (Exception e) {
            log.error("init mailbox failure , e = ", e);
            EnvUtil.exit();
        }
        Assert.notEmpty(actorPaths, "@EnableMus @interface can not be null");
        List<Resource> resources = new ArrayList<>();
        for (String actionPath : actorPaths) {
            Resource[] temp = resolver.getResources(EnvUtil.coverPackageNameToClassPath(actionPath));
            Collections.addAll(resources, temp);
        }
        List<String> topicList = new ArrayList<>();
        Set<String> actorClassSet = new HashSet<>();
        //遍历包的下的路径 获取有 Actor注解的实例 作为消费者
        for (Resource resource : resources) {
            String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getEnclosingClassName();
            if (Objects.isNull(className)) {
                className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            }
            if (actorClassSet.contains(className)) {
                continue;
            }
            actorClassSet.add(className);
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(ActorSync.class)) {
                AbstractActor actor = (AbstractActor) SpringUtil.getBean(className);
                ActorSync actorAnnotation = clazz.getAnnotation(ActorSync.class);
                Assert.notNull(actor, "actor, can not be null , please check actor annotation");
                //消息事件处理器
                String topic = actorAnnotation.topic();
                topicList.add(topic);
                russerMessageEvent.setActor(topic, actor);
            }
        }
        RedisPubSubAsyncCommands<String, String> subAsyncCommands = redisSubConnection.async();
        String[] topics = CollectionUtil.listToStringArr(topicList);
        if (CollectionUtil.isNotEmpty(topics)) {
            subAsyncCommands.subscribe(topics);
        }
    }

}
