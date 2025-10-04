package com.tkfc.boot.starter.cacher.service;

import com.tkfc.cache.base.interfaces.ILock;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 内存锁
 *
 * @author 0neBean
 * @since 2022-08-23 22:53:05
 */
public final class CLock implements ILock {

    private final ReentrantLock lock;
    private final ConcurrentLinkedQueue<Thread> blockingThreadQueue;


    CLock() {
        this.lock = new ReentrantLock();
        this.blockingThreadQueue = new ConcurrentLinkedQueue<>();
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
        while (!blockingThreadQueue.isEmpty()) {
            LockSupport.unpark(blockingThreadQueue.poll());
        }
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
