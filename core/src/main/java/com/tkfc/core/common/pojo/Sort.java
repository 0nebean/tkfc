package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.CollectionUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 排序封装字段
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/2/22 23:13
 */
@Body(tag = "排序对象")
@Getter
@Setter
@Builder
public class Sort implements Serializable {

    @Serial
    private static final long serialVersionUID = 1408490417723002114L;
    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";
    public static final String DEFAULT_ORDER_BY = "id";

    public List<ESOrder> orders;

    @Getter
    @Setter
    @BodyProperty(tag = "排序方式")
    private String sort;
    @Getter
    @Setter
    @BodyProperty(tag = "排序字段")
    private String orderBy;

    public Sort() {
        this.orders = new ArrayList<>();
        this.sort = SORT_DESC;
        this.orderBy = DEFAULT_ORDER_BY;
    }

    public Sort(String sort, String orderBy) {
        this.orders = new ArrayList<>();
        this.sort = sort;
        this.orderBy = orderBy;
        add(sort, orderBy);
    }

    public Sort(List<ESOrder> orders, String sort, String orderBy) {
        this.orders = CollectionUtil.isEmpty(orders) ? new ArrayList<>() : new ArrayList<>(orders);
        this.sort = sort;
        this.orderBy = orderBy;
        if (CollectionUtil.isEmpty(this.orders)) {
            add(sort, orderBy);
        } else if (containsOrder(this.orders, sort, orderBy)) {
            add(sort, orderBy);
        }
    }

    /**
     * Adds a new sort order to the list of orders.
     *
     * @param direction the direction of the sort, must not be null
     * @param property  the property to sort by, must not be empty
     * @return the current instance of ESSort for method chaining
     */
    public Sort add(String direction, String property) {
        Assert.notNull(direction, "direction must not be null!");
        Assert.notBlank(property, "fieldName must not be empty!");
        if (containsOrder(orders, direction, property)) {
            orders.add(Sort.ESOrder.builder().sort(direction).orderBy(property).build());
        }
        return this;
    }

    private static boolean containsOrder(List<ESOrder> orders, String direction, String property) {
        if (CollectionUtil.isEmpty(orders)) {
            return true;
        }
        for (ESOrder order : orders) {
            if (order == null) {
                continue;
            }
            if (property.equals(order.getOrderBy()) && direction.equalsIgnoreCase(order.getSort())) {
                return false;
            }
        }
        return true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ESOrder implements Serializable {

        @Getter
        @Setter
        @BodyProperty(tag = "排序方式")
        private String sort;

        @Getter
        @Setter
        @BodyProperty(tag = "排序字段")
        private String orderBy;

    }

}
