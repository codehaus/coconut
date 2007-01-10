/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.spi.service.AbstractCacheService;
import org.coconut.cache.store.CacheStore;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StoreCacheService {

    public static class EntrySupport<K, V> extends AbstractCacheService<K, V> {
        private final CacheStore<K, CacheEntry<K, V>> store;

        private final CacheStore<K, V> store2;

        private final CacheErrorHandler<K, V> errorHandler;

        public EntrySupport(CacheConfiguration<K, V> conf) {
            super(conf);
            errorHandler = conf.getErrorHandler();
            store = (CacheStore<K, CacheEntry<K, V>>) conf.backend().getExtendedBackend();
            store2 = (CacheStore<K, V>) conf.backend().getBackend();
        }

        public void storeEntry(final CacheEntry<K, V> value) {
            if (store != null) {
                K key = value.getKey();
                try {
                    store.store(key, value);
                } catch (Exception e) {
                    errorHandler.storeEntryFailed(store, key, value, false, e);
                }
            } else if (store2 != null) {
                K key = value.getKey();
                V v =value.getValue();
                try {
                    store2.store(key,v);
                } catch (Exception e) {
                    errorHandler.storeFailed(store2, key, v, false, e);
                }
            }
        }
    }

    public static class ValueSupport<K, V> extends AbstractCacheService<K, V> {
        private final CacheStore<K, V> store;

        private final CacheErrorHandler<K, V> errorHandler;

        public ValueSupport(CacheConfiguration<K, V> conf) {
            super(conf);
            errorHandler = conf.getErrorHandler();
            store = (CacheStore<K, V>) conf.backend().getBackend();
        }

        public void store(final K key, final V value) {
            try {
                store.store(key, value);
            } catch (Exception e) {
                errorHandler.storeFailed(store, key, value, false, e);
            }
        }
    }

}
