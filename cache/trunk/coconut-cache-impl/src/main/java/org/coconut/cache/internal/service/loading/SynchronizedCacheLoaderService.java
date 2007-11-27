/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.worker.InternalCacheWorkerService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;

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

    private final ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>> futures = new ConcurrentHashMap<K, FutureTask<AbstractCacheEntry<K, V>>>();

    /** The Executor responsible for doing the actual load. */
    private final Executor loadExecutor;

    public SynchronizedCacheLoaderService(InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf,
            final InternalCacheWorkerService threadManager, final LoadSupport<K, V> loadSupport) {
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

    /** {@inheritDoc} */
    @Override
    AbstractCacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes, boolean isSynchronous) {
        try {
            return super.loadAndAddToCache(key, attributes, isSynchronous);
        } finally {
            futures.remove(key);
        }
    }
}
