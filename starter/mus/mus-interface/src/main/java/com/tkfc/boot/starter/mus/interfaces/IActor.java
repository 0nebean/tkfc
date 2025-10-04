package com.tkfc.boot.starter.mus.interfaces;

import com.tkfc.core.toolkit.SnowflakeIdUtil;

/**
 * 演员接口抽象
 *
 * @author 0neBean
 * @since 2022-08-26 19:51:28
 */
public interface IActor {

    /**
     * 接收
     *
     * @author 0neBean
     * @since 2022/4/26 22:28
     */
    void notice(byte[] message);

    /**
     * 获取actor 实例ID
     * @return actorInstId
     */
    default String  getActorInstId(){
        return actorInstId;
    }


    String actorInstId = SnowflakeIdUtil.generateId().toString();
}
