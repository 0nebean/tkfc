package com.tkfc.boot.starter.musser.event;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import com.tkfc.boot.starter.mus.message.PayLoad;
import com.tkfc.boot.starter.musser.config.MusserConfig;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 事件投递 参考 erlang 的mailbox
 *
 * @author 0neBean
 * @since 2022-04-26 15:34:15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MusserMailBox implements IMailBox {


    private boolean READY = Boolean.FALSE;
    private boolean MAIL_BOX_STARTED = Boolean.FALSE;
    private final static Set<String> MAIL_TAGS = new CopyOnWriteArraySet<>();
    private final static Queue<PayLoad> COMMIT_LIST = new ConcurrentLinkedDeque<>();
    private final static Map<String, Disruptor<PayLoad>> DATA_MAP = new HashMap<>();

    /**
     * 消息入栈
     *
     * @param topic actor tag
     * @author 0neBean
     * @since 2022/4/26 22:25
     */
    public void send(String topic, Object message) {
        if (READY) {
            sendToDisruptor(topic, message);
        } else {
            sendToCommitQueue(topic, message);
        }
    }

    /**
     * 获取 disruptor
     *
     * @param topic topic
     * @return disruptor
     */
    public  Disruptor<PayLoad> getDisruptorByTopic(String topic) {
        Disruptor<PayLoad> disruptor = DATA_MAP.get(topic);
        if (Objects.isNull(disruptor)) {
            disruptor = buildDisruptor(topic);
            DATA_MAP.put(topic, disruptor);
        }
        return disruptor;
    }

    /**
     * 消息出栈
     *
     * @param topic 消息标题
     * @author 0neBean
     * @since 2022/4/26 22:25
     */
    public void pop(String topic) {
        MAIL_TAGS.remove(topic);
    }

    /**
     * 启动邮箱
     *
     * @author 0neBean
     * @since 2022/5/1 12:36
     */
    public void startUpMailBox() {
        Assert.notTrue(MAIL_BOX_STARTED, "mail box already started , it only can started once!");
        MAIL_BOX_STARTED = Boolean.TRUE;
        DATA_MAP.forEach((k,v)-> v.start());
        READY = Boolean.TRUE;
        handleCommitList();
        log.info("actor mail box startUp !");
    }

    /**
     * 处理为提交消息
     *
     * @author 0neBean
     * @since 2022/5/1 12:35
     */
    private void handleCommitList() {
        while (!COMMIT_LIST.isEmpty()) {
            PayLoad poll = COMMIT_LIST.poll();
            send(poll.getTopic(), poll.getMessage());
        }
    }

    /**
     * 销毁邮箱 自动关闭环形队列
     *
     * @author 0neBean
     * @since 2022/4/28 0:33
     */
    @PreDestroy
    private void shutdownMailBox() {
        DATA_MAP.forEach((k,v)-> v.shutdown());
        log.info("actor mail box shutdown !");
    }

    /**
     * 发送到队列中
     *
     * @param topic   消息标题
     * @param message 消息内容
     * @author 0neBean
     * @since 2022/5/1 12:44
     */
    private void sendToCommitQueue(String topic, Object message) {
        COMMIT_LIST.offer(buildPayLoad(topic, message));
    }

    /**
     * 发送消息到分发器
     *
     * @param topic   消息标题
     * @param message 消息内容
     * @author 0neBean
     * @since 2022/5/1 12:45
     */
    private void sendToDisruptor(String topic, Object message) {
        log.info("mail box send tag = {} ", topic);
        boolean notExistInMailBox = isNotExistInMailBox(topic);
        if (notExistInMailBox) {
            disruptorMessage(topic, message);
        } else {
            log.info("repeat message in mail box , tag = {}", topic);
        }
    }

    private final static String RING_BUFFER_KEY = "actor.mail.box.ring.buffer.%s";


    /**
     * 消息工厂类
     *
     * @author 0neBean
     * @since 2022-04-26 20:21:27
     */
    private static class MessageEventFactory implements EventFactory<PayLoad> {
        @Override
        public PayLoad newInstance() {
            return new PayLoad();
        }
    }


    public  Disruptor<PayLoad> buildDisruptor(String topic) {
        //环形队列的长度
        String ringBuffer = PropUtil.getInstance().getConfigWithDefaultValue(String.format(RING_BUFFER_KEY, topic), "128");
        //事件工厂
        EventFactory<PayLoad> factory = new MessageEventFactory();
        //创建disruptor对象
        return new Disruptor<>(factory, ParseUtil.toInt(ringBuffer), AsyncUtil.getThreadFactory());
    }


    /**
     * 投递消息
     *
     * @param topic   消息标题
     * @param message 消息内容
     * @author 0neBean
     * @since 2022/4/26 22:26
     */
    private void disruptorMessage(String topic, Object message) {
        Disruptor<PayLoad> disruptor = getDisruptorByTopic(topic);
        RingBuffer<PayLoad> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        PayLoad msg = ringBuffer.get(sequence);
        msg.setTopic(topic);
        msg.setMessage(buildMessageByte(message));
        msg.setMailBox(this);
        ringBuffer.publish(sequence);
    }

    /**
     * 消息是否已经存在邮箱中
     *
     * @param topic 消息tag
     * @return boolean
     * @author 0neBean
     * @since 2022/4/26 22:26
     */
    private static boolean isNotExistInMailBox(String topic) {
        if (MAIL_TAGS.contains(topic)) {
            return Boolean.FALSE;
        } else {
            MAIL_TAGS.add(topic);
            return Boolean.TRUE;
        }
    }


}
