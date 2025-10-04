package com.tkfc.boot.starter.mybatis.extend;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.fastjson2.annotation.JSONField;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 顶级Vo
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/21 0:19
 */
public abstract class BaseVo<T extends BaseModel> {

    @Getter
    @Setter
    @ExcelIgnore
    @BodyProperty(tag = "主键")
    private Long id;

    @Getter
    @Setter
    @ExcelIgnore
    @BodyProperty(tag = "创建时间")
    private LocalDateTime createTime;

    @Getter
    @Setter
    @ExcelIgnore
    @BodyProperty(tag = "更新时间")
    private LocalDateTime updateTime;

    @JSONField(serialize = false)
    @SuppressWarnings("all")
    public T toModel() {
        Class<?> clazz = ReflectionUtil.getClassT(this, 0);
        return (T) JsonUtil.toBean(toJson(), clazz);
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }
}
