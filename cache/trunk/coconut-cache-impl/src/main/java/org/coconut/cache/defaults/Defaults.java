package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.SynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.entry.SynchronizedEntryMap;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.SynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.eviction.UnsynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.internal.service.expiration.SynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.expiration.UnsynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.listener.DefaultCacheListener;
import org.coconut.cache.internal.service.loading.SynchronizedCacheLoaderService;
import org.coconut.cache.internal.service.loading.UnsynchronizedCacheLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.parallel.UnsynchronizedParallelCacheService;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.servicemanager.SynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.UnsynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.service.worker.SynchronizedCacheWorkerService;

final class Defaults {

    private static volatile Collection<Class<?>> SYNCHRONIZED_CACHE;

    /** The default services for this cache. */
    private static volatile Collection<Class<?>> UNSYNCHRONIZED_CACHE;

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Defaults() {}

    // /CLOVER:ON
    public static ServiceComposer sync(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        Collection<Class<?>> classes = SYNCHRONIZED_CACHE;
        if (classes == null) {
            classes = initSync();
            SYNCHRONIZED_CACHE = classes;
        }
        return ServiceComposer.compose(cache, helper, conf, classes);
    }

    public static ServiceComposer unsync(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        Collection<Class<?>> classes = UNSYNCHRONIZED_CACHE;
        if (classes == null) {
            classes = initUnsync();
            UNSYNCHRONIZED_CACHE = classes;
        }
        return ServiceComposer.compose(cache, helper, conf, classes);
    }

    private static Collection<Class<?>> initSync() {
        List<Class<?>> c = new ArrayList<Class<?>>();
        c.add(DefaultCacheStatisticsService.class);
        c.add(DefaultCacheListener.class);
        c.add(SynchronizedCacheEvictionService.class);
        c.add(SynchronizedCacheExpirationService.class);
        c.add(SynchronizedCacheLoaderService.class);
        c.add(DefaultCacheManagementService.class);
        c.add(DefaultCacheEventService.class);
        c.add(SynchronizedCacheWorkerService.class);
        c.add(SynchronizedCacheServiceManager.class);
        c.add(SynchronizedEntryFactoryService.class);
        c.add(SynchronizedEntryMap.class);
        /* SynchronizedParallelCacheService.class);c.add( */
        c.add(DefaultCacheExceptionService.class);
        return c;
    }

    private static Collection<Class<?>> initUnsync() {
        List<Class<?>> c = new ArrayList<Class<?>>();
        c.add(DefaultCacheStatisticsService.class);
        c.add(DefaultCacheListener.class);
        c.add(UnsynchronizedCacheEvictionService.class);
        c.add(UnsynchronizedCacheExpirationService.class);
        c.add(UnsynchronizedCacheLoaderService.class);
        c.add(DefaultCacheEventService.class);
        c.add(UnsynchronizedParallelCacheService.class);
        c.add(UnsynchronizedCacheServiceManager.class);
        c.add(UnsynchronizedEntryFactoryService.class);
        c.add(DefaultCacheExceptionService.class);
        c.add(EntryMap.class);
        return c;
    }
}
