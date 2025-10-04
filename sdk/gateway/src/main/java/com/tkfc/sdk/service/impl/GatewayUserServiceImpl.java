package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.boot.starter.mybatis.extend.BaseSplitServiceImpl;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.toolkit.NumberUtil;
import com.tkfc.core.toolkit.SnowflakeIdUtil;
import com.tkfc.sdk.consts.GlobalConst;
import com.tkfc.sdk.mapper.GatewayUserMapper;
import com.tkfc.sdk.model.GatewayUser;
import com.tkfc.sdk.pojo.actor.ModifyBizUserInfoMsg;
import com.tkfc.sdk.pojo.actor.enums.ModifyUserTypeEnum;
import com.tkfc.sdk.pojo.vo.GatewayUserVo;
import com.tkfc.sdk.secruity.SdkSecPasswordEncoder;
import com.tkfc.sdk.service.GatewayUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 网关用户 serviceImpl
 *
 * @author 0neBean
 * @since 2024-06-03 18:11:07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayUserServiceImpl extends BaseSplitServiceImpl<GatewayUser, GatewayUserVo, GatewayUserMapper> implements GatewayUserService {

    @Resource(name = "mailbox")
    private IMailBox mailBox;
    private final SdkSecPasswordEncoder secPasswordEncoder;


    @Override
    public GatewayUserVo findUserByUsername(String username, String ticketId) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(GatewayUser::getUsername, username).or(s -> s.eq(GatewayUser::getEmail, username));
        List<GatewayUser> gatewayUsers = find(sql, ticketId);
        return Optional.ofNullable(gatewayUsers).orElse(new ArrayList<>()).stream().findFirst().map(v -> v.toVo(GatewayUserVo.class)).orElse(null);
    }

    @Override
    public GatewayUserVo createUser(String username, String ticketId) {
        GatewayUser user = GatewayUser.builder().build();
        boolean isMobile = NumberUtil.isNum(username);
        if (isMobile) {
            user.setMobileNumber(username);
        } else {
            user.setEmail(username);
        }
        user.setId(SnowflakeIdUtil.generateId());
        user.setUsername(username);
        save(user, ticketId);
        return user.toVo(GatewayUserVo.class);
    }

    @Override
    public GatewayUserVo findUserByMobile(String mobile, String ticketId) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(GatewayUser::getMobileNumber, mobile);
        sql.eq(GatewayUser::getIsActive, YesOrNoEnum.YES.getValue());
        return Optional.ofNullable(findOne(sql, ticketId)).map(m -> m.toVo(GatewayUserVo.class)).orElse(null);
    }

    @Override
    public Boolean matchPassword(String rawPassword, String password) {
        return secPasswordEncoder.matches(rawPassword, password);
    }

    @Override
    public void addGatewayUser(String ticketId, Long userOpenId, String username, String password) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(BaseModel::getId, userOpenId);
        boolean userNotExist = count(sql, ticketId) <= 0;
        if (!userNotExist) {
            return;
        }
        GatewayUser user = GatewayUser.builder().username(username).isActive(Boolean.TRUE).password(secPasswordEncoder.encode(password)).build();
        save(user, ticketId);
        mailBox.send(GlobalConst.ActorTopic.TOPIC_BIZ_USER_MODIFY_CALLBACK, ModifyBizUserInfoMsg.builder().modifyType(ModifyUserTypeEnum.UPDATE_OPEN_ID).userOpenId(user.getId().toString()).userId(userOpenId).build());
    }

    @Override
    public void resetPassword(String ticketId, String userOpenId, String password) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(BaseModel::getId, userOpenId);
        boolean userNotExist = count(sql, ticketId) <= 0;
        if (userNotExist) {
            return;
        }
        GatewayUser user = GatewayUser.builder().password(secPasswordEncoder.encode(password)).build();
        update(user, sql, ticketId);
    }

    @Override
    public void updateBizUserInfo(String ticketId, String userOpenId, Boolean isActive, String email, String mobileNumber, String realName) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(BaseModel::getId, userOpenId);
        boolean userNotExist = count(sql, ticketId) <= 0;
        if (userNotExist) {
            return;
        }
        GatewayUser user = GatewayUser.builder().isActive(isActive).email(email).mobileNumber(mobileNumber).realName(realName).build();
        update(user, sql, ticketId);
    }

}
