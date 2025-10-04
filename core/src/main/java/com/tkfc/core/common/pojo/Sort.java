package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 排序封装字段
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
@Body(tag = "排序对象")
@Getter
@Setter
@Builder
public class Sort implements Serializable {

    private static final long serialVersionUID = 1408490417723002114L;
    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";
    public static final String DEFAULT_ORDER_BY = "id";

    @Getter
    @Setter
    @BodyProperty(tag = "排序方式")
    private String sort = "";
    @Getter
    @Setter
    @BodyProperty(tag = "排序字段")
    private String orderBy = "";

    public Sort() {
        this.sort = SORT_DESC;
        this.orderBy = DEFAULT_ORDER_BY;
    }

    public Sort(String sort, String orderBy) {
        this.sort = sort;
        this.orderBy = orderBy;
    }

}
