package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.sdk.mapper.GatewayAppSiteBindMapper;
import com.tkfc.sdk.model.GatewayAppSiteBind;
import com.tkfc.sdk.pojo.vo.GatewayAppSiteBindVo;
import com.tkfc.sdk.pojo.vo.GatewaySiteVo;
import com.tkfc.sdk.service.GatewayAppSiteBindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用站点绑定表 serviceImpl
 *
 * @author 0neBean
 * @since 2023-11-03 13:13:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayAppSiteBindServiceImpl extends BaseServiceImpl<GatewayAppSiteBind, GatewayAppSiteBindVo, GatewayAppSiteBindMapper> implements GatewayAppSiteBindService {

    @Override
    public List<GatewaySiteVo> findAllBindSite() {
        return baseMapper.findAllBindSite();
    }
}
