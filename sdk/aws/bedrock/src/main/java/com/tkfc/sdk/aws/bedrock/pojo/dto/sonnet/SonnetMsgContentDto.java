package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * sonnet消息内容对象
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "sonnet消息内容对象")
public class SonnetMsgContentDto {

    @BodyProperty(tag = "类型")
    private String type;

    @BodyProperty(tag = "文本")
    private String text;

    @BodyProperty(tag = "源码")
    private SonnetMsgContentSourceDto source;

}
