package com.tkfc.sdk.actors;


import com.tkfc.boot.starter.mus.abstracts.AbstractActor;
import com.tkfc.boot.starter.mus.annotations.Actor;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.sdk.consts.GlobalConst;
import com.tkfc.sdk.pojo.actor.ModifyBizUserInfoMsg;
import com.tkfc.sdk.pojo.actor.enums.ModifyUserTypeEnum;
import com.tkfc.sdk.service.GatewayUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新用户的 open id
 */
@Slf4j
@RequiredArgsConstructor
@Actor(topic = GlobalConst.ActorTopic.TOPIC_BIZ_USER_MODIFY, group = GlobalConst.ActorTopicGroup.DEFAULT_GROUP)
public class ModifyBizUserInfoActor extends AbstractActor {

    private final GatewayUserService gatewayUserService;

    @Override
    public void notice(byte[] message) {
        ModifyBizUserInfoMsg msg = JsonUtil.toBean(message, ModifyBizUserInfoMsg.class);
        log.info("ModifyBizUserInfoActor got an new msg = {}", msg);
        ModifyUserTypeEnum modifyType = msg.getModifyType();
        switch (modifyType) {
            case RESET_PASSWORD:
                gatewayUserService.resetPassword(msg.getTicketId(), msg.getUserOpenId(), msg.getPassword());
                break;
            case ADD:
                gatewayUserService.addGatewayUser(msg.getTicketId(), msg.getUserId(), msg.getUsername(), msg.getPassword());
                break;
            case DELETE_USERS:
                gatewayUserService.deleteByIdsPhysically(msg.getSelectedIds(), msg.getTicketId());
            case UPDATE_USER_INFO:
                gatewayUserService.updateBizUserInfo(msg.getTicketId(), msg.getUserOpenId(), msg.getIsActive(), msg.getEmail(), msg.getMobileNumber(), msg.getRealName());
                break;
            default:
                break;
        }
    }
}
