package com.tkfc.cache.base.interfaces;

import java.util.Map;

/**
 * 顶级抽象缓存接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 23:33
 */
public interface ICache {


    /**
     * 设置初始化监听
     *
     * @param initCacheListener 监听
     */
    void setInitCacheListener(CacheInitListener initCacheListener);

    /**
     * 获取监听器
     *
     * @return 监听器
     */
    CacheInitListener getInitCacheListener();

    /**
     * 装在缓存
     *
     * @param addOn 装载项
     */
    void loadAll(Map<String, String> addOn);


}
