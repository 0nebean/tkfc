package com.tkfc.sdk.mapper;

import com.tkfc.boot.starter.mybatis.extend.BaseMapper;
import com.tkfc.sdk.model.GatewayAppSiteBind;
import com.tkfc.sdk.pojo.vo.GatewaySiteVo;

import java.util.List;

/**
 * 应用站点绑定表 Mapper
 *
 * @author 0neBean
 * @since 2023-11-03 13:13:49
 */

public interface GatewayAppSiteBindMapper extends BaseMapper<GatewayAppSiteBind> {

    /**
     * 查找所有绑定过的站点信息
     *
     * @return 所有绑定过的站点信息
     */
    List<GatewaySiteVo> findAllBindSite();

}
