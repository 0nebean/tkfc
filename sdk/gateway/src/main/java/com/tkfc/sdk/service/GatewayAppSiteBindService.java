package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewayAppSiteBind;
import com.tkfc.sdk.pojo.vo.GatewayAppSiteBindVo;
import com.tkfc.sdk.pojo.vo.GatewaySiteVo;

import java.util.List;


/**
 * 应用站点绑定表 service
 *
 * @author 0neBean
 * @since 2023-11-03 13:13:49
 */
public interface GatewayAppSiteBindService extends BaseService<GatewayAppSiteBind, GatewayAppSiteBindVo> {

    /**
     * 查找所有绑定的站点信息
     *
     * @return 站点信息列表
     */
    List<GatewaySiteVo> findAllBindSite();


}
