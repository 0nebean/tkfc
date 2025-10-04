package com.tkfc.core.toolkit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 异步执行类
 *
 * @author 0neBean
 * @since 2022-08-17 12:03:45
 */
@Slf4j
public class AsyncUtil {


    /**
     * 最大队列长度
     */
    private static final int QUEUE_LIMIT = 200;
    private static final int CORE_POOL_SIZE = 50;
    private static final int MAX_POOL_SIZE = 200;
    private static final long KEEP_ALIVE_TIME = 60L;

    private static final BlockingQueue<Runnable> TASK_QUEUE = new LinkedBlockingQueue<>(QUEUE_LIMIT);


    /**
     * 拒绝策略 同步执行
     */
    private final static RejectedExecutionHandler THREAD_POOL_EXEC_FAILURE_CALL_BACK = (runnable, threadPoolExecutor) -> {
        log.warn("AsyncUtil Thread pool is full, task will be rejected. Pool size = {}, Active threads = {}, Queue size = {}", threadPoolExecutor.getPoolSize(), threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
    };

    /**
     * 额定线程数线程池
     */
    private static final ThreadPoolExecutor ASYNC_TASK_THREAD_POOL;

    /**
     * 线程工厂类
     */
    private static final ThreadFactory THREAD_FACTORY = r -> {
        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler((t, e) -> log.error("async thread catch an error , e = ", e));
        return thread;
    };


    /*
       初始化线程数的线程池
      */
    static {
        ASYNC_TASK_THREAD_POOL = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                TASK_QUEUE,
                THREAD_FACTORY,
                THREAD_POOL_EXEC_FAILURE_CALL_BACK
        );
    }

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
        ASYNC_TASK_THREAD_POOL.execute(new Thread(asyncTask));
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
            log.error("when thread sleep got interrupted exception , e = ", e);
        }
    }

}
