package com.tkfc.cache.base.abstracts;

import com.tkfc.cache.base.interfaces.CacheInitListener;
import com.tkfc.cache.base.interfaces.ICacheService;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 缓存接口定义
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/7 17:42
 */
public abstract class AbstractCache implements ICacheService {

    //初始化回调实现
    private CacheInitListener initCacheListener;

    @Override
    public void setInitCacheListener(CacheInitListener initCacheListener) {
        Assert.isNull(this.initCacheListener, "CacheInitListener only can init once !");
        this.initCacheListener = initCacheListener;
        Map<String, String> invoke = initCacheListener.invoke();
        loadAll(invoke);
    }

    @Override
    public CacheInitListener getInitCacheListener() {
        return initCacheListener;
    }


}
