package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.boot.starter.mybatis.extend.BaseSplitServiceImpl;
import com.tkfc.boot.starter.mybatis.sql.build.SqlBuilder;
import com.tkfc.boot.starter.mybatis.sql.wrapper.SqlWrapper;
import com.tkfc.core.common.pojo.BasePageExpressionRequest;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.NumberUtil;
import com.tkfc.core.toolkit.SnowflakeIdUtil;
import com.tkfc.sdk.mapper.GatewayUserMapper;
import com.tkfc.sdk.model.GatewayTenantUserBind;
import com.tkfc.sdk.model.GatewayUser;
import com.tkfc.sdk.pojo.base.TransferBindDto;
import com.tkfc.sdk.pojo.vo.GatewayUserVo;
import com.tkfc.sdk.secruity.SdkSecPasswordEncoder;
import com.tkfc.sdk.service.GatewayTenantUserBindService;
import com.tkfc.sdk.service.GatewayUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final SdkSecPasswordEncoder secPasswordEncoder;
    private final GatewayTenantUserBindService tenantUserBindService;


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
    public Long addGatewayUser(String ticketId, Long userOpenId, String username, String realName, String password) {
        SqlWrapper<GatewayUser> sql = SqlBuilder.init();
        sql.eq(GatewayUser::getUsername, username);
        boolean userNotExist = count(sql, ticketId) <= 0;
        if (!userNotExist) {
            return findUserByUsername(username, ticketId).getId();
        }
        GatewayUser user = GatewayUser.builder().username(username).realName(realName).isActive(Boolean.TRUE).password(secPasswordEncoder.encode(password)).build();
        save(user, ticketId);
        return user.getId();
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

    @Override
    public Boolean bindUserTenant(TransferBindDto request, String ticketId) {
        List<GatewayTenantUserBind> binds = new ArrayList<>();
        Long userId = request.getMainDataId();
        request.getBindIds().forEach((tenantId -> {
            GatewayTenantUserBind bind = new GatewayTenantUserBind();
            bind.setTicketId(ticketId);
            bind.setTenantId(tenantId);
            bind.setUserId(userId);
            binds.add(bind);
        }));
        if (CollectionUtil.isNotEmpty(binds)) {
            tenantUserBindService.saveBatch(binds);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean unBindUserTenant(TransferBindDto request, String ticketId) {
        SqlWrapper<GatewayTenantUserBind> sql = SqlBuilder.init();
        sql.eq(GatewayTenantUserBind::getUserId, request.getMainDataId());
        sql.eq(GatewayTenantUserBind::getTicketId, ticketId);
        sql.in(GatewayTenantUserBind::getTenantId, request.getBindIds());
        List<Long> bindIds = tenantUserBindService.find(sql).stream().map(BaseModel::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bindIds)) {
            tenantUserBindService.deleteByIdsPhysically(bindIds);
        }
        return Boolean.TRUE;
    }

    @Override
    public BaseResponse<List<GatewayUser>> findGatewayUserPage(String ticketId, BasePageExpressionRequest request) {
        Boolean tableExists = baseMapper.tableExists(ticketId);
        if (!tableExists) {
            return BaseResponse.ok(new ArrayList<>(), request.getPagination());
        }
        SqlWrapper<GatewayUser> sql = SqlBuilder.<GatewayUser>init().expression(request);
        return BaseResponse.ok(find(sql, ticketId), sql.getPagination());
    }
}
