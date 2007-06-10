package org.coconut.cache.service.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public abstract class CacheServiceExecutor {
    
    /**
     * Returns a dedicated executor. A dedicated executor is used for running very long
     * runnables. For example, an Evict thread that does it own sleeping.
     * 
     * @param service
     * @return
     */
    public abstract void executeDedicated(Runnable r);

    public abstract ExecutorService createExecutorService();

    public abstract ScheduledExecutorService createScheduledExecutorService();
    
    /**
     * Called when the service has shut down.
     */
    public abstract void shutdown();
}
