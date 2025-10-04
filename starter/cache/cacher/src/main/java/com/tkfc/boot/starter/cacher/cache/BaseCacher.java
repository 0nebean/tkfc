package com.tkfc.boot.starter.cacher.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * 缓存接口 第二层抽象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 10:28
 */
@Slf4j
public abstract class BaseCacher implements ICacher {

    private static final long serialVersionUID = -355186648055691917L;

    @Override
    public Boolean getBoolean(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Boolean.class);
    }

    @Override
    public Integer getInteger(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Integer.class);
    }

    @Override
    public Long getLong(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Long.class);
    }

    @Override
    public Double getDouble(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Double.class);
    }

    @Override
    public String getJson(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, String.class);
    }

    @Override
    public String getString(String key) {
        return getJsonFromCache(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, BigDecimal.class);
    }


    @Override
    public Date getDate(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Date.class);
    }


    @Override
    public Timestamp getTimestamp(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, Timestamp.class);
    }


    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = getJsonFromCache(key);
        return JSON.parseArray(json, clazz);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Map<K, V> getMap(String key) {
        String json = getJsonFromCache(key);
        JSONObject jsonObject = JsonUtil.toJsonObject(json);
        return (Map<K, V>) jsonObject;
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, clazz);
    }

    @Override
    public <T> T getObject(String key, TypeReference<T> type) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, type);
    }

    public Boolean set(String key, Object value) {
        try {
            return Objects.nonNull(getCacheMap().put(key, (value instanceof String) ? value.toString() : JSON.toJSONString(value)));
        } catch (Exception e) {
            log.error("cacher set method get err = ", e);
            return false;
        }
    }

    public Boolean setIfAbsent(String key, Object value) {
        try {
            return StringUtil.isNotBlank(getCacheMap().putIfAbsent(key, JSON.toJSONString(value)));
        } catch (Exception e) {
            log.error("cacher set method get err = ", e);
            return false;
        }
    }

    public String getJsonFromCache(String key) {
        log.debug("cacher call method getJsonFromCache , key ={}", key);
        return getCacheMap().get(key);
    }

    @Override
    public Boolean del(String key) {
        try {
            return Objects.nonNull(getCacheMap().remove(key));
        } catch (Exception e) {
            log.error("cacher del method get err = ", e);
            return false;
        }
    }

    @Override
    public Boolean hDel(String key, String hashKey) {
        try {
            Object o = Optional.ofNullable(getMap(key)).map(m -> m.get(hashKey)).orElse(null);
            if (Objects.nonNull(o)) {
                Map<String, Object> map = getMap(key);
                return Objects.nonNull(map.remove(hashKey)) && set(key, map);
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("cacher del method get err = ", e);
            return false;
        }
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> values) {
        try {
            Map<String, Object> jsonMap = new HashMap<>();
            values.forEach((k, v) -> jsonMap.put(k, JSON.toJSONString(v)));
            return Objects.nonNull(getCacheMap().put(key, JSON.toJSONString(jsonMap)));
        } catch (Exception e) {
            log.error("cacher hSetAll method get err = ", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> hGetAll(String key) {
        String json = getJsonFromCache(key);
        return JsonUtil.toBean(json, new TypeReference<>() {
        });
    }
}
