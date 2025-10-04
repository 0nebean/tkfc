package com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * sonnet用量
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "sonnet用量")
public class SonnetUsageDto {

    @BodyProperty(tag = "输入token")
    private Integer input_tokens;

    @BodyProperty(tag = "输出token")
    private Integer output_tokens;

}
