package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface InternalCacheWorkerService {

    ExecutorService getExecutorService(Class<?> service);

    ScheduledExecutorService getScheduledExecutorService(Class<?> service);
}
