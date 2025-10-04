package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;

import java.io.Serializable;

/**
 * 物理删除标识
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/27 11:41
 */
@Body(tag = "物理删除标识")
public class PhysicallyDeleted implements Serializable {


    public final static Integer DELETED = 1;
    public final static Integer UN_DELETED = 0;
    private static final long serialVersionUID = -5639555111124108122L;

    public PhysicallyDeleted() {
        setValue(UN_DELETED);
    }

    @BodyProperty(tag = "物理删除标识")
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
