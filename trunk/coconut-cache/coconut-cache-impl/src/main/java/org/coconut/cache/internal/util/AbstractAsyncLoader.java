/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractAsyncLoader<K, V> implements AsyncCacheLoader<K, V> {
    /**
     * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
     */
    public V load(K key) throws Exception {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        SingleKeyCallback<V> skc = new SingleKeyCallback<V>();
        try {
            asyncLoad(key, skc).get();
        } catch (ExecutionException ee) {
            Throwable t = ee.getCause();
            if (t instanceof Exception) {
                throw (Exception) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new IllegalStateException("Unknown throwable", t);
            }
        }
        return skc.result;
    }

    /**
     * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
     */
    public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        SingleKeyCallback<Map<K, V>> skc = new SingleKeyCallback<Map<K, V>>();
        try {
            asyncLoadAll(keys, skc).get();
        } catch (ExecutionException ee) {
            Throwable t = ee.getCause();
            if (t instanceof Exception) {
                throw (Exception) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new IllegalStateException("Unknown throwable", t);
            }
        }
        return skc.result;
    }

    static class SingleKeyCallback<V> implements Callback<V> {

        private V result;

        V getResult() {
            return result;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(V result) {
            this.result = result;
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            // already handled
        }
    }
}
