package com.tkfc.sdk.mapper;

import com.tkfc.boot.starter.mybatis.extend.BaseMapper;
import com.tkfc.sdk.model.GatewaySiteDomainBind;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站点绑定域名 Mapper
 *
 * @author 0neBean
 * @since 2023-10-29 12:20:36
 */

public interface GatewaySiteDomainBindMapper extends BaseMapper<GatewaySiteDomainBind> {

    /**
     * 查询域名绑定站点列表
     *
     * @param siteIds siteIds
     * @return 分页api信息
     */
    List<GatewaySiteDomainVo> findBindDomainInfoBySiteIds(@Param("siteIds") List<Long> siteIds);

}
