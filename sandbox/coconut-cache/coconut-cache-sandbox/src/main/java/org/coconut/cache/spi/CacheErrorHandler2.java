/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.loading.CacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheErrorHandler2<K, V> extends CacheErrorHandler<K, V> {

    public void storeEntryFailed(CacheStore<K, CacheEntry<K, V>> store, K key,
            CacheEntry<K, V> value, boolean isAsync, Throwable cause) {
        String msg = "Failed to store value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public void backendDeleteFailed(Collection<? extends K> keys, Throwable cause) {
        String msg = "Failed to delete values for collection of keys [keys.size = "
                + keys.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    public void backendStoreFailed(Map<K, V> map, Throwable cause) {
        String msg = "Failed to store values for collection entries [size = "
                + map.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public void storeFailed(CacheStore<K, V> loader, K key, V value, boolean isAsync,
            Throwable cause) {
        checkInitialized();
        String msg = "Failed to store value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }
    

    
    public synchronized Map<K, V> loadAllFailed2(
            CacheLoader<? super K, ? extends V> loader, Collection<? extends K> keys,
            boolean isAsync, Throwable cause) {
        String msg = "Failed to load values [keys = " + keys.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }


    public synchronized V loadFailed2(CacheLoader<? super K, ? extends V> loader, K key,
            boolean isAsync, Throwable cause) {
        String msg = "Failed to load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }
}
