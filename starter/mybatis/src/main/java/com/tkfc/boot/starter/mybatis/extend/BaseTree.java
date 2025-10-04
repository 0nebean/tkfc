package com.tkfc.boot.starter.mybatis.extend;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 树类型
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/10 9:56
 */
public abstract class BaseTree<T extends BaseTree<T>> {


    /**
     * ID
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 标题
     */
    @Getter
    @Setter
    private String title;
    /**
     * 类型 item / folder
     */
    @Getter
    @Setter
    private Boolean hasChild;
    /**
     * 是否选中
     */
    @Getter
    @Setter
    private Boolean selected;
    /**
     * 属性
     */
    @Getter
    @Setter
    private Map<String, Object> attr;
    /**
     * 子数据
     */
    @Getter
    @Setter
    private List<T> childList;

}
