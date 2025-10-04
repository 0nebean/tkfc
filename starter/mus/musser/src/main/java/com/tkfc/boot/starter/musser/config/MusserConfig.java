package com.tkfc.boot.starter.musser.config;

import com.lmax.disruptor.dsl.Disruptor;
import com.tkfc.boot.starter.mus.abstracts.AbstractActor;
import com.tkfc.boot.starter.mus.annotations.Actor;
import com.tkfc.boot.starter.mus.annotations.EnableMus;
import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import com.tkfc.boot.starter.mus.message.PayLoad;
import com.tkfc.boot.starter.musser.event.MusserMailBox;
import com.tkfc.boot.starter.musser.event.MusserMessageEvent;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.core.toolkit.SpringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

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
public class MusserConfig {


    @Bean(name = "mailBox")
    public IMailBox mailBox() {
        String errorMessage = "Musser cannot be depended with Russer at the same time ,you probably depended Racher contains Russer";
        Assert.notTrue(Objects.equals(EnvUtil.getEnvProps(IMailBox.I_MAIL_BOX_IMPL_KEY), IMailBox.I_MAIL_BOX_IMPL_RUSSER), errorMessage);
        EnvUtil.setEnvProps(IMailBox.I_MAIL_BOX_IMPL_KEY, IMailBox.I_MAIL_BOX_IMPL_MUSSER);
        MusserMailBox mailBox = new MusserMailBox();
        SpringUtil.pushReadyTask(() -> {
            bindActorConsumer(mailBox);
            mailBox.startUpMailBox();
        });
        return mailBox;
    }

    /**
     * 绑定 actor 和 mail box 的消费关系
     *
     * @param mailBox 邮箱
     * @author 0neBean
     * @since 2022/4/27 0:02
     */
    @SneakyThrows
    public void bindActorConsumer(MusserMailBox mailBox) {
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
        List<Resource> resources = new ArrayList<>();
        for (String actionPath : actorPaths) {
            Resource[] temp = resolver.getResources(EnvUtil.coverPackageNameToClassPath(actionPath));
            Collections.addAll(resources, temp);
        }
        //遍历包的下的路径 获取有 Actor注解的实例 作为消费者
        for (Resource resource : resources) {
            String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Actor.class)) {
                AbstractActor actor = (AbstractActor) SpringUtil.getBean(className);
                Assert.notNull(actor, String.format("actor %s, can not be null ,please check actor annotation", className));
                String topic = clazz.getAnnotation(Actor.class).topic();
                //消息事件处理器
                MusserMessageEvent messageEvent = new MusserMessageEvent(actor);
                Disruptor<PayLoad> disruptor = mailBox.getDisruptorByTopic(topic);
                disruptor.handleEventsWith(messageEvent);
            }
        }
        log.info("actor start bind mail box consumer done!");
    }

}
