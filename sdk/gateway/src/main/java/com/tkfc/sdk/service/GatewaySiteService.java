package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseService;
import com.tkfc.sdk.model.GatewaySite;
import com.tkfc.sdk.pojo.vo.GatewaySiteVo;


/**
 * 站点配置 service
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:54
 */
public interface GatewaySiteService extends BaseService<GatewaySite, GatewaySiteVo> {

    /**
     * 同步站点信息到缓存
     *
     * @return bool
     */
    Boolean sync();

}
