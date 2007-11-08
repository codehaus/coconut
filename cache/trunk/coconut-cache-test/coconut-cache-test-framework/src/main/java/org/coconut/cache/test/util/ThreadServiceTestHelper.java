package org.coconut.cache.test.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class ThreadServiceTestHelper extends CacheWorkerManager {

    private final static int PERMITS = 100000;

    private Semaphore awaits = new Semaphore(PERMITS);


    public void awaitAllIdle() {
        awaits.acquireUninterruptibly(PERMITS);
        awaits.release(PERMITS);
    }

    private class MyRunnable implements Runnable {
        private final Runnable r;

        public MyRunnable(Runnable r) {
            this.r = r;
        }

        public void run() {
            try {
                r.run();
            } finally {
                awaits.release();
            }
        }

    }

    @Override
    public ExecutorService getExecutorService(Class<?> service, AttributeMap attributes) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>())
        {
            @Override
            public void execute(Runnable command) {
                awaits.acquireUninterruptibly();
                super.execute(new MyRunnable(command));
            }
        };
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
            AttributeMap attributes) {
        throw new UnsupportedOperationException();
    }

}
