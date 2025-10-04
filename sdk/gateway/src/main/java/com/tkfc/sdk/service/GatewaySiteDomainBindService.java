package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewaySiteDomainBind;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainBindVo;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainVo;

import java.util.List;


/**
 * 站点绑定域名 service
 *
 * @author 0neBean
 * @since 2023-10-29 12:20:36
 */
public interface GatewaySiteDomainBindService extends BaseService<GatewaySiteDomainBind, GatewaySiteDomainBindVo> {

    /**
     * 查询域名绑定站点列表
     *
     * @param siteIds siteIds
     * @return 分页api信息
     */
    List<GatewaySiteDomainVo> findBindDomainInfoBySiteIds(List<Long> siteIds);
}
