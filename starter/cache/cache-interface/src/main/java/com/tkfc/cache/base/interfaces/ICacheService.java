package com.tkfc.cache.base.interfaces;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存方法定义
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/10 21:03
 */
public interface ICacheService extends ICache {

    String LOCK_HASH_KEY = "LOCK_HASH_KEY";

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

    <T> T getObject(String key, Class<T> clazz);

    <T> T getObject(String key, TypeReference<T> type);

    Boolean set(String key, Object value);

    Long increment(String key, Long value);

    Long increment(String key);

    Boolean setIfAbsent(String key, Object value);

    Boolean setIfAbsent(String key, Object value, Long expire);

    Boolean hasKey(String key);

    Boolean set(String key, Object value, Long expire);

    Boolean del(String key);

    Boolean delKey(String pattern);

    Boolean expire(String key, Long expire);

    Long getExpire(String key);

    Boolean hSet(String key, String hashKey, Object value);

    Boolean hSetIfAbsent(String key, String hashKey, Object value);

    Boolean hasKey(String key, String hashKey);

    Boolean hSetAll(String key, Map<String, Object> values);

    Map<String, Object> hGetAll(String key);

    <T> T hGet(String key, String hashKey, Class<T> clazz);

    <T> T hGet(String key, String hashKey, TypeReference<T> type);

    Boolean hDel(String key, String hashKey);

    ILock getLock(String key);

    Boolean lpush(String key, Object item);

    <T> T rpop(String key, Class<T> clazz);

    <T> T brpop(String key, long timeout, TimeUnit timeUnit, Class<T> clazz);

    <T> T brpopLpush(String sourceKey, String destinationKey, long timeout, TimeUnit timeUnit, Class<T> clazz);

    Boolean clearQueue(String key);

    <T> List<T> getAllQueueData(String key, Class<T> clazz);

    Boolean zadd(String key, Double score, Object value);

    Set<String> zRangeByScore(String key, Double min, Double max);

    Set<String> zrevRangeByScore(String key, Double min, Double max);

    Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, Long start, Long end);

    Set<ZSetOperations.TypedTuple<String>> zrevRangeByScoreWithScores(String key, Double min, Double max);

    Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, Double min, Double max);

    Long zremRangeByRank(String key, Long start, Long end);

    Long zremRangeByScore(String key, Double min, Double max);

    Set<String> zrevRange(String key, Long start, Long end);

    Long zcard(String key);

    Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end);

    Set<String> keysWithPattern(String keyPre);
}
