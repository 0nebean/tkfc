package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * sonnet消息对象
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "sonnet消息对象")
public class SonnetMsgDto {

    @BodyProperty(tag = "角色")
    private String role;

    @BodyProperty(tag = "类型")
    private String type;


    @BodyProperty(tag = "内容")
    private List<SonnetMsgContentDto> content;

}
