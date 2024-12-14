package com.in28minutes.microservices.mlagenteval.utils;

import java.util.concurrent.*;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:35
 */
public class ThreadPoolUtils {

    private static final int CORE_POOL_SIZE = 5; // 设置核心线程数量
    private static final int MAXIMUM_POOL_SIZE = 10; // 设置最大线程数量
    private static final int KEEP_ALIVE_TIME = 60; // 空闲线程存活时间
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100); // 工作队列

    private static final ExecutorService THREAD_POOL_EXECUTOR;

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                WORK_QUEUE,
                new ThreadPoolExecutor.CallerRunsPolicy() // 当达到最大线程数时的拒绝策略
        );
    }

    public static Executor getThreadPoolExecutor() {
        return THREAD_POOL_EXECUTOR;
    }
}
