package org.coconut.cache.test.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class ThreadServiceTestHelper extends CacheWorkerManager {

    private final static int PERMITS = 100000;

    private Semaphore awaits = new Semaphore(PERMITS);

    public ExecutorService createExecutorService() {
        return null;
    }

    public ScheduledExecutorService createScheduledExecutorService() {
        return null;
    }

    public void executeDedicated(Runnable r) {}

    public void awaitAllIdle() throws InterruptedException {
        awaits.acquire(PERMITS);
        awaits.release(PERMITS);
    }

    private class MyRunnable implements Runnable {
        public void run() {
            try {
                awaits.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1); // should never happen
            } finally {
                awaits.release();
            }
        }

    }

    @Override
    public ExecutorService getExecutorService(Class<?> service, AttributeMap attributes) {
        return null;
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
            AttributeMap attributes) {
        return null;
    }
}
