package com.tkfc.sdk.pojo.vo;

import com.tkfc.core.common.annotations.web.param.Body;
import lombok.*;

/**
 * 操作文件数据对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-24 09:26:59
 */
@Body(tag = "操作文件数据对象")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BizFileTranVo {

    private String url;
    private String icon;
    private String fileType;
    private Integer fileSize;

}
