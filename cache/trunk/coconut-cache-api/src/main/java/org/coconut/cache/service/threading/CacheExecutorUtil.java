package org.coconut.cache.service.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheExecutorUtil {

    public static CacheThreadManager sharedExecutorFactory() {
        return null;
    }

    public static class DefaultCacheExceptionHandler<K, V> extends CacheServiceThreadManager {

        Class service;

        DefaultCacheExceptionHandler(Class service) {
            this.service = service;
        }
        /**
         * @see org.coconut.cache.service.threading.CacheServiceThreadManager#createExecutorService()
         */
        @Override
        public ExecutorService createExecutorService() {
            return Executors.newSingleThreadExecutor(new DefaultThreadFactory(service
                    .getSimpleName()));
        }

        @Override
        public ScheduledExecutorService createScheduledExecutorService() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void executeDedicated(Runnable r) {
            Executors.newSingleThreadExecutor(
                    new DefaultThreadFactory(service.getSimpleName())).execute(r);
        }
    }

    static class DefaultThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);

        final ThreadGroup group;

        final AtomicInteger threadNumber = new AtomicInteger(1);

        final String namePrefix;

        DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = name + "-" + poolNumber.getAndIncrement();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
