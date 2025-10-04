package com.tkfc.sdk.aws.bedrock.pojo.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * 翻译语言枚举
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 11:14:57
 */
public enum TransLanguage implements BaseEnums<String> {

    //枚举项
    EN("English", "英语", 0),
    ZH("Chinese", "中文", 1),
    ZH_TW("Chinese-Traditional", "繁体中文", 2),
    AR("Arabic", "阿拉伯语", 3),
    FR("French", "法语", 4),
    HE("Hebrew", "希伯来语", 5),
    PL("Polish", "波兰语", 6),
    RO("Romanian", "罗马尼亚语", 7),
    SK("Slovak", "斯洛伐克语", 8),
    CS("Czech", "捷克语", 9),
    HU("Hungarian", "匈牙利语", 10),
    BG("Bulgarian", "保加利亚语", 11),
    SI("Slovenian", "斯洛文尼亚语", 12),
    KR("Korean", "韩语", 13),
    JP("Japanese", "日语", 14),
    DE("German", "德语", 15),
    IT("Italian", "意大利语", 16),
    AZ("Azerbaijani", "阿塞拜疆语", 17),
    PT("Portuguese", "葡萄牙语", 18),
    ES("Spanish", "西班牙语", 19),
    LV("Latvian", "拉脱维亚语", 20),
    EE("Estonian", "爱沙尼亚语", 21),
    HR("Croatian", "克罗地亚语", 22),
    GR("Greek", "希腊语", 23),
    UZ("Uzbek (Latin)", "乌兹别克语", 24),
    RU("Russian", "俄语", 25),
    ;

    private final String description;
    private final String value;
    private final Integer sort;

    TransLanguage(String value, String description, Integer sort) {
        this.value = value;
        this.description = description;
        this.sort = sort;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    @Override
    public BaseEnums<String>[] getValues() {
        return values();
    }
}
