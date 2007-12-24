/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.worker.CacheWorkerService;

/**
 * What to do on cache shutdown.
 * <p>
 * Pending loads will be dropped
 * <p>
 * ongoing loads will not be terminated on shutdown, but will not be added to the cache
 * <p>
 * shutdownNow -> ongoing loads will be interrupted.
 *
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {

    /** InternalCacheEntryService responsible for creating cache entries. */
    private final InternalCacheEntryService attributeFactory;

    private final ConcurrentHashMap<K, LoadableFutureTask<K, V>> futures = new ConcurrentHashMap<K, LoadableFutureTask<K, V>>();

    /** The Executor responsible for doing the actual load. */
    private final Executor loadExecutor;

    public SynchronizedCacheLoaderService(InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf, final CacheWorkerService threadManager,
            final LoadSupport<K, V> loadSupport) {
        super(loadConf, attributeFactory, exceptionService, loadSupport);
        this.attributeFactory = attributeFactory;
        this.loadExecutor = threadManager.getExecutorService(CacheLoadingService.class);
    }

    /** {@inheritDoc} */
    @Override
    public void loadAsync(K key, AttributeMap attributes) {
        loadExecutor.execute(createFuture(key, attributes));
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        LoadableFutureTask<K, V> ft = createFuture(key, attributes);
        ft.run();
        return ft.getBlocking();
    }

    private LoadableFutureTask<K, V> createFuture(K key, AttributeMap attributes) {
        LoadableFutureTask<K, V> future = futures.get(key);
        if (future == null) {
            // no load in progress, create new Future for load of key
            AttributeMap map = attributeFactory.createMap(attributes);
//            Callable<CacheEntry<K, V>> r = LoadingUtils.createLoadCallable(this, key, map);
            LoadableFutureTask<K, V> newFuture = new LoadableFutureTask<K, V>(this, key, map);
            future = futures.putIfAbsent(key, newFuture);
            // another thread might have created a future in the mean time
            if (future == null) {
                future = newFuture;
            }
        }
        return future;
    }

    /** {@inheritDoc} */
    @Override
    public CacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes, boolean isSynchronous) {
        try {
            return super.loadAndAddToCache(key, attributes, isSynchronous);
        } finally {
            futures.remove(key);
        }
    }
}
