package com.tkfc.cache.base.interfaces;

import java.util.concurrent.TimeUnit;

/**
 * 锁对象
 *
 * @author 0neBean
 * @since 2022-08-23 22:36:41
 */
public interface ILock {

    void lock();

    void lockInterruptibly() throws InterruptedException;

    void unLock();

    Boolean tryLock();

    Boolean tryLock(long timeout) throws InterruptedException;

    Integer getHoldCount();

    Boolean isLocked();

    Boolean isHeldByCurrentThread();
}
