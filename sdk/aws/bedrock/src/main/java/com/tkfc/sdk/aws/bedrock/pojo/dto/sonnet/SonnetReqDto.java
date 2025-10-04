package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * Claude请求入参
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "Claude请求入参实体")
public class SonnetReqDto {

    @BodyProperty(tag = "anthropic服务版本")
    private String anthropic_version;

    @BodyProperty(tag = "最大token")
    private Integer max_tokens;

    @BodyProperty(tag = "大模型参数-多样性")
    private Double temperature;

    @BodyProperty(tag = "系统提示词")
    private String system;

    @BodyProperty(tag = "请求消息")
    private List<SonnetMsgDto> messages;

}
