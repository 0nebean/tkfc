package com.tkfc.dictionary.dto;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;


/**
 * 字典对象
 *
 * @author 0neBean
 * @since 2021-03-01 17:50:22
 */
@Body(tag = "字典对象")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictionaryDto {
    /**
     * 字典项值
     */
    @BodyProperty(tag = "字典项值")
    private String val;
    /**
     * 字典项含义
     */
    @BodyProperty(tag = "字典项含义")
    private String dic;
    /**
     * 字典项词组值
     */
    @BodyProperty(tag = "字典项词组值")
    private String groupVal;
    /**
     * 词组内排序字段
     */
    @BodyProperty(tag = "词组内排序字段")
    private Integer sort;


}