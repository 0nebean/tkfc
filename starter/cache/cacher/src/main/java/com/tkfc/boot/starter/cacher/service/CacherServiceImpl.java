package com.tkfc.boot.starter.cacher.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.tkfc.boot.starter.cacher.cache.Cacher;
import com.tkfc.boot.starter.cacher.task.KeyCleaner;
import com.tkfc.cache.base.abstracts.AbstractCache;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓存服务实现类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 17:47
 */
@Slf4j
public class CacherServiceImpl extends AbstractCache {

    private final static Object LOCK_MONITOR = new Object();
    private final static Object QUEUE_LOCK_MONITOR = new Object();

    @Override
    public Boolean getBoolean(String key) {
        return Cacher.getInstance().getBoolean(key);
    }

    @Override
    public Integer getInteger(String key) {
        return Cacher.getInstance().getInteger(key);
    }

    @Override
    public Long getLong(String key) {
        return Cacher.getInstance().getLong(key);
    }

    @Override
    public Double getDouble(String key) {
        return Cacher.getInstance().getDouble(key);
    }

    @Override
    public String getJson(String key) {
        return Cacher.getInstance().getJson(key);
    }

    @Override
    public String getString(String key) {
        return Cacher.getInstance().getString(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return Cacher.getInstance().getBigDecimal(key);
    }

    @Override
    public Date getDate(String key) {
        return Cacher.getInstance().getDate(key);
    }

    @Override
    public Timestamp getTimestamp(String key) {
        return Cacher.getInstance().getTimestamp(key);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return Cacher.getInstance().getList(key, clazz);
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return Cacher.getInstance().getObject(key, clazz);
    }

    @Override
    public <T> T getObject(String key, TypeReference<T> type) {
        return Cacher.getInstance().getObject(key, type);
    }

    @Override
    public void loadAll(Map<String, String> addOn) {
        Cacher.getInstance().getCacheMap().putAll(addOn);
    }

    @Override
    public Boolean set(String key, Object value) {
        return Cacher.getInstance().set(key, value);
    }

    @Override
    public Long increment(String key, Long value) {
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            Long val = Optional.ofNullable(getLong(key)).orElse(0L);
            set(key, val + value);
            return val + value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Long increment(String key) {
        return increment(key, 1L);
    }

    @Override
    public Boolean setIfAbsent(String key, Object value) {
        return Cacher.getInstance().setIfAbsent(key, value);
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
    public Boolean hasKey(String key) {
        return Cacher.getInstance().getCacheMap().containsKey(key);
    }

    @Override
    public Boolean expire(String key, Long expire) {
        try {
            Long resultForSetTime = Cacher.getInstance().getCacheExpiredMap().put(key, expire);
            Long resultForSetName = Cacher.getInstance().getCacheExpiredMap().put(key + KeyCleaner.EXPIRE_TIME_SUFFIX, System.currentTimeMillis());
            return Objects.nonNull(resultForSetTime) && Objects.nonNull(resultForSetName);
        } catch (Exception e) {
            log.error("set key expire time failure e = ", e);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        Long startTime = Cacher.getInstance().getCacheExpiredMap().get(key + KeyCleaner.EXPIRE_TIME_SUFFIX);
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public Boolean del(String key) {
        return Cacher.getInstance().del(key);
    }

    @Override
    public Boolean delKey(String pattern) {
        for (String key : Cacher.getInstance().getCacheMap().keySet()) {
            if (key.startsWith(pattern)) {
                del(key);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value) {
        Map<String, String> map = Cacher.getInstance().getMap(key);
        if (null == map) {
            map = new HashMap<>();
        }
        map.put(hashKey, JSON.toJSONString(value));
        return set(key, map);
    }

    @Override
    public Boolean hSetIfAbsent(String key, String hashKey, Object value) {
        Map<String, String> map = Cacher.getInstance().getMap(key);
        if (null == map) {
            map = new HashMap<>();
        }
        String result = map.putIfAbsent(hashKey, JSON.toJSONString(value));
        set(key, map);
        return StringUtil.isNotBlank(result);
    }

    @Override
    public Boolean hasKey(String key, String hashKey) {
        if (!Cacher.getInstance().getCacheMap().containsKey(key)) {
            return false;
        }
        Map<String, String> map = Cacher.getInstance().getMap(key);
        if (null == map || map.isEmpty()) {
            return false;
        }
        return map.containsKey(hashKey);
    }

    @Override
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        Map<String, String> map = Cacher.getInstance().getMap(key);
        if (null == map) {
            return null;
        }
        String json = map.get(hashKey);
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        T result;
        try {
            result = JSON.parseObject(json, clazz);
        } catch (Exception e) {
            log.error("cast json to object err ,json = {}", json);
            return null;
        }
        return result;
    }

    @Override
    public <T> T hGet(String key, String hashKey, TypeReference<T> type) {
        Map<String, String> map = Cacher.getInstance().getMap(key);
        if (null == map) {
            return null;
        }
        String json = map.get(hashKey);
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        T result;
        try {
            result = JSON.parseObject(json, type);
        } catch (Exception e) {
            log.error("cast json to object err ,json = {}", json);
            return null;
        }
        return result;
    }

    @Override
    public Boolean hDel(String key, String hashKey) {
        return Cacher.getInstance().hDel(key, hashKey);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> values) {
        return Cacher.getInstance().hSetAll(key, values);
    }

    @Override
    public Map<String, Object> hGetAll(String key) {
        return Cacher.getInstance().hGetAll(key);
    }

    @Override
    public ILock getLock(String key) {
        synchronized (LOCK_MONITOR) {
            CLock lock = Cacher.getInstance().getLockMap().get(key);
            if (Objects.isNull(lock)) {
                lock = new CLock();
                Cacher.getInstance().getLockMap().put(key, lock);
            }
            return lock;
        }
    }

    @Override
    public Boolean lpush(String key, Object item) {
        ConcurrentLinkedQueue<Object> queue;
        synchronized (QUEUE_LOCK_MONITOR) {
            if (Cacher.getInstance().getQueueMap().containsKey(key)) {
                queue = Cacher.getInstance().getQueueMap().get(key);
            } else {
                queue = new ConcurrentLinkedQueue<>();
                Cacher.getInstance().getQueueMap().put(key, queue);
            }
            // 与 Redis 一致：队列元素统一存 JSON 字符串，便于 lrem 精确 ACK
            Object stored = item instanceof String ? item : JsonUtil.toJson(item);
            return queue.add(stored);
        }
    }

    @Override
    public <T> T rpop(String key, Class<T> clazz) {
        ConcurrentLinkedQueue<Object> queue;
        synchronized (QUEUE_LOCK_MONITOR) {
            if (Cacher.getInstance().getQueueMap().containsKey(key)) {
                queue = Cacher.getInstance().getQueueMap().get(key);
                Object item = queue.poll();
                if (Objects.isNull(item)) {
                    return null;
                }
                return JsonUtil.copyObject(item, clazz);
            } else {
                return null;
            }
        }
    }

    @Override
    public <T> T brpop(String key, long timeout, TimeUnit timeUnit, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T brpopLpush(String sourceKey, String destinationKey, long timeout, TimeUnit timeUnit, Class<T> clazz) {
        return rpoplpush(sourceKey, destinationKey, clazz);
    }

    @Override
    public Long llen(String key) {
        synchronized (QUEUE_LOCK_MONITOR) {
            ConcurrentLinkedQueue<Object> queue = Cacher.getInstance().getQueueMap().get(key);
            return queue == null ? 0L : (long) queue.size();
        }
    }

    @Override
    public Long lrem(String key, long count, Object item) {
        synchronized (QUEUE_LOCK_MONITOR) {
            ConcurrentLinkedQueue<Object> queue = Cacher.getInstance().getQueueMap().get(key);
            if (queue == null || item == null) {
                return 0L;
            }
            long removed = 0L;
            if (count == 0) {
                while (queue.remove(item)) {
                    removed++;
                }
                return removed;
            }
            long limit = Math.abs(count);
            for (long i = 0; i < limit; i++) {
                if (!queue.remove(item)) {
                    break;
                }
                removed++;
            }
            return removed;
        }
    }

    @Override
    public <T> T rpoplpush(String sourceKey, String destinationKey, Class<T> clazz) {
        synchronized (QUEUE_LOCK_MONITOR) {
            ConcurrentLinkedQueue<Object> source = Cacher.getInstance().getQueueMap().get(sourceKey);
            if (source == null) {
                return null;
            }
            Object item = source.poll();
            if (item == null) {
                return null;
            }
            ConcurrentLinkedQueue<Object> dest = Cacher.getInstance().getQueueMap().get(destinationKey);
            if (dest == null) {
                dest = new ConcurrentLinkedQueue<>();
                Cacher.getInstance().getQueueMap().put(destinationKey, dest);
            }
            dest.offer(item);
            if (String.class.isAssignableFrom(clazz)) {
                return (T) (item instanceof String ? item : JsonUtil.toJson(item));
            }
            return JsonUtil.copyObject(item, clazz);
        }
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        synchronized (QUEUE_LOCK_MONITOR) {
            ConcurrentLinkedQueue<Object> queue = Cacher.getInstance().getQueueMap().get(key);
            if (queue == null || queue.isEmpty()) {
                return Collections.emptyList();
            }
            List<Object> all = Arrays.asList(queue.toArray());
            int from = (int) Math.max(0, start);
            int toExclusive = end < 0 ? all.size() : (int) Math.min(all.size(), end + 1);
            if (from >= toExclusive) {
                return Collections.emptyList();
            }
            List<String> result = new ArrayList<>();
            for (Object item : all.subList(from, toExclusive)) {
                result.add(item instanceof String ? (String) item : JsonUtil.toJson(item));
            }
            return result;
        }
    }

    @Override
    public Boolean clearQueue(String key) {
        synchronized (QUEUE_LOCK_MONITOR) {
            if (Cacher.getInstance().getQueueMap().containsKey(key)) {
                return Objects.nonNull(Cacher.getInstance().getQueueMap().remove(key));
            } else {
                return true;
            }
        }
    }

    @Override
    public <T> List<T> getAllQueueData(String key, Class<T> clazz) {
        ConcurrentLinkedQueue<Object> queue;
        synchronized (QUEUE_LOCK_MONITOR) {
            if (Cacher.getInstance().getQueueMap().containsKey(key)) {
                queue = Cacher.getInstance().getQueueMap().get(key);
                List<Object> objects = Arrays.asList(queue.toArray());
                if (CollectionUtil.isEmpty(objects)) {
                    return null;
                }
                return JsonUtil.copyList(objects, clazz);
            }
            return null;
        }
    }

    @Override
    public Boolean zadd(String key, Double score, Object value) {
        return null;
    }

    @Override
    public Set<String> zRangeByScore(String key, Double min, Double max) {
        return null;
    }

    @Override
    public Set<String> zrevRangeByScore(String key, Double min, Double max) {
        return null;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, Long start, Long end) {
        return null;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zrevRangeByScoreWithScores(String key, Double min, Double max) {
        return null;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, Double min, Double max) {
        return null;
    }

    @Override
    public Long zremRangeByRank(String key, Long start, Long end) {
        return null;
    }

    @Override
    public Long zremRangeByScore(String key, Double min, Double max) {
        return null;
    }

    @Override
    public Set<String> zrevRange(String key, Long start, Long end) {
        return null;
    }

    @Override
    public Long zcard(String key) {
        return null;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end) {
        return null;
    }

    @Override
    public Set<String> keysWithPattern(String keyPre) {
        return null;
    }
}
