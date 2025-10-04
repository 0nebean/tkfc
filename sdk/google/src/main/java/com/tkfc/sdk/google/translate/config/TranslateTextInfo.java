package com.tkfc.sdk.google.translate.config;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslateTextInfo {

    /**
     * 文本内容
     */
    private String content;

    /**
     * 类属性值对象集合
     */
    private List<TranslateClassAttribute> translateClassAttributes;

    /**
     * 文本原语言缩写
     */
    private String sourceLanguage;

    /**
     * 目标语言缩写
     */
    private String targetLanguage;

}

