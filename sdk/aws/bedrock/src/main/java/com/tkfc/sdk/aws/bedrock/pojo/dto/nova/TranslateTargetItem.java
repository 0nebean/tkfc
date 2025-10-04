package com.tkfc.sdk.aws.bedrock.pojo.dto.nova;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 翻译目标
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
@Body(tag = "翻译目标")
public class TranslateTargetItem {

    @BodyProperty(tag = "键")
    private String key;

    @BodyProperty(tag = "值")
    private String value;

    @BodyProperty(tag = "翻译值")
    private String tarnsValue;

}
