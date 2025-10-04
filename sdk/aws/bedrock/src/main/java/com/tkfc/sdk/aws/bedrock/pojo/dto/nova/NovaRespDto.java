package com.tkfc.sdk.aws.bedrock.pojo.dto.nova;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * @author ZHOUQUAN
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "Nova响应出参实体")
public class NovaRespDto {
    @Data
    @Builder
    @Getter
    @Setter
    public static class Content {
        private String text;
    }

    @Data
    @Builder
    public static class Messages {
        @BodyProperty(tag = "角色")
        private String role;

        @BodyProperty(tag = "内容")
        private List<NovaReqDto.Content> content;
    }

    @Data
    @Builder
    public static class Output {
        @BodyProperty(tag = "角色")
        private Messages message;
    }

    @BodyProperty(tag = "输出")
    private Output output;

    @BodyProperty(tag = "终止原因")
    private String stopReason;

    @BodyProperty(tag = "使用数据统计")
    private JSONObject usage;
}
