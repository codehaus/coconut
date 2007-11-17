package org.coconut.cache.service.worker;

import java.util.concurrent.ScheduledExecutorService;

public interface CacheWorkerService {
    ScheduledExecutorService getScheduledExecutorService(Object service);
}
