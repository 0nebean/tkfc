package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用列表参数对象请求对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
@Body(tag = "通用列表参数对象请求对象")
public class BasePageVoRequest<T> {

    @Getter
    @Setter
    @BodyProperty(tag = "对象")
    private T data;

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
