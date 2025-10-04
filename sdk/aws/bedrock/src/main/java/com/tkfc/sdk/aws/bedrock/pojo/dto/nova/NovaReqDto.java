package com.tkfc.sdk.aws.bedrock.pojo.dto.nova;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Nora请求入参
 * <a href="https://docs.aws.amazon.com/nova/latest/userguide/complete-request-schema.html">...</a>
 * <p>
 * Nova 模型调用需要指定 This model can only be used through an inference profile.
 * <a href="https://docs.aws.amazon.com/bedrock/latest/userguide/cross-region-inference.html">...</a>
 * <p>
 * Use an inference profile in model
 * <a href="https://docs.aws.amazon.com/bedrock/latest/userguide/inference-profiles-use.html">...</a>
 * <p>
 * invoke model
 * <a href="https://docs.aws.amazon.com/bedrock/latest/APIReference/API_runtime_InvokeModel.html#API_runtime_InvokeModel_Example_5">...</a>
 * <p>
 * <a href="https://docs.aws.amazon.com/bedrock/latest/userguide/inference-profiles-support.html">...</a>
 * @author zhouquan
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "Nova请求入参实体")
public class NovaReqDto {
    @Data
    @Builder
    public static class Content {
        private String text;
    }

    @Data
    @Builder
    public static class InferenceConfig {
        @BodyProperty(tag = "最大token")
        private int max_new_tokens;

        @BodyProperty(tag = "大模型参数-多样性")
        private Double temperature;

        @BodyProperty(tag = "top_p")
        private Double top_p;

        @BodyProperty(tag = "top_k")
        private int top_k;
    }

    @Data
    @Builder
    public static class Messages {
        @BodyProperty(tag = "角色")
        private String role;

        @BodyProperty(tag = "内容")
        private List<Content> content;
    }

    @BodyProperty(tag = "系统提示词")
    private List<Content> system;

    @BodyProperty(tag = "接口配置")
    private InferenceConfig inferenceConfig;

    @BodyProperty(tag = "请求消息")
    private List<Messages> messages;

    @BodyProperty(tag = "schema")
    private String schemaVersion;
}
