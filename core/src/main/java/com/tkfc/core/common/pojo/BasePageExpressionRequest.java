package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 通用列表表达式请求对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
@Body(tag = "通用列表表达式请求对象")
public class BasePageExpressionRequest {

    @Getter
    @Setter
    @BodyProperty(tag = "选中的ids")
    private List<Long> selectedIds;

    @Getter
    @Setter
    @BodyProperty(tag = "参数载荷")
    private Object payload;

    @Getter
    @Setter
    @BodyProperty(tag = "表达式")
    private String[] expressions;

    @Getter
    @Setter
    @BodyProperty(tag = "排序")
    private Sort sort;

    @Getter
    @Setter
    @BodyProperty(tag = "分页")
    private Pagination pagination;

    @Getter
    @Setter
    @BodyProperty(tag = "逻辑删除")
    private PhysicallyDeleted physicallyDeleted;


}
