package com.tkfc.sdk.consts;

/**
 * 全局常量
 *
 * @author 0neBean
 * @since 2023-02-28 13:55:24
 */
public interface GlobalConst {

    /**
     * actor topic
     */
    interface ActorTopic {
        String TOPIC_BIZ_USER_MODIFY = "TOPIC-BIZ-USER-MODIFY";
        String TOPIC_BIZ_USER_MODIFY_CALLBACK = "TOPIC-BIZ-USER-CALLBACK";
        String TOPIC_SYNC_GATEWAY_INFO = "TOPIC-SYNC-GATEWAY-INFO";
    }

    /**
     * actor topic
     */
    interface ActorTopicGroup {
        String DEFAULT_GROUP = "DEFAULT_GROUP";
    }

}
