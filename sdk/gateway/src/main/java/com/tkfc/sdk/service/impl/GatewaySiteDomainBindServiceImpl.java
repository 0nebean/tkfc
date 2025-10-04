package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.sdk.mapper.GatewaySiteDomainBindMapper;
import com.tkfc.sdk.model.GatewaySiteDomainBind;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainBindVo;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainVo;
import com.tkfc.sdk.service.GatewaySiteDomainBindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点绑定域名 serviceImpl
 *
 * @author 0neBean
 * @since 2023-10-29 12:20:36
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewaySiteDomainBindServiceImpl extends BaseServiceImpl<GatewaySiteDomainBind, GatewaySiteDomainBindVo, GatewaySiteDomainBindMapper> implements GatewaySiteDomainBindService {

    @Override
    public List<GatewaySiteDomainVo> findBindDomainInfoBySiteIds(List<Long> siteIds) {
        return baseMapper.findBindDomainInfoBySiteIds(siteIds);
    }

}
