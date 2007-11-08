package org.coconut.cache.internal.service.loading;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.worker.CacheWorkerService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;

public class SynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {

    private final InternalCacheEntryService attributeFactory;

    private final ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>> futures = new ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>>();

    private final Executor loadExecutor;

    public SynchronizedCacheLoaderService(InternalCacheEntryService attributeFactory,
            CacheExceptionService<K, V> exceptionService, CacheLoadingConfiguration<K, V> loadConf,
            final CacheWorkerService threadManager, final LoadSupport<K, V> cache) {
        super(loadConf, attributeFactory, exceptionService, cache);
        this.attributeFactory = attributeFactory;
        this.loadExecutor = threadManager.getExecutorService(CacheLoadingService.class);
    }

    /** {@inheritDoc} */
    @Override
    public void loadAsync(K key, AttributeMap attributes) {
        loadExecutor.execute(createFuture(key, attributes));
    }
    
    /** {@inheritDoc} */
    public AbstractCacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        FutureTask<AbstractCacheEntry<K, V>> ft = createFuture(key, attributes);
        ft.run();
        try {
            return ft.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            if ((e.getCause() instanceof RuntimeException)) {
                throw (RuntimeException) e.getCause();
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    private FutureTask<AbstractCacheEntry<K, V>> createFuture(K key, AttributeMap attributes) {
        FutureTask<AbstractCacheEntry<K, V>> future = futures.get(key);
        if (future == null) {
            AttributeMap map = attributeFactory.createMap(attributes);
            Callable<AbstractCacheEntry<K, V>> r = LoadingUtils.loadValue(this, key, map);
            FutureTask<AbstractCacheEntry<K, V>> newFuture = new FutureTask<AbstractCacheEntry<K, V>>(
                    r);
            future = futures.putIfAbsent(key, newFuture);
            if (future == null) {
                future = newFuture;
            }
        }
        return future;
    }

    @Override
    AbstractCacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes, boolean isSynchronous) {
        try {
            return super.loadAndAddToCache(key, attributes, isSynchronous);
        } finally {
            futures.remove(key);
        }
    }
}
