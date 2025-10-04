package com.tkfc.boot.starter.russer.event;

import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import com.tkfc.core.throwable.RunTimException;
import com.tkfc.core.toolkit.JsonUtil;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 事件投递 参考 erlang 的mailbox
 *
 * @author 0neBean
 * @since 2022-04-26 15:34:15
 */
@Slf4j
@RequiredArgsConstructor
public class RusserMailBox implements IMailBox {

    @Getter
    private final StatefulRedisPubSubConnection<String, String> connection;

    @Override
    public void send(String topic, Object message) {
        RedisPubSubAsyncCommands<String, String> asyncCommands = connection.async();
        RedisFuture<Long> publish = asyncCommands.publish(topic,JsonUtil.toJson(buildPayLoad(topic,message)));
        try {
            publish.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RunTimException(e);
        }
    }



}
