package com.tkfc.boot.starter.mus.interfaces;

import com.tkfc.boot.starter.mus.message.PayLoad;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.SnowflakeIdUtil;
import com.tkfc.core.toolkit.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * 邮箱接口
 *
 * @author 0neBean
 * @since 2022-08-26 19:51:57
 */
public interface IMailBox {

    String I_MAIL_BOX_IMPL_SYNC_KEY = "I_MAIL_BOX_IMPL_SYNC_KEY";
    String I_MAIL_BOX_IMPL_SYNC_RUSSER = "I_MAIL_BOX_IMPL_SYNC_RUSSER";
    String I_MAIL_BOX_IMPL_KEY = "I_MAIL_BOX_IMPL_KEY";
    String I_MAIL_BOX_IMPL_RUSSER = "I_MAIL_BOX_IMPL_RUSSER";
    String I_MAIL_BOX_IMPL_MUSSER = "I_MAIL_BOX_IMPL_MUSSER";

    /**
     * 发送消息
     *
     * @param topic   消息标题
     * @param message 消息内容
     */
    void send(String topic, Object message);


    /**
     * 构建消息载荷
     *
     * @param topic   消息标题
     * @param message 消息内容
     * @return PayLoad
     */
    default PayLoad buildPayLoad(String topic, Object message) {
        PayLoad msg = new PayLoad();
        msg.setTopic(topic);
        msg.setMessage(buildMessageByte(message));
        msg.setMessageTag(SnowflakeIdUtil.generateId().toString());
        return msg;
    }

    /**
     * 构建消息 byte数组
     *
     * @param message 消息体
     * @return byte数组
     */
    default byte[] buildMessageByte(Object message) {
        String messageJson = StringUtil.isString(message) ? String.valueOf(message) : JsonUtil.toJson(message);
        return StringUtil.isBlank(messageJson) ? new byte[0] : messageJson.getBytes(StandardCharsets.UTF_8);
    }
}
