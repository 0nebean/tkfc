package com.tkfc.boot.starter.cacher.cache;

import com.tkfc.boot.starter.cacher.service.CLock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 缓存实现
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 10:14
 */
public class Cacher extends BaseCacher {


    protected Map<String, CLock> LOCK_MAP;
    protected Map<String, String> CACHE_MAP;
    protected Map<String, Long> CACHE_EXPIRED_MAP;
    protected Map<String, ConcurrentLinkedQueue<Object>> QUEUE_MAP;

    //单例
    private static class CacheHolder {
        private static final Cacher INSTANCE = new Cacher();
    }

    private Cacher() {
        CACHE_MAP = new ConcurrentHashMap<>();
        CACHE_EXPIRED_MAP = new ConcurrentHashMap<>();
        LOCK_MAP = new ConcurrentHashMap<>();
        QUEUE_MAP = new ConcurrentHashMap<>();
    }

    public static Cacher getInstance() {
        return CacheHolder.INSTANCE;
    }

    @Override
    public Map<String, String> getCacheMap() {
        return CACHE_MAP;
    }

    @Override
    public Map<String, Long> getCacheExpiredMap() {
        return CACHE_EXPIRED_MAP;
    }

    @Override
    public Map<String, CLock> getLockMap() {
        return LOCK_MAP;
    }

    @Override
    public Map<String, ConcurrentLinkedQueue<Object>> getQueueMap() {
        return QUEUE_MAP;
    }
}
