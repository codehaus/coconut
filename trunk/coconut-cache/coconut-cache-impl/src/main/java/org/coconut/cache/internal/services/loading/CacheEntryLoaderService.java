/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Caches;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.core.Callback;
import org.coconut.core.EventProcessor;
import org.coconut.core.util.ThreadUtils;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEntryLoaderService<K, V> extends AbstractCacheService<K, V> implements
        CacheLoader<K, CacheEntry<K, V>> {

    private final CacheErrorHandler<K, V> errorHandler;

    private final AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

    private boolean canLoadAsync = false;

    public CacheEntryLoaderService(CacheConfiguration<K, V> conf) {
        super(conf);
        errorHandler = conf.getErrorHandler();
        Executor e = conf.threading().getExecutor();
        if (e == null) {
            e = ThreadUtils.SAME_THREAD_EXECUTOR;
        }
        canLoadAsync = e != null;
        loader = LoadUtil.wrapAsAsync(getLoader(conf), e);
    }

    public boolean canLoadAsync() {
        return canLoadAsync;
    }

    public Future<?> asyncLoadAllEntries(final Collection<? extends K> keys,
            AbstractCache<K, V> cache) {
        return asyncLoadAllEntries(keys, LoadUtil.entriesNonNullIntoAbstractCache(cache));
    }

    public Future<?> asyncLoadAllEntries(final Collection<? extends K> keys,
            EventProcessor<Map<K, CacheEntry<K, V>>> eh) {
        if (!canLoadAsync()) {
            throw new UnsupportedOperationException(
                    "No Executor was defined in CacheConfiguration, cannot load entries asynchronously without an executor");
        }
        Callback c = LoadUtil.entriesLoadedCallback(keys, eh, loader, errorHandler);
        return loader.asyncLoadAll(keys, c);
    }

    public Future<?> asyncLoadEntry(final K key, AbstractCache<K, V> sink) {
        return asyncLoadEntry(key, LoadUtil.entryNonNullIntoAbstractCache(sink));
    }

    public Future<?> asyncLoadEntry(final K key, EventProcessor<CacheEntry<K, V>> eh) {
        if (!canLoadAsync()) {
            throw new UnsupportedOperationException(
                    "No Executor was defined in CacheConfiguration, cannot load entries asynchronously without an executor");
        }
        Callback c = LoadUtil.entryLoadedCallback(key, eh, loader, errorHandler);
        return loader.asyncLoad(key, c);
    }

    public Map<K, CacheEntry<K, V>> loadAll(Collection<? extends K> keys) {
        Map<K, CacheEntry<K, V>> map;
        try {
            map = (Map<K, CacheEntry<K, V>>) loader.loadAll(keys);
        } catch (Exception e) {
            map = errorHandler.loadAllEntrisFailed(loader, keys, false, e);
        }
        return map;
    }

    public CacheEntry<K, V> load(final K key) {
        CacheEntry<K, V> entry;
        try {
            entry = (CacheEntry<K, V>) loader.load(key);
        } catch (Exception e) {
            entry = errorHandler.loadEntryFailed(loader, key, false, e);
        }
        return entry;
    }

    static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getLoader(
            CacheConfiguration<K, V> conf) {
        if (conf.backend().getBackend() != null) {
            return LoadUtil.toExtendedCacheLoader(conf.backend().getBackend());
        } else if (conf.backend().getExtendedBackend() != null) {
            return conf.backend().getExtendedBackend();
        } else {
            return Caches.nullLoader();
        }
    }
}
