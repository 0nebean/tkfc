package com.tkfc.boot.starter.cacher.task;

import com.tkfc.boot.starter.cacher.cache.Cacher;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.TimerTask;

/**
 * 过期键清理任务
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/10 17:40
 */
@Slf4j
public class KeyCleaner extends TimerTask {


    public final static String EXPIRE_TIME_SUFFIX = "_tmp";

    @Override
    public void run() {
        Cacher.getInstance().getCacheExpiredMap().forEach((k, v) -> {
            long currentTimeMillis = System.currentTimeMillis();
            if (!k.endsWith(EXPIRE_TIME_SUFFIX)) {
                Long expire = Cacher.getInstance().getCacheExpiredMap().get(k);
                Long expireTimestamp = Cacher.getInstance().getCacheExpiredMap().get(k + EXPIRE_TIME_SUFFIX);
                if (Objects.isNull(expire) || Objects.isNull(expireTimestamp)) {
                    return;
                }
                if ((expire + expireTimestamp) <= currentTimeMillis) {
                    Cacher.getInstance().getCacheMap().remove(k);
                    Cacher.getInstance().getCacheExpiredMap().remove(k);
                    Cacher.getInstance().getCacheExpiredMap().remove(k + EXPIRE_TIME_SUFFIX);
                    log.debug("KeyCleaner clean key = {}", k);
                }
            }
        });
    }
}
