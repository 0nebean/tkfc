package com.tkfc.boot.starter.elasticsearch.extend;

import com.tkfc.core.toolkit.JsonUtil;

import java.io.Serializable;

/**
 * 顶级实体类
 *
 * @author 0neBean
 */
public abstract class BaseModelES implements Serializable {

    private static final long serialVersionUID = 7212458519506826287L;

    public String toJson() {
        return JsonUtil.toJson(this);
    }

    @SuppressWarnings("all")
    public <T> T toVo(Class<T> clazz) {
        return JsonUtil.toBean(toJson(), clazz);
    }

}
