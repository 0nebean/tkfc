package com.tkfc.boot.starter.musser.event;

import com.lmax.disruptor.EventHandler;
import com.tkfc.boot.starter.mus.annotations.Actor;
import com.tkfc.boot.starter.mus.interfaces.IActor;
import com.tkfc.boot.starter.mus.message.PayLoad;
import com.tkfc.core.throwable.base.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 消息事件处理器
 *
 * @author 0neBean
 * @since 2022-08-29 08:15:16
 */
@Slf4j
@RequiredArgsConstructor
public class MusserMessageEvent implements EventHandler<PayLoad> {

    /**
     * actor map
     */
    private final IActor actor;

    @Override
    public void onEvent(PayLoad payLoad, long l, boolean b) {
        onEvent(payLoad);
    }

    /**
     * 收到消息处理逻辑
     *
     * @param payLoad 消息
     */
    private void onEvent(PayLoad payLoad) {
        String payLoadTag = payLoad.getTopic();
        Actor actorAnnotation = getActor().getClass().getAnnotation(Actor.class);
        String topic = actorAnnotation.topic();
        if (Objects.equals(payLoadTag, topic)) {
            log.debug("notice actor , payload topic = {}", payLoadTag);
            IActor actor = getActor();
            Assert.notNull(actor);
            actor.notice(payLoad.getMessage());
            MusserMailBox mailBox = (MusserMailBox) payLoad.getMailBox();
            mailBox.pop(payLoadTag);
        } else {
            log.debug("MusserMessageEvent onEvent tag not equal to current actor  topic = {} ,will not notice", payLoad);
        }
    }

    private IActor getActor() {
        return actor;
    }
}
