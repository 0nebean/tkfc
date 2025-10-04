package com.tkfc.boot.starter.russer.event;

import com.tkfc.boot.starter.mus.annotations.Actor;
import com.tkfc.boot.starter.mus.annotations.ActorSync;
import com.tkfc.boot.starter.mus.interfaces.IActor;
import com.tkfc.boot.starter.mus.message.PayLoad;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息事件处理器
 *
 * @author 0neBean
 * @since 2022-08-29 08:15:16
 */
@Slf4j

public class RusserMessageEvent implements RedisPubSubListener<String, String> {

    public RusserMessageEvent(ICacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * actor map
     */
    private final Map<String, IActor> ACTOR_MAP = new ConcurrentHashMap<>();

    private final ICacheService cacheService;


    /**
     * 收到消息处理逻辑
     *
     * @param payLoad 消息
     */
    private void onEvent(PayLoad payLoad) {
        String payLoadTag = payLoad.getTopic();
        String messageTag = payLoad.getMessageTag();
        IActor actor = getActor(payLoadTag);
        String actorInstId = actor.getActorInstId();
        Actor actorAnnotation = actor.getClass().getAnnotation(Actor.class);
        String topic;
        String group;
        if (Objects.isNull(actorAnnotation)) {
            ActorSync actorSync = actor.getClass().getAnnotation(ActorSync.class);
            topic = actorSync.topic();
            group = actorSync.group();
        } else {
            topic = actorAnnotation.topic();
            group = actorAnnotation.group();
        }
        String consumerFlagKey = String.format("RUSSER:MESSAGE-CONSUMER-GROUP-CHECK:%s:%s", group, messageTag);
        String checkLockKey = String.format("RUSSER:MESSAGE-CONSUMER-GROUP-CHECK-LOCK:%s:%s", group, messageTag);
        //如果消息topic不匹配直接返回
        if (!Objects.equals(payLoadTag, topic)) {
            log.debug("RusserMessageEvent onEvent tag not equal to current actor  topic = {} ,will not notice", topic);
            return;
        }
        ILock lock = cacheService.getLock(checkLockKey);
        try {
            //如果actor没有指定分组信息直接广播消息
            if (StringUtil.isNotBlank(group)) {
                lock.lock();
                //如果actor指定了分组信息，并且上次消费是本节点，怎让出消费
                String consumerFlag = cacheService.getString(consumerFlagKey);
                if (StringUtil.isNotBlank(consumerFlag)) {
                    log.debug("RusserMessageEvent onEvent continuous consumer message, group already consumer, topic = {} ,will not notice", topic);
                    return;
                }
            }
            actor.notice(payLoad.getMessage());
            //消费逻辑 写入消费记录到缓存
            cacheService.set(consumerFlagKey, StringPool.HASH, 1000L * 3);
            log.debug("notice actor , payload topic = {}", topic);
        } finally {
            if (lock.isLocked()) {
                lock.unLock();
            }
        }
    }

    @Override
    public void message(String topic, String message) {
        onEvent(JsonUtil.toBean(message, PayLoad.class));
    }

    /**
     * 设置actor
     *
     * @param topic 消息标题
     * @param actor actor
     */
    public void setActor(String topic, IActor actor) {
        this.ACTOR_MAP.put(topic, actor);
    }

    /**
     * 获取actor
     *
     * @param topic 消息标题
     * @return IActor
     */
    public IActor getActor(String topic) {
        return this.ACTOR_MAP.get(topic);
    }

    @Override
    public void message(String pattern, String topic, String message) {
        log.info("russer receive message [{}] at topic {}", topic, message);
    }

    @Override
    public void subscribed(String topic, long count) {
        log.info("russer subscribed topic {}", topic);
    }

    @Override
    public void psubscribed(String pattern, long count) {
    }

    @Override
    public void unsubscribed(String topic, long count) {
        log.info("russer un subscribed topic {}", topic);
    }

    @Override
    public void punsubscribed(String pattern, long count) {
    }
}
