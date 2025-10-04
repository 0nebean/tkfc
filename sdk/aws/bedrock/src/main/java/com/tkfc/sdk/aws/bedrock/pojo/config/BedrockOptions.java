package com.tkfc.sdk.aws.bedrock.pojo.config;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 翻译参数
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 13:20:17
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "翻译参数")
public class BedrockOptions {

    @BodyProperty(tag = "系统提示词")
    private String sysPrompt;

    @BodyProperty(tag = "anthropic服务版本")
    private String anthropicVersion;

    @BodyProperty(tag = "最大token")
    private Integer maxTokens;

    @BodyProperty(tag = "大模型参数-多样性")
    private Double temperature;

    @BodyProperty(tag = "nova top_p 参数")
    private Double topP;

    @BodyProperty(tag = "nova top_k 参数")
    private Integer topK;

}
