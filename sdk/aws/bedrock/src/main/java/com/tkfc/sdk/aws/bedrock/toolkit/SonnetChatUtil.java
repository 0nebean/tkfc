package com.tkfc.sdk.aws.bedrock.toolkit;


import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.aws.bedrock.constant.Constants;
import com.tkfc.sdk.aws.bedrock.pojo.config.BedrockConfig;
import com.tkfc.sdk.aws.bedrock.pojo.config.BedrockOptions;
import com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet.SonnetMsgDto;
import com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet.SonnetReqDto;
import com.tkfc.sdk.aws.bedrock.pojo.dto.sonnet.SonnetRespDto;
import com.tkfc.sdk.aws.bedrock.toolkit.common.BedrockRuntime;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
import java.util.Objects;

/**
 * gpt聊天会话工具类(Claude 3.5 Sonnet)
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 14:18:43
 */
public class SonnetChatUtil extends BedrockRuntime {


    /**
     * 对话
     *
     * @param config   配置
     * @param messages 消息
     * @param options  选项
     * @return 返回新的对话
     */
    public static SonnetRespDto chat(BedrockConfig config, List<SonnetMsgDto> messages, BedrockOptions options) {
        BedrockRuntimeClient claudeClient = initClaudeClient(config);

        SonnetReqDto transRequest = buildTransReqParam(messages, options);
        InvokeModelResponse response = doRequest(claudeClient, transRequest);
        return covertSingletonResult(response);
    }


    /**
     * 包装单个翻译结果
     *
     * @param response 请求响应
     * @return 翻译的结果
     */
    private static SonnetRespDto covertSingletonResult(InvokeModelResponse response) {
        String responseResult = response.body().asUtf8String();
        return JsonUtil.toBean(responseResult, SonnetRespDto.class);
    }

    /**
     * 发送翻译请求
     *
     * @param claudeClient 请求实例
     * @param transRequest 请求参数
     * @return 请求响应
     */
    private static InvokeModelResponse doRequest(BedrockRuntimeClient claudeClient, SonnetReqDto transRequest) {
        return claudeClient.invokeModel(request -> request
                .body(SdkBytes.fromUtf8String(JsonUtil.toJsonWithFeatures(transRequest)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .modelId(Constants.bedrock.CHAT_MODEL_ID_SONNET));
    }

    /**
     * 构建翻译参数
     *
     * @param messages 会话集合
     * @param options  Claude详细配置
     * @return 翻译参数
     */
    private static SonnetReqDto buildTransReqParam(List<SonnetMsgDto> messages, BedrockOptions options) {
        options = Objects.isNull(options) ? BedrockOptions.builder().build() : options;
        return SonnetReqDto.builder()
                .system(StringPool.EMPTY)
                .anthropic_version(StringUtil.isNotBlank(options.getAnthropicVersion()) ? options.getAnthropicVersion() : Constants.bedrock.ANTHROPIC_VERSION_DEFAULT)
                .max_tokens(Objects.nonNull(options.getMaxTokens()) ? options.getMaxTokens() : Constants.bedrock.MAX_TOKENS_DEFAULT)
                .temperature(Objects.nonNull(options.getTemperature()) ? options.getTemperature() : Constants.bedrock.TEMPERATURE_DEFAULT)
                .messages(messages)
                .build();
    }


}
