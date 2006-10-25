/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheService;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.store.CacheStore;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StoreSupport {

    public static class EntrySupport<K, V> extends AbstractCacheService<K, V> {
        private final CacheStore<K, CacheEntry<K, V>> store;

        private final CacheErrorHandler<K, V> errorHandler;

        private final boolean retrievePrevious = false;

        public EntrySupport(CacheConfiguration<K, V> conf) {
            super(conf);
            errorHandler = conf.getErrorHandler();
            store = (CacheStore<K, CacheEntry<K, V>>) conf.backend().getStore();
        }

        public CacheEntry<K, V> storeEntry(final CacheEntry<K, V> value) {
            CacheEntry<K, V> v;
            if (store != null) {
                K key = value.getKey();
                try {
                    v = store.store(key, value, retrievePrevious);
                } catch (Exception e) {
                    v = errorHandler.storeEntryFailed(store, key, value, false, e);
                }
                return v;
            }
            return null;
        }
    }

    public static class ValueSupport<K, V> {
        private final CacheStore<K, V> store;

        private final CacheErrorHandler<K, V> errorHandler;

        private final boolean retrievePrevious = false;

        public ValueSupport(CacheConfiguration<K, V> conf) {
            errorHandler = conf.getErrorHandler();
            store = conf.backend().getStore();
        }

        public V store(final K key, final V value) {
            V v;
            try {
                v = store.store(key, value, retrievePrevious);
            } catch (Exception e) {
                v = errorHandler.storeFailed(store, key, value, false, e);
            }
            return v;
        }
    }

}
