/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.store.CacheStore;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StoreSupport {
    public static <K, V> V store(final CacheStore<K, V> store, final K key,
            final V value, boolean retrievePrevious,
            final CacheErrorHandler<K, V> errorHandler) {
        V v;
        try {
            v = store.store(key, value, retrievePrevious);
        } catch (Exception e) {
            v = errorHandler.storeFailed(store, key, value, false, e);
        }
        return v;
    }
    
    public static <K, V> CacheEntry<K, V> storeEntry(final CacheStore<K, CacheEntry<K,V>> store, final K key,
            final CacheEntry<K,V> value, boolean retrievePrevious,
            final CacheErrorHandler<K, V> errorHandler) {
        CacheEntry<K,V> v;
        try {
            v = store.store(key, value, retrievePrevious);
        } catch (Exception e) {
            v = errorHandler.storeEntryFailed(store, key, value, false, e);
        }
        return v;
    }
}
