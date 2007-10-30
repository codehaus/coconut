/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.worker.CacheWorkerService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V>
        implements ManagedObject, CompositeService {

    private final InternalCacheAttributeService attributeFactory;

    private final Executor loadExecutor;

    public DefaultCacheLoaderService(
            InternalCacheAttributeService attributeFactory,
            CacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf,
            final CacheWorkerService threadManager, final LoadSupport<K, V> cache) {
        super(loadConf, attributeFactory, exceptionService, cache);
        this.attributeFactory = attributeFactory;
        this.loadExecutor = threadManager.getExecutorService(CacheLoadingService.class);
        attributeFactory.update().setTimeToFreshNanos(
                LoadingUtils.getInitialTimeToRefresh(loadConf));
    }


    @Override
    void doLoad(K key, AttributeMap attributes) {
        loadFuture(key, attributes);
    }

    FutureTask<AbstractCacheEntry<K, V>> loadFuture(K key, AttributeMap attributes) {
        AttributeMap map = attributeFactory.createMap(attributes);
        Callable<AbstractCacheEntry<K, V>> r = LoadingUtils.loadValue(this, key, map);
        FutureTask<AbstractCacheEntry<K, V>> ft = new FutureTask<AbstractCacheEntry<K, V>>(
                r);
        loadExecutor.execute(ft);
        return ft;
    }

    public AbstractCacheEntry<K, V> loadBlocking(K key) {
        AttributeMap map = attributeFactory.createMap();
        FutureTask<AbstractCacheEntry<K, V>> ft = loadFuture(key, map);
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
}
