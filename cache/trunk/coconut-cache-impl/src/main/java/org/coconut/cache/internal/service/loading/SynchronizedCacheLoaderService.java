/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
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
 * @version $Id: SynchronizedCacheLoaderService.java 521 2007-12-22 19:15:11Z kasper $
 */
public class SynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {

    /** InternalCacheEntryService responsible for creating cache entries. */
    private final InternalCacheEntryService attributeFactory;

    private final ConcurrentHashMap<K, LoadableFutureTask<K, V>> futures = new ConcurrentHashMap<K, LoadableFutureTask<K, V>>();

    /** The Executor responsible for doing the actual load. */
    private final Executor loadExecutor;

    private final MemoryStore map;

    private final Cache cache;

    private final AbstractCacheServiceManager icsm;

    public SynchronizedCacheLoaderService(AbstractCacheServiceManager icsm, MemoryStore map,
            Cache cache, InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf, final CacheWorkerService threadManager,
            final InternalCache<K, V> loadSupport) {
        super(loadConf, attributeFactory, exceptionService, loadSupport);
        this.attributeFactory = attributeFactory;
        this.loadExecutor = threadManager.getExecutorService(CacheLoadingService.class);
        this.map = map;
        this.icsm = icsm;
        this.cache = cache;
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

    /** {@inheritDoc} */
    public void loadAsync(K key, AttributeMap attributes) {
        loadExecutor.execute(createFuture(key, attributes));
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        LoadableFutureTask<K, V> ft = createFuture(key, attributes);
        ft.run();
        return ft.getBlocking();
    }

    /** {@inheritDoc} */
    @Override
    void doLoad(K key, AttributeMap attributes) {
        boolean doLoad = false;

        synchronized (cache) {
            if (icsm.lazyStart(false)) {
                doLoad=false;
                //doLoad = map.needsLoad(key);
            }
        }
        if (doLoad) {
            forceLoad(key, attributes);
        }
    }

    /** {@inheritDoc} */
    @Override
    void doLoadAll(Map<? extends K, ? extends AttributeMap> attributes) {
        Map<K, AttributeMap> keys = new HashMap<K, AttributeMap>();

        synchronized (cache) {
            if (!icsm.lazyStart(false)) {
                return;
            }
            //map.needsLoad(keys, attributes);
        }

        forceLoadAll(keys);
    }

    /** {@inheritDoc} */
    @Override
    void loadAll(AttributeMap attributes, boolean force) {
        final Map<K, AttributeMap> keys;
        synchronized (this) {
            if (!icsm.lazyStart(false)) {
                return;
            }
            if (force) {
                keys = Attributes.toMap(new ArrayList(map.keySet()), attributes);
            } else {
                keys =null;
                //keys = map.whoNeedsLoading(attributes);
            }
        }

        forceLoadAll(keys);
    }

    public Map<K, V> loadBlockingAll(Map<? extends K, ? extends AttributeMap> keys) {
        HashMap<K, V> map = new HashMap<K, V>();
        for (Map.Entry<? extends K, ? extends AttributeMap> e : keys.entrySet()) {
            CacheEntry<K, V> ce = loadBlocking(e.getKey(), e.getValue());
            if (ce != null) {
                map.put(e.getKey(), ce.getValue());
            }
        }
        return map;
    }

    private LoadableFutureTask<K, V> createFuture(K key, AttributeMap attributes) {
        LoadableFutureTask<K, V> future = futures.get(key);
        if (future == null) {
            // no load in progress, create new Future for load of key
            AttributeMap map = attributeFactory.createMap(attributes);
            LoadableFutureTask<K, V> newFuture = new LoadableFutureTask<K, V>(this, key, map);
            future = futures.putIfAbsent(key, newFuture);
            // another thread might have created a future in the mean time
            if (future == null) {
                future = newFuture;
            }
        }
        return future;
    }
}
