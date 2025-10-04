package com.tkfc.boot.starter.racher.enums;

import com.tkfc.core.enums.base.BaseEnums;

/**
 * redis 链接模式
 *
 * @author 0neBean
 * @since 2022-09-08 23:30:04
 */
public enum RedisUseModelEnum implements BaseEnums<String> {

    //枚举项
    USE_SINGLE_SERVER("single", "单机模式"),

    USE_CLUSTER_SERVERS("cluster", "集群模式"),

    USE_SENTINEL_SERVERS("sentinel", "哨兵模式"),

    USE_REPLICATED_SERVERS("replicated", "复制模式"),

    USE_MASTER_SLAVE_SERVERS("masterSlave", "主从模式");


    private final String description;
    private final String value;

    RedisUseModelEnum(String value, String description) {
        this.value = value;
        this.description = description;
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
        return null;
    }

    @Override
    public BaseEnums<String>[] getValues() {
        return values();
    }
}
