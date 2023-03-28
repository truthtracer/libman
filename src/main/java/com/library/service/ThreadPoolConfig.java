//package com.library.service;
//
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import com.library.utils.FileUtils;
//
//import java.util.concurrent.*;
//
//public class ThreadPoolConfig {
//    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileUtils.class);
//
//    public static ThreadPoolExecutor createThreadPoolExecutor(final int coreSize, final int maxSize,
//                                                              final long keepAliveTime, final String namePrefix, final int queueSize, final Boolean daemon,
//                                                              final Integer priority) {
//        return new ThreadPoolExecutor(coreSize, maxSize, coreSize == maxSize ? 0 : keepAliveTime, TimeUnit.MILLISECONDS,
//                queueSize >= 1 ? new LinkedBlockingQueue<>(queueSize) : new SynchronousQueue<>(),
//                createThreadFactory(namePrefix, daemon, priority),
//                (r, executor) -> log.warn("Reject"+namePrefix));
//    }
//
//    public static ThreadPoolExecutor createThreadPoolExecutor(final String namePrefix, final int queueSize) {
//        return createThreadPoolExecutor(1, 1, 300, namePrefix, queueSize, null, null);
//    }
//    public static ThreadFactory createThreadFactory(final String namePrefix, final Boolean daemon,
//                                                    final Integer priority) {
//        final ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
//        threadFactoryBuilder.setNameFormat(namePrefix + "-%d").setDaemon(daemon)
//                .setPriority(priority == null ? Thread.NORM_PRIORITY : priority);
//        threadFactoryBuilder.setUncaughtExceptionHandler(
//                (thread, throwable) -> log.error("Thread threw an uncaught exception"));
//        return threadFactoryBuilder.build();
//    }
//}
