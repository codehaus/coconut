/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.cache.spi.CacheErrorHandler2;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Callback;
import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PlainValueLoaderService<K, V> {
    private final CacheErrorHandler2<K, V> errorHandler;

    private final AsyncCacheLoader<? super K, ? extends V> loader;

    PlainValueLoaderService(CacheConfiguration<K, V> conf) {
        errorHandler = (CacheErrorHandler2<K, V>) conf.getErrorHandler();
        loader = null;
    }

    public Future<?> asyncLoad(final K key, EventProcessor<V> eh) {
        Callback<V> c = LoadUtil2.valueLoadedCallback(key, eh, loader,
                errorHandler);
        return loader.asyncLoad(key, (Callback) c);
    }

    public Future<?> asyncLoad(final K key, Map<K, V> sink) {
        return asyncLoad(key, LoadUtil2.valueNonNullIntoMap(key, sink));
    }

    public Future<?> asyncLoadAll(final Collection<? extends K> keys,
            EventProcessor<Map<K, V>> eh) {
        Callback<Map<K, V>> c = LoadUtil2.valuesLoadedCallback(keys, eh,
                loader, errorHandler);
        return loader.asyncLoadAll(keys, (Callback) c);
    }

    public Future<?> asyncLoadAll(final Collection<? extends K> keys, Map<K, V> sink) {
        return asyncLoadAll(keys, LoadUtil2.valuesNonNullIntoMap(sink));
    }

    /**
     * Attempts to load and return a value from the specified cache loader. If
     * the load fails by throwing an exception. The specified CacheErrorHandler
     * will be used to handle the error.
     * 
     * @param loader
     *            the cache loader that the value should be loaded from
     * @param key
     *            the key for which value to load
     * @param errorHandler
     *            the error handler used for handling errors
     * @return the value for the given key or <tt>null</tt> if no value could
     *         be found for the specified key
     */
    public V load(final K key) {
        V v;
        try {
            v = loader.load(key);
        } catch (Exception e) {
            v = errorHandler.loadFailed2(loader,key, false, e);
        }
        return v;
    }

    public Map<K, V> loadAll(

    Collection<? extends K> keys) {
        Map<K, V> map;
        try {
            map = (Map<K, V>) loader.loadAll(keys);
        } catch (Exception e) {
            map = errorHandler.loadAllFailed2(loader, keys, false, e);
        }
        return map;
    }

}