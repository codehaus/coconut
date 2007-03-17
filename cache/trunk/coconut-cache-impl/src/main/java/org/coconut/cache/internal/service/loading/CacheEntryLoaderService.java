/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.internal.service.InternalCacheServiceManager;
import org.coconut.cache.internal.service.threading.InternalThreadManager;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.Loaders;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Callback;
import org.coconut.core.EventProcessor;
import org.coconut.internal.util.ThreadUtils;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEntryLoaderService<K, V> extends AbstractCacheService<K, V> implements
        CacheLoader<K, CacheEntry<K, V>> {

    private final CacheErrorHandler<K, V> errorHandler;

    private final AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

    private final InternalThreadManager threadManager;

    public CacheEntryLoaderService(InternalCacheServiceManager manager,
            CacheConfiguration<K, V> conf, InternalThreadManager threadManager) {
        super(manager, conf);
        this.errorHandler = conf.getErrorHandler();
        this.threadManager = threadManager;
        this.loader = LoadUtil.wrapAsAsync(getLoader(conf), threadManager);
    }

    public boolean canLoadAsync() {
        return threadManager.isAsync();
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
            map = errorHandler.loadAllFailed(loader, keys, false, e);
        }
        return map;
    }

    public CacheEntry<K, V> load(final K key) {
        CacheEntry<K, V> entry;
        try {
            entry = (CacheEntry<K, V>) loader.load(key);
        } catch (Exception e) {
            entry = errorHandler.loadFailed(loader, key, false, e);
        }
        return entry;
    }

    static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getLoader(
            CacheConfiguration<K, V> conf) {
        CacheLoadingConfiguration<K, V> co = conf
                .getServiceConfiguration(CacheLoadingConfiguration.class);
        if (co.getBackend() != null) {
            return LoadUtil.toExtendedCacheLoader(co.getBackend());
        } else if (co.getExtendedBackend() != null) {
            return co.getExtendedBackend();
        } else {
            return Loaders.nullLoader();
        }
    }
}
