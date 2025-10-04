package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * sonnet消息内容对象
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "sonnet消息内容对象")
public class SonnetMsgContentSourceDto {

    @BodyProperty(tag = "类型")
    private String type;

    @BodyProperty(tag = "媒体类型")
    private String media_type;

    @BodyProperty(tag = "内容")
    private String data;
}
