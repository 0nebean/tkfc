package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "Claude响应出参实体")
public class SonnetRespDto {

    @BodyProperty(tag = "ID")
    private String id;

    @BodyProperty(tag = "响应内容实体")
    private String type;

    @BodyProperty(tag = "响应内容实体")
    private String role;

    @BodyProperty(tag = "响应内容实体")
    private String model;

    @BodyProperty(tag = "响应内容实体")
    private List<SonnetMsgContentDto> content;

    @BodyProperty(tag = "终止原因")
    private String stop_reason;

    @BodyProperty(tag = "终止序列")
    private String stop_sequence;

    @BodyProperty(tag = "响应内容实体")
    private SonnetUsageDto  usage;
}
