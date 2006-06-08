/* Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheException;
import org.coconut.cache.spi.LoadableCache;
import org.coconut.cache.store.OldCacheStore;


public abstract class StoragableCache<K, V> extends LoadableCache<K, V> {

    private OldCacheStore<K, V> store;
   

    /**
     * @param key the key that failed to load
     * @param cause
     */
    protected void storeFailed(K key, Throwable cause) {
        throw new CacheException("Failed to load value [key = " + key.toString() + "]",
            cause);
    }

    /**
     * @param key the key that failed to load
     * @param cause
     */
    protected void storeFailed(Collection< ? extends K> keys, Throwable cause) {
        throw new CacheException(
            "Failed to load values for collection of keys [keys.size = " + keys.size() + "]", cause);
    }
    
    protected Runnable prepareStore(K key, V value) {
        return new WriteSync(key,value);
    }

    protected Runnable prepareStore(K key, V value, long timeout, TimeUnit unit) {
        return prepareStore(key, value);
    }

    private class WriteSync implements Runnable {
        private final K key;
        private final V value;
        /**
         * @param key
         * @param value
         */
        public WriteSync(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public void run() {
            try {
                store.store(key, value);
            } catch (Exception e) {
                storeFailed(key, e);
            }
        }
    }
}
