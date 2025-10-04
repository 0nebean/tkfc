package com.tkfc.cache.base.interfaces;

import java.util.Map;

/**
 * 初始化缓存回调
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 10:22
 */
public interface CacheInitListener {

    Map<String, String> invoke();
}
