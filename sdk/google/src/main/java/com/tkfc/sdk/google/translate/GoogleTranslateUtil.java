// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.tkfc.sdk.google.translate;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkfc.sdk.google.translate.config.GoogleConfig;
import com.tkfc.sdk.google.translate.config.SslProxy;
import com.tkfc.sdk.google.translate.config.TranslateClassAttribute;
import com.tkfc.sdk.google.translate.config.TranslateTextInfo;
import com.tkfc.core.toolkit.RestUtil;
import com.tkfc.core.toolkit.StringUtil;
import org.apache.commons.compress.utils.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * google翻译工具类
 *
 * @author xumingpeng
 * @since 2024-07-08 17:34:34
 */
public class GoogleTranslateUtil {


    public static void main(String[] args)
    {
        TranslateClassAttribute translateClassAttribute1 = TranslateClassAttribute.builder().key("adwadawd").val("&quot;火车&quot;").build();
        ArrayList<TranslateClassAttribute> translateClassAttributes = Lists.newArrayList();
        translateClassAttributes.add(translateClassAttribute1);
        System.out.println(translateContents(GoogleConfig.builder()
                        .key("AIzaSyAYH0zEs1vE6dCJwbEr8yxY3IHm_YObC-o")
                        .url("https://translation.googleapis.com/language/translate/v2")
                        .build(),
                TranslateTextInfo.builder()
                        .translateClassAttributes(translateClassAttributes)
                        .sourceLanguage("zh")
                        .targetLanguage("en")
                        .build(),
                SslProxy.builder()
                        .ip("127.0.0.1")
                        .port(1080).build())
        );
    }

    /**
     * 翻译文本集合
     * <p>
     * 注：使用此翻译方法
     * translateTextInfo （文案参数对象）中需传参：translateClassAttributes（类属性值对象集合）
     * content（文本内容）无需传参
     *
     * @param googleConfig      google配置
     * @param translateTextInfo 文案参数
     * @param proxy    本地测试对象(非必填)
     * @return 翻译内容
     */
    public static List<TranslateClassAttribute> translateContents(GoogleConfig googleConfig, TranslateTextInfo translateTextInfo, SslProxy proxy) {
        //将翻译的内容递归转换成map
        Map<String, String> allFieldsMap = translateTextInfo.getTranslateClassAttributes().stream().collect(Collectors.toMap(TranslateClassAttribute::getKey, TranslateClassAttribute::getVal));
        // 使用StringBuilder拼接Map中所有value值
        StringBuilder translateContentString = new StringBuilder();
        // 递归处理Map中的value
        for (Map.Entry<String, String> entry : allFieldsMap.entrySet()) {
            String formattedValue = formatValue(entry.getValue(), entry.getKey());
            translateContentString.append(formattedValue);
        }
        translateTextInfo.setContent(translateContentString.toString());
        //获取翻译结果
        String resultJson = getTranslateResult(googleConfig, translateTextInfo, proxy);
        //构建google翻译结果出参
        String googleResult = buildGoogleResult(resultJson);
        //构建类属性值对象出参集合
        return buildTranslateClassAttribute(allFieldsMap, googleResult);
    }

    /**
     * 翻译
     * <p>
     * 注：使用此翻译方法
     * translateTextInfo （文案参数对象）中只需传参content（文本内容）
     *
     * @param googleConfig      google配置
     * @param translateTextInfo 文案参数
     * @param proxy    本地测试对象(非必填)
     * @return 翻译内容
     */
    public static String translate(GoogleConfig googleConfig, TranslateTextInfo translateTextInfo, SslProxy proxy) {
        //获取翻译结果
        String resultJson = getTranslateResult(googleConfig, translateTextInfo, proxy);
        return buildGoogleResult(resultJson);
    }

    /**
     * 构建类属性值对象出参集合
     */
    private static ArrayList<TranslateClassAttribute> buildTranslateClassAttribute(Map<String, String> allFieldsMap, String googleResult) {
        ArrayList<TranslateClassAttribute> translateAfterList = Lists.newArrayList();
        if (StringUtil.isNotBlank(googleResult)) {
            // 创建一个Map用于存储结果
            Map<String, String> resultMap = new HashMap<>();
            allFieldsMap.forEach((key, value) -> {
                String valueByDataId = getValueByDataId(googleResult, key);
                resultMap.put(key, valueByDataId);
            });
            resultMap.forEach((key, value) -> {
                translateAfterList.add(TranslateClassAttribute.builder()
                        .key(key)
                        .val(allFieldsMap.get(key))
                        .tranVal(value)
                        .build());
            });
        }
        return translateAfterList;
    }

    /**
     * 通过jsoup获取翻译结构
     *
     * @param htmlContent html文本
     * @param dataId      key值
     * @return key值对应的翻译结果
     */
    @SuppressWarnings("all")
    public static String getValueByDataId(String htmlContent, String dataId) {
        Document doc = Jsoup.parse(htmlContent);
        Elements elements = doc.select("div[data-id=" + dataId + "]");
        if (!elements.isEmpty()) {
            Element element = elements.get(0);
            return element.text();
        } else {
            return null;
        }
    }


    /**
     * 构建google翻译结果出参
     */
    private static String buildGoogleResult(String googleTranslateResult) {
        if (StringUtil.isBlank(googleTranslateResult)) {
            return null;
        }

        JSONObject data = JSONObject.parseObject(googleTranslateResult).getJSONObject("data");
        JSONArray translations = data.getJSONArray("translations");
        if (translations != null && !translations.isEmpty()) {
            JSONObject item = translations.getJSONObject(0);
            return item.getString("translatedText");
        } else {
            return null;
        }
    }

    /**
     * 获取翻译结果
     */
    private static String getTranslateResult(GoogleConfig googleConfig, TranslateTextInfo translateTextInfo, SslProxy proxy) {
        //构建请求参数
        MultiValueMap<String, String> requestBody = getBody(googleConfig, translateTextInfo);
        //获取rest请求工具类
        RestUtil instance = getRestUtil(proxy);
        //发送google翻译请求
        return sendGoogleTranslateHttpReq(googleConfig, requestBody, instance);
    }

    /**
     * 发送google翻译请求
     */
    private static String sendGoogleTranslateHttpReq(GoogleConfig googleConfig, MultiValueMap<String, String> requestBody, RestUtil instance) {
        //构建请求头信息
        JSONObject head = new JSONObject();
        head.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        head.put("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return instance.doPostForRef(googleConfig.getUrl(), requestBody, head, new ParameterizedTypeReference<>() {
        });
    }

    /**
     * 判断是否本地测试
     *
     * @param localProxyTest 本地测试IP代理对象
     * @return rest请求工具类
     */
    private static RestUtil getRestUtil(SslProxy localProxyTest) {
        //代理IP端口不为null,则为返回本地调试所用实例
        if (Objects.nonNull(localProxyTest) && StringUtil.isNotBlank(localProxyTest.getIp()) && Objects.nonNull(localProxyTest.getPort())) {
            return RestUtil.getInstanceWithProxy(localProxyTest.getIp(), localProxyTest.getPort());
        } else {
            return RestUtil.getInstance();
        }
    }

    /**
     * 构建请求体
     *
     * @param googleConfig      google配置
     * @param translateTextInfo 文案参数
     */
    private static MultiValueMap<String, String> getBody(GoogleConfig googleConfig, TranslateTextInfo translateTextInfo) {
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("key", googleConfig.getKey());
        paramsMap.add("q", translateTextInfo.getContent());
        paramsMap.add("target", translateTextInfo.getTargetLanguage());
        paramsMap.add("source", translateTextInfo.getSourceLanguage());
        return paramsMap;
    }


    private static String formatValue(Object value, String key) {
        StringBuilder sb = new StringBuilder();
        // 其他情况，将对象转换为字符串并添加前后的标记
        String valueAsString = String.valueOf(value);
        sb.append("<div data-id=\"").append(key).append("\">").append(valueAsString).append("</div>");
        return sb.toString();
    }

}
