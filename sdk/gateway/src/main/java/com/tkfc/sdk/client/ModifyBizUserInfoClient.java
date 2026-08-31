package com.tkfc.sdk.client;

import com.tkfc.sdk.pojo.actor.ModifyBizUserInfoMsg;
import com.tkfc.sdk.service.GatewayUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 网管访问信息同步 API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyBizUserInfoClient {

    private final GatewayUserService gatewayUserService;

    /**
     * 修改业务用户信息。
     *
     * @param msg 包含要修改的用户信息和修改类型的消息对象
     */
    public ModifyBizUserInfoMsg modifyBizUserInfo(ModifyBizUserInfoMsg msg) {
        log.info("ModifyBizUserInfoActor got an new msg = {}", msg);
        switch (msg.getModifyType()) {
            case RESET_PASSWORD:
                gatewayUserService.resetPassword(msg.getTicketId(), msg.getUserOpenId(), msg.getPassword());
                break;
            case ADD:
                msg.setUserOpenId(gatewayUserService.addGatewayUser(msg.getTicketId(), msg.getUserId(), msg.getUsername(), msg.getRealName(), msg.getPassword()).toString());
                break;
            case DELETE_USERS:
                gatewayUserService.deleteByIdsPhysically(msg.getSelectedIds(), msg.getTicketId());
                break;
            case UPDATE_USER_INFO:
                gatewayUserService.updateBizUserInfo(msg.getTicketId(), msg.getUserOpenId(), msg.getIsActive(), msg.getEmail(), msg.getMobileNumber(), msg.getRealName());
                break;
            default:
                break;
        }
        return msg;
    }

}
