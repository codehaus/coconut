package org.coconut.cache.internal.service.parallel;

import org.coconut.cache.service.parallel.CacheParallelService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

public abstract class AbstractParallelCacheService extends AbstractCacheLifecycle implements
        CacheParallelService {

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheParallelService.class, ParallelUtils.wrapService(this));
    }

    @Override
    public String toString() {
        return "Parallel Service";
    }
}
