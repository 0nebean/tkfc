package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.sdk.mapper.GatewayTenantUserBindMapper;
import com.tkfc.sdk.model.GatewayTenantUserBind;
import com.tkfc.sdk.pojo.vo.GatewayTenantUserBindVo;
import com.tkfc.sdk.service.GatewayTenantUserBindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 平台租户用户关联关系表 serviceImpl
 *
 * @author 0neBean
 * @since 2023-03-24 20:28:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayTenantUserBindServiceImpl extends BaseServiceImpl<GatewayTenantUserBind, GatewayTenantUserBindVo, GatewayTenantUserBindMapper> implements GatewayTenantUserBindService {

}
