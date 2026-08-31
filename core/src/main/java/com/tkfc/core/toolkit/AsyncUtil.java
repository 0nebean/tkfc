package com.tkfc.core.toolkit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 异步执行类
 *
 * @author 0neBean
 * @since 2022-08-17 12:03:45
 */
@Slf4j
public class AsyncUtil {


    /**
     * 线程工厂类
     */
    private static final ThreadFactory THREAD_FACTORY = Thread.ofVirtual()
            .name("tkfc-async-vt-", 0)
            .uncaughtExceptionHandler((t, e) -> log.error("async virtual thread catch an error , e = ", e))
            .factory();

    /**
     * 虚拟线程执行器：每个任务一个虚拟线程
     */
    private static final ExecutorService ASYNC_TASK_EXECUTOR = Executors.newThreadPerTaskExecutor(THREAD_FACTORY);

    /**
     * 获取线程工厂类
     *
     * @return ThreadFactory
     */
    public static ThreadFactory getThreadFactory() {
        return THREAD_FACTORY;
    }

    /**
     * @param asyncTask 异步执行的代码
     */
    public static void async(Runnable asyncTask) {
        ASYNC_TASK_EXECUTOR.execute(asyncTask);
    }

    /**
     * 休眠线程
     *
     * @param millis 毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("when thread sleep got interrupted exception , e = ", e);
        }
    }

}
