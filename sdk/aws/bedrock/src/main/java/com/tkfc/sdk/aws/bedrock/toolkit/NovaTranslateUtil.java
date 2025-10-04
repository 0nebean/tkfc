package com.tkfc.sdk.aws.bedrock.toolkit;

import com.alibaba.fastjson2.TypeReference;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.aws.bedrock.constant.Constants;
import com.tkfc.sdk.aws.bedrock.exception.UnKnowLanguageException;
import com.tkfc.sdk.aws.bedrock.exception.UnexpectedAiAnswerException;
import com.tkfc.sdk.aws.bedrock.pojo.config.BedrockConfig;
import com.tkfc.sdk.aws.bedrock.pojo.config.BedrockOptions;
import com.tkfc.sdk.aws.bedrock.pojo.dto.nova.NovaReqDto;
import com.tkfc.sdk.aws.bedrock.pojo.dto.nova.NovaRespDto;
import com.tkfc.sdk.aws.bedrock.pojo.dto.nova.TranslateTargetItem;
import com.tkfc.sdk.aws.bedrock.pojo.enums.TransLanguage;
import com.tkfc.sdk.aws.bedrock.toolkit.common.BedrockRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Nova翻译工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 14:18:43
 */
@Slf4j
public class NovaTranslateUtil extends BedrockRuntime {

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static List<TranslateTargetItem> translate(BedrockConfig config, List<TranslateTargetItem> sourceText, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        BedrockRuntimeClient claudeClient = initClaudeClient(config);
        NovaReqDto novaTransRequest = buildTransReqParam(sourceText, null, targetLang, options);
        InvokeModelResponse response = doRequest(claudeClient, novaTransRequest);
        return covertSingletonTranslateResult(response);
    }


    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 源语言
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static List<TranslateTargetItem> translate(BedrockConfig config, List<TranslateTargetItem> sourceText, String sourceLang, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        BedrockRuntimeClient claudeClient = initClaudeClient(config);
        NovaReqDto novaTransRequest = buildTransReqParam(sourceText, sourceLang, targetLang, options);
        InvokeModelResponse response = doRequest(claudeClient, novaTransRequest);
        return covertSingletonTranslateResult(response);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static List<TranslateTargetItem> translate(BedrockConfig config, List<TranslateTargetItem> sourceText, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, null, targetLang, null);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 源语言
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static List<TranslateTargetItem> translate(BedrockConfig config, List<TranslateTargetItem> sourceText, String sourceLang, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, sourceLang, targetLang, null);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static TranslateTargetItem translate(BedrockConfig config, TranslateTargetItem sourceText, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, Collections.singletonList(sourceText), null, targetLang, options).get(0);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 源语言
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static TranslateTargetItem translate(BedrockConfig config, TranslateTargetItem sourceText, String sourceLang, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, Collections.singletonList(sourceText), sourceLang, targetLang, options).get(0);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static TranslateTargetItem translate(BedrockConfig config, TranslateTargetItem sourceText, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, null, targetLang, null);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 源语言
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static TranslateTargetItem translate(BedrockConfig config, TranslateTargetItem sourceText, String sourceLang, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, sourceLang, targetLang, null);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static String translate(BedrockConfig config, String sourceText, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        TranslateTargetItem targetItem = TranslateTargetItem.builder().key("key1").value(sourceText).build();
        return translate(config, Collections.singletonList(targetItem), null, targetLang, options).get(0).getTarnsValue();
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 源语言
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译的结果
     */
    public static String translate(BedrockConfig config, String sourceText, String sourceLang, String targetLang, BedrockOptions options) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        TranslateTargetItem targetItem = TranslateTargetItem.builder().key("key1").value(sourceText).build();
        return translate(config, Collections.singletonList(targetItem), sourceLang, targetLang, options).get(0).getTarnsValue();
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sysPrompt 系统提示词
     * @param text 对话文本
     * @return 翻译的结果
     */
    public static String call(BedrockConfig config, String sysPrompt, String text, BedrockOptions options) {
        BedrockRuntimeClient claudeClient = initClaudeClient(config);
        NovaReqDto novaTransRequest = buildAskReqParam(sysPrompt, text, options);
        InvokeModelResponse response = doRequest(claudeClient, novaTransRequest);
        return getNovaRespMsg(new String(response.body().asByteArray()));
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sysPrompt 系统提示词
     * @param text 对话文本
     * @return 翻译的结果
     */
    public static String ask(BedrockConfig config, String sysPrompt, String text, BedrockOptions options) {
        BedrockRuntimeClient claudeClient = initClaudeClient(config);
        NovaReqDto novaTransRequest = buildAskReqParam(sysPrompt, text, options);
        InvokeModelResponse response = doRequest(claudeClient, novaTransRequest);
        return covertRespToString(new String(response.body().asByteArray()));
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static String translate(BedrockConfig config, String sourceText, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, null, targetLang, null);
    }

    /**
     * 翻译单个文本
     *
     * @param config     Claude配置
     * @param sourceText 源文本
     * @param sourceLang 目标语言
     * @param targetLang 翻译语言
     * @return 翻译的结果
     */
    public static String translate(BedrockConfig config, String sourceText, String sourceLang, String targetLang) throws UnKnowLanguageException, UnexpectedAiAnswerException {
        return translate(config, sourceText, sourceLang, targetLang, null);
    }

    /**
     * 将返回结果转换为String
     *
     * @param responseResult 翻译结果
     * @return 翻译结果的字符
     */
    private static String covertRespToString(String responseResult) {
        String translateResult = getNovaRespMsg(responseResult);
        Matcher matcher = Constants.bedrock.TRANSLATED_REGEX.matcher(translateResult);
        String firstMatch = null;
        while (matcher.find()) {
            if (StringUtil.isNotBlank(matcher.group(1))) {
                firstMatch = matcher.group(1);
            }
        }
        return firstMatch;
    }

    /**
     * 获取nova的返回消息
     * @param responseResult 响应结果
     * @return 响应消息
     */
    private static String getNovaRespMsg(String responseResult) {
        NovaRespDto novaResponse = JsonUtil.toBean(responseResult, NovaRespDto.class);
        return Optional.ofNullable(novaResponse.getOutput())
                .map(NovaRespDto.Output::getMessage)
                .map(NovaRespDto.Messages::getContent)
                .orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .map(NovaReqDto.Content::getText)
                .orElse(StringPool.EMPTY);
    }

    /**
     * 包装单个翻译结果
     *
     * @param response 请求响应
     * @return 翻译的结果
     */
    private static List<TranslateTargetItem> covertSingletonTranslateResult(InvokeModelResponse response) throws UnexpectedAiAnswerException {
        String resp = new String(response.body().asByteArray());
        String respStr = covertRespToString(resp);
        if (StringUtil.isBlank(respStr)) {
            throw new UnexpectedAiAnswerException(String.format("call claude trans got an result = %s", resp));
        }
        if (JsonUtil.isNotJson(respStr)) {
            throw new UnexpectedAiAnswerException(String.format("call claude trans got an result = %s", resp));
        }
        List<TranslateTargetItem> result = JsonUtil.toBean(respStr, new TypeReference<>() {
        });
        result.forEach(item -> {
            if (StringUtil.isNotBlank(item.getValue()) && StringUtil.isBlank(item.getTarnsValue())) {
                item.setTarnsValue(item.getValue());
            }
        });
        return result;
    }

    /**
     * 发送翻译请求
     *
     * @param claudeClient 请求实例
     * @param transRequest 请求参数
     * @return 请求响应
     */
    private static InvokeModelResponse doRequest(BedrockRuntimeClient claudeClient, NovaReqDto transRequest) {
        InvokeModelResponse response;
        try (claudeClient) {
            response = claudeClient.invokeModel(request -> request
                    .body(SdkBytes.fromUtf8String(JsonUtil.toJson(transRequest)))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .modelId(Constants.bedrock.NOVA_TRANS_MODEL_ID));
        }
        return response;
    }


    /**
     * 构建nova翻译参数
     *
     * @param sourceText 源文本
     * @param sourceLang 目标语言
     * @param targetLang 翻译语言
     * @param options    翻译参数
     * @return 翻译参数
     */
    private static NovaReqDto buildTransReqParam(List<TranslateTargetItem> sourceText, String sourceLang, String targetLang, BedrockOptions options) throws UnKnowLanguageException {
        List<NovaReqDto.Messages> message = Collections.singletonList(NovaReqDto.Messages.builder()
                .role(Constants.bedrock.USER_DEFAULT)
                .content(Collections.singletonList(NovaReqDto.Content.builder().text(JsonUtil.toJson(sourceText)).build()))
                .build());
        options = Objects.isNull(options) ? BedrockOptions.builder().build() : options;
        NovaReqDto.InferenceConfig inferenceConfig = NovaReqDto.InferenceConfig.builder()
                .max_new_tokens(Objects.nonNull(options.getMaxTokens()) ? options.getMaxTokens() : Constants.bedrock.MAX_TOKENS_DEFAULT)
                .top_k(Objects.nonNull(options.getTopK()) ? options.getTopK() : Constants.bedrock.NOVA_TOP_K)
                .top_p(Objects.nonNull(options.getTopP()) ? options.getTopP() : Constants.bedrock.NOVA_TOP_P)
                .temperature(Objects.nonNull(options.getTemperature()) ? options.getTemperature() : Constants.bedrock.TEMPERATURE_DEFAULT)
                .build();
        getLanguageType(targetLang);
        String sysPrompt;
        if (StringUtil.isBlank(options.getSysPrompt())) {
            sysPrompt = Constants.bedrock.SYS_AP_DEFAULT;
        } else {
            sysPrompt = options.getSysPrompt();
        }
        if (StringUtil.isBlank(sourceLang)) {
            sysPrompt = sysPrompt.replace("of the (from_language) ", StringPool.EMPTY);
        } else {
            sysPrompt = sysPrompt.replace("(from_language)", getLanguageType(sourceLang).getValue());
        }
        sysPrompt = sysPrompt.replace("(to_language)", getLanguageType(targetLang).getValue());
        return NovaReqDto.builder()
                .system(Collections.singletonList(NovaReqDto.Content.builder().text(sysPrompt).build()))
                .inferenceConfig(inferenceConfig)
                .schemaVersion("messages-v1")
                .messages(message)
                .build();
    }

    /**
     * 构建nova对话参数
     *
     * @param sysPrompt 系统提示词
     * @param text 对话文本
     * @param options    翻译参数
     * @return 翻译参数
     */
    private static NovaReqDto buildAskReqParam(String sysPrompt, String text, BedrockOptions options) {
        List<NovaReqDto.Messages> message = Collections.singletonList(NovaReqDto.Messages.builder()
                .role(Constants.bedrock.USER_DEFAULT)
                .content(Collections.singletonList(NovaReqDto.Content.builder().text(JsonUtil.toJson(text)).build()))
                .build());
        options = Objects.isNull(options) ? BedrockOptions.builder().build() : options;
        NovaReqDto.InferenceConfig inferenceConfig = NovaReqDto.InferenceConfig.builder()
                .max_new_tokens(Objects.nonNull(options.getMaxTokens()) ? options.getMaxTokens() : Constants.bedrock.MAX_TOKENS_DEFAULT)
                .top_k(Objects.nonNull(options.getTopK()) ? options.getTopK() : Constants.bedrock.NOVA_TOP_K)
                .top_p(Objects.nonNull(options.getTopP()) ? options.getTopP() : Constants.bedrock.NOVA_TOP_P)
                .temperature(Objects.nonNull(options.getTemperature()) ? options.getTemperature() : Constants.bedrock.TEMPERATURE_DEFAULT)
                .build();
        return NovaReqDto.builder()
                .system(Collections.singletonList(NovaReqDto.Content.builder().text(sysPrompt).build()))
                .inferenceConfig(inferenceConfig)
                .schemaVersion("messages-v1")
                .messages(message)
                .build();
    }

    /**
     * 获取语言类型
     *
     * @param lang 语言
     * @return 语言类型
     * @throws UnKnowLanguageException 未知语言异常
     */
    private static TransLanguage getLanguageType(String lang) throws UnKnowLanguageException {
        if (StringUtil.isBlank(lang)) {
            throw new UnKnowLanguageException(String.format("un know language %s", lang));
        }
        TransLanguage result;
        try {
            result = TransLanguage.valueOf(lang.toUpperCase());
        } catch (Exception ignored) {
            throw new UnKnowLanguageException(String.format("un know language %s", lang));
        }
        return result;
    }

}
