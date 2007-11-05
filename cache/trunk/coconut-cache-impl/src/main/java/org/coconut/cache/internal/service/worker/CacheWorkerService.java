package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.cache.service.worker.CacheWorkerManager;

public interface CacheWorkerService {

    ExecutorService getExecutorService(Class<?> service);

    ScheduledExecutorService getScheduledExecutorService(Class<?> service);

    CacheWorkerManager getManager();
}
