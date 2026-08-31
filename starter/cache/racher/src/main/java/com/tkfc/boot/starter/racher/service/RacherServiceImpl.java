package com.tkfc.boot.starter.racher.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.tkfc.cache.base.abstracts.AbstractCache;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 17:47
 */
@Slf4j
public class RacherServiceImpl extends AbstractCache {

    private final static Object QUEUE_LOCK_MONITOR = new Object();
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    public RacherServiceImpl(RedisTemplate<String, String> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public Boolean getBoolean(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Boolean.class);
        });
    }

    @Override
    public Integer getInteger(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Integer.class);
        });
    }

    @Override
    public Long getLong(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Long.class);
        });
    }

    @Override
    public Double getDouble(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Double.class);
        });
    }

    @Override
    public String getJson(String key) {
        return JSON.parseObject(getString(key), String.class);
    }

    @Override
    public String getString(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            return serializer.deserialize(value);
        });
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, BigDecimal.class);
        });
    }

    @Override
    public Date getDate(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Date.class);
        });
    }

    @Override
    public Timestamp getTimestamp(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, Timestamp.class);
        });
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseArray(json, clazz);
        });
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, clazz);
        });
    }

    @Override
    public <T> T getObject(String key, TypeReference<T> type) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(Objects.requireNonNull(serializer.serialize(key)));
            String json = serializer.deserialize(value);
            return JSON.parseObject(json, type);
        });
    }

    @Override
    public void loadAll(Map<String, String> addOn) {
        addOn.forEach(this::set);
    }

    @Override
    public Boolean set(String key, Object value) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            String json = (value instanceof String) ? value.toString() : JSON.toJSONString(value);
            return connection.set(Objects.requireNonNull(serializer.serialize(key)), Objects.requireNonNull(serializer.serialize(json)));
        });
    }

    @Override
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            String json = JSON.toJSONString(value);
            return connection.setNX(Objects.requireNonNull(serializer.serialize(key)), Objects.requireNonNull(serializer.serialize(json)));
        });
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, Long expire) {
        return setIfAbsent(key, value) && expire(key, expire);
    }

    @Override
    public Boolean set(String key, Object value, Long expire) {
        return set(key, value) && expire(key, expire);
    }

    @Override
    public Long increment(String key, Long value) {
        return redisTemplate.opsForValue().increment(key, value);
    }

    @Override
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key, 1L);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Boolean expire(String key, Long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            String json = JSON.toJSONString(value);
            return connection.hSet(Objects.requireNonNull(serializer.serialize(key)), Objects.requireNonNull(serializer.serialize(hashKey)), Objects.requireNonNull(serializer.serialize(json)));
        });
    }

    @Override
    public Boolean hSetIfAbsent(String key, String hashKey, Object value) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            String json = JSON.toJSONString(value);
            return connection.hSetNX(Objects.requireNonNull(serializer.serialize(key)), Objects.requireNonNull(serializer.serialize(hashKey)), Objects.requireNonNull(serializer.serialize(json)));
        });
    }

    @Override
    public Boolean hasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        HashOperations<String, String, String> hash = this.redisTemplate.opsForHash();
        String json = hash.get(key, hashKey);
        return JSON.parseObject(json, clazz);
    }

    @Override
    public <T> T hGet(String key, String hashKey, TypeReference<T> type) {
        HashOperations<String, String, String> hash = this.redisTemplate.opsForHash();
        String json = hash.get(key, hashKey);
        return JSON.parseObject(json, type);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            connection.del(serializer.serialize(key));
            return true;
        });
    }

    @Override
    public Boolean delKey(String pattern) {
        // 获取匹配模式的所有键
        Set<String> keysToDelete = redisTemplate.keys(pattern);
        // 删除匹配的键
        if (CollectionUtil.isNotEmpty(keysToDelete)) {
            keysToDelete.forEach(this::del);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> values) {
        try {
            HashOperations<String, String, String> hash = this.redisTemplate.opsForHash();
            values.forEach((k, v) -> hash.put(key, k, JSON.toJSONString(v)));
        } catch (Exception e) {
            log.error("cacher hSetAll method get err = ", e);
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> hGetAll(String key) {
        HashOperations<String, String, String> hash = this.redisTemplate.opsForHash();
        Map<String, String> entries = hash.entries(key);
        return new HashMap<>(entries);
    }

    @Override
    public Boolean hDel(String key, String hashKey) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        Long deleted = hash.delete(key, hashKey);
        return deleted > 0L;
    }

    @Override
    public ILock getLock(String key) {
        return new RLock(redissonClient.getLock(key));
    }

    @Override
    public Boolean lpush(String key, Object item) {
        synchronized (QUEUE_LOCK_MONITOR) {
            String json = toQueueJson(item);
            if (StringUtil.isBlank(json)) {
                return false;
            }
            return Objects.nonNull(redisTemplate.opsForList().leftPush(key, json));
        }
    }

    @Override
    public <T> T rpop(String key, Class<T> clazz) {
        String json = redisTemplate.opsForList().rightPop(key);
        if (StringUtil.isNotBlank(json)) {
            return JsonUtil.toBean(json, clazz);
        } else {
            return null;
        }
    }

    @Override
    public <T> T brpop(String key, long timeout, TimeUnit timeUnit, Class<T> clazz) {
        String json = redisTemplate.opsForList().rightPop(key, timeout, timeUnit);
        if (StringUtil.isNotBlank(json)) {
            return JsonUtil.toBean(json, clazz);
        } else {
            return null;
        }
    }

    @Override
    public <T> T brpopLpush(String sourceKey, String destinationKey, long timeout, TimeUnit timeUnit, Class<T> clazz) {
        String json = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, timeUnit);
        if (String.class.isAssignableFrom(clazz)) {
            return (T) json;
        }
        if (StringUtil.isNotBlank(json)) {
            return JsonUtil.toBean(json, clazz);
        } else {
            return null;
        }
    }

    @Override
    public Long llen(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size == null ? 0L : size;
    }

    @Override
    public Long lrem(String key, long count, Object item) {
        String json = toQueueJson(item);
        if (StringUtil.isBlank(json)) {
            return 0L;
        }
        Long removed = redisTemplate.opsForList().remove(key, count, json);
        return removed == null ? 0L : removed;
    }

    @Override
    public <T> T rpoplpush(String sourceKey, String destinationKey, Class<T> clazz) {
        String json = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
        if (String.class.isAssignableFrom(clazz)) {
            return (T) json;
        }
        if (StringUtil.isNotBlank(json)) {
            return JsonUtil.toBean(json, clazz);
        }
        return null;
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        List<String> list = redisTemplate.opsForList().range(key, start, end);
        return list == null ? Collections.emptyList() : list;
    }

    private String toQueueJson(Object item) {
        if (item == null) {
            return null;
        }
        if (item instanceof String) {
            return (String) item;
        }
        return JsonUtil.toJson(item);
    }

    @Override
    public Boolean clearQueue(String key) {
        synchronized (QUEUE_LOCK_MONITOR) {
            return del(key);
        }
    }

    @Override
    public <T> List<T> getAllQueueData(String key, Class<T> clazz) {
        synchronized (QUEUE_LOCK_MONITOR) {
            Long end = redisTemplate.opsForList().size(key);
            if (Objects.isNull(end)) {
                return null;
            }
            end = end - 1;
            List<T> result = new ArrayList<>();
            List<String> jsonList = redisTemplate.opsForList().range(key, 0L, end);
            if (CollectionUtil.isEmpty(jsonList)) {
                return null;
            }
            for (String json : jsonList) {
                result.add(JsonUtil.toBean(json, clazz));
            }
            return result;
        }
    }

    @Override
    public Boolean zadd(String key, Double score, Object value) {
        String json = (value instanceof String) ? value.toString() : JSON.toJSONString(value);
        return redisTemplate.opsForZSet().add(key, json, score);
    }

    @Override
    public Set<String> zRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrevRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zrevRangeByScoreWithScores(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    @Override
    public Long zremRangeByRank(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    @Override
    public Long zremRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrevRange(String key, Long start, Long end) {

        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    @Override
    public Long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    @Override
    public Set<String> keysWithPattern(String keyPre) {
        return redisTemplate.keys(keyPre);
    }
}
