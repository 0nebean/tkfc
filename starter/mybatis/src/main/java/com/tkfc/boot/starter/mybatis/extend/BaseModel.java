package com.tkfc.boot.starter.mybatis.extend;

import com.tkfc.core.common.annotations.orm.LogicalDelete;
import com.tkfc.core.common.annotations.orm.OrderBy;
import com.tkfc.core.toolkit.JsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 顶级实体类
 *
 * @author 0neBean
 */
@OrderBy("id desc")
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = -2836660164599197027L;

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Long operatorId;

    @Getter
    @Setter
    private String operatorName;

    @Getter
    @Setter
    @LogicalDelete
    private String isDeleted;

    @Getter
    @Setter
    private LocalDateTime createTime;

    @Getter
    @Setter
    private LocalDateTime updateTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseModel baseModel = (BaseModel) o;
        return Objects.equals(id, baseModel.id) &&
                Objects.equals(operatorId, baseModel.operatorId) &&
                Objects.equals(operatorName, baseModel.operatorName) &&
                Objects.equals(isDeleted, baseModel.isDeleted) &&
                Objects.equals(createTime, baseModel.createTime) &&
                Objects.equals(updateTime, baseModel.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, operatorId, operatorName, isDeleted, createTime, updateTime);
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }

    @SuppressWarnings("all")
    public <T> T toVo(Class<T> clazz) {
        return JsonUtil.toBean(toJson(), clazz);
    }
}
