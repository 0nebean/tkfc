package com.tkfc.sdk.mapper;

import com.tkfc.boot.starter.mybatis.extend.BaseMapper;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.sdk.model.GatewayTicketApiBind;
import com.tkfc.sdk.pojo.vo.GatewayApiVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 凭证绑定接口关系 Mapper
 *
 * @author 0neBean
 * @since 2022-07-20 22:08:33
 */

public interface GatewayTicketApiBindMapper extends BaseMapper<GatewayTicketApiBind> {

    /**
     * 分页查询凭证绑定的接口信息
     *
     * @param ticket     凭证ID
     * @param searchText 搜索字符
     * @param pagination 分页
     * @return 分页api信息
     */
    List<GatewayApiVo> findBindApiInfo(
            @Param("ticket") Long ticket,
            @Param("appKey") String appKey,
            @Param("searchText") String searchText,
            Pagination pagination
    );
}
