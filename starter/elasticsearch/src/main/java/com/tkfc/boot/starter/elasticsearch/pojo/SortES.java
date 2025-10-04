package com.tkfc.boot.starter.elasticsearch.pojo;

import com.tkfc.core.throwable.base.Assert;
import lombok.Builder;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 0neBean
 * @since 2024/01/
 * 封装排序参数
 */
public class SortES {

    public final List<ESOrder> orders;

    public SortES() {
        orders = new ArrayList<>();
    }

    public SortES(SortOrder direction, String property) {
        orders = new ArrayList<>();
        add(direction, property);
    }

    /**
     * Adds a new sort order to the list of orders.
     *
     * @param direction the direction of the sort, must not be null
     * @param property  the property to sort by, must not be empty
     * @return the current instance of ESSort for method chaining
     */
    public SortES add(SortOrder direction, String property) {
        Assert.notNull(direction, "direction must not be null!");
        Assert.notBlank(property, "fieldName must not be empty!");
        orders.add(ESOrder.builder().direction(direction).property(property).build());
        return this;
    }


    @Data
    @Builder
    public static class ESOrder implements Serializable {
        private final SortOrder direction;
        private final String property;
    }

}
