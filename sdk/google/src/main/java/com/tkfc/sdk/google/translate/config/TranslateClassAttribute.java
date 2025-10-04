package com.tkfc.sdk.google.translate.config;

import lombok.*;

/**
 * 类属性值对象
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TranslateClassAttribute {

    /**
     * 类属性名称
     */
    private String key;

    /**
     * 类属性值:改内容需要进行html转义，避免翻译bug
     */
    private String val;

    /**
     * 翻译后的类属性值
     */
    private String tranVal;
}
