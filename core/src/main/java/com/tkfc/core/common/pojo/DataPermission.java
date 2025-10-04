package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据权限
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/21 20:46
 */
@Body(tag = "排序对象")
public class DataPermission {

    public DataPermission() {
        this.hasDataPerm = false;
    }

    @Getter
    @Setter
    @BodyProperty(tag = "机构ID")
    private Long orgId;
    @Getter
    @Setter
    @BodyProperty(tag = "数据sql片段")
    private String joinSql;
    @Getter
    @Setter
    @BodyProperty(tag = "权限sql片段")
    private String permissionSql;
    @Getter
    @Setter
    @BodyProperty(tag = "是否有数据权限")
    private Boolean hasDataPerm;


}
