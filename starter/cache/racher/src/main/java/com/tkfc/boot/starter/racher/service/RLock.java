package com.tkfc.boot.starter.racher.service;

import com.tkfc.cache.base.interfaces.ILock;

import java.util.concurrent.TimeUnit;

/**
 * 内存锁
 *
 * @author 0neBean
 * @since 2022-08-23 22:53:05
 */
public final class RLock implements ILock {

    private final org.redisson.api.RLock lock;

    RLock(org.redisson.api.RLock lock) {
        this.lock = lock;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unLock() {
        lock.unlock();
    }

    @Override
    public Boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public Boolean tryLock(long timeout) throws InterruptedException {
        return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Integer getHoldCount() {
        return lock.getHoldCount();
    }

    @Override
    public Boolean isLocked() {
        return lock.isLocked();
    }

    @Override
    public Boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }
}
