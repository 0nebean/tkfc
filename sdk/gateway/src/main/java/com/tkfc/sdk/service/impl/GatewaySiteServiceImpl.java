package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.sdk.mapper.GatewaySiteMapper;
import com.tkfc.sdk.model.GatewaySite;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainVo;
import com.tkfc.sdk.pojo.vo.GatewaySiteVo;
import com.tkfc.sdk.service.GatewayAppSiteBindService;
import com.tkfc.sdk.service.GatewaySiteDomainBindService;
import com.tkfc.sdk.service.GatewaySiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站点配置 serviceImpl
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:54
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewaySiteServiceImpl extends BaseServiceImpl<GatewaySite, GatewaySiteVo, GatewaySiteMapper> implements GatewaySiteService {

    private final ICacheService cacheService;
    private final GatewayAppSiteBindService appSiteBindService;
    private final GatewaySiteDomainBindService siteDomainBindService;
    private final static String HOST_MAP_SITE_IDS_KEY = "gateway:siteInfo:hostMapSiteIds";

    @Override
    public Boolean sync() {
        List<GatewaySiteVo> allBindSite = appSiteBindService.findAllBindSite();
        List<Long> siteIds = allBindSite.stream().map(BaseVo::getId).collect(Collectors.toList());
        List<GatewaySiteDomainVo> domainVos = siteDomainBindService.findBindDomainInfoBySiteIds(siteIds);
        Map<String, Object> domainMapSiteId = new HashMap<>();
        if (CollectionUtil.isEmpty(domainVos)) {
            return Boolean.TRUE;
        }
        domainVos.forEach(d -> domainMapSiteId.put(d.getHostName(), d.getSiteKey()));
        cacheService.hSetAll(HOST_MAP_SITE_IDS_KEY, domainMapSiteId);
        return Boolean.TRUE;
    }
}
