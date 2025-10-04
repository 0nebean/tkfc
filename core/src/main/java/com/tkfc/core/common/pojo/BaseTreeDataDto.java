package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 加载树的请求参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/10 10:53
 */
@Body(tag = "同步加载树的请求参数")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseTreeDataDto {

    @BodyProperty(tag = "自己的ID")
    private Long selfId;

    @BodyProperty(tag = "父级的ID")
    private Long parentId;

    @BodyProperty(tag = "表达式")
    private String[] expressions;

}
