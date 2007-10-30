package org.coconut.cache.internal.service.loading;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.worker.CacheWorkerService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.core.AttributeMap;

public class ThreadSafeCacheLoaderService<K, V> extends DefaultCacheLoaderService<K, V> {

    private final ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>> futures = new ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>>();

    public ThreadSafeCacheLoaderService(AbstractCacheEntryFactoryService attributeFactory,
            CacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf, CacheWorkerService threadManager,
            LoadSupport<K, V> cache) {
        super(attributeFactory, exceptionService, loadConf, threadManager, cache);
    }

    @Override
    FutureTask<AbstractCacheEntry<K, V>> createFuture(K key, AttributeMap attributes) {
        FutureTask<AbstractCacheEntry<K, V>> future = futures.get(key);
        if (future == null) {
            FutureTask<AbstractCacheEntry<K, V>> newFuture = super.createFuture(key,
                    attributes);
            future = futures.putIfAbsent(key, newFuture);
            if (future == null) {
                future = newFuture;
            }
        }
        return future;
    }

    @Override
    AbstractCacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes,
            boolean isSynchronous) {
        try {
            return super.loadAndAddToCache(key, attributes, isSynchronous);
        } finally {
            futures.remove(key);
        }
    }
}
