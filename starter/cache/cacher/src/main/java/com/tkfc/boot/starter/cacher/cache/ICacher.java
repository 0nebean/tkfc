package com.tkfc.boot.starter.cacher.cache;

import com.alibaba.fastjson2.TypeReference;
import com.tkfc.boot.starter.cacher.service.CLock;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 缓存接口 第一层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 10:30
 */
public interface ICacher extends Serializable {

    Map<String, String> getCacheMap();

    Map<String, Long> getCacheExpiredMap();

    Map<String, CLock> getLockMap();

    Map<String, ConcurrentLinkedQueue<Object>> getQueueMap();

    Boolean getBoolean(String key);

    Integer getInteger(String key);

    Long getLong(String key);

    Double getDouble(String key);

    String getJson(String key);

    String getString(String key);

    BigDecimal getBigDecimal(String key);

    Date getDate(String key);

    Timestamp getTimestamp(String key);

    <T> List<T> getList(String key, Class<T> clazz);

    <K, V> Map<K, V> getMap(String key);

    <T> T getObject(String key, Class<T> clazz);

    <T> T getObject(String key, TypeReference<T> type);

    Boolean set(String key, Object value);

    Boolean del(String key);

    Boolean hDel(String key, String hashKey);

    Boolean hSetAll(String key, Map<String, Object> values);

    Map<String, Object> hGetAll(String key);

}
