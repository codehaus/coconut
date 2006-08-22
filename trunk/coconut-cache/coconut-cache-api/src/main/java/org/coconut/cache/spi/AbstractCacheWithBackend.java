/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.sandbox.store.Store;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCacheWithBackend<K, V> extends
        AbstractCache<K, V> {

    private Store<K, V> store;

    public AbstractCacheWithBackend() {
    }

    public AbstractCacheWithBackend(CacheConfiguration<K, V> configuration) {
        super(configuration);
    }

    protected boolean hasStorageSupport() {
        return store instanceof LoaderWrapper;
    }

    protected boolean isAdvancedLoader() {
        return false;
    }



    @Override
    public Future<?> load(final K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
       return store.loadAsync(key,null);
    }

    @Override
    public Future<?> loadAll(final Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        Collection<? extends K> copy = new ArrayList<K>(keys);
        checkCollectionForNulls(copy);
        return store.loadAllAsync(copy);
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected Map<K, V> backendLoadFailed(Collection<? extends K> keys,
            Throwable cause) {
        String msg = "Failed to load values for collection of keys [keys.size = "
                + keys.size() + "]";
        getLog().fatal(msg, cause);
        throw new CacheException(msg, cause);
    }

    protected void backendDeleteFailed(Collection<? extends K> keys,
            Throwable cause) {
        String msg = "Failed to delete values for collection of keys [keys.size = "
                + keys.size() + "]";
        getLog().fatal(msg, cause);
        throw new CacheException(msg, cause);
    }
    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected void backendStoreFailed(Map<K, V> map, Throwable cause) {
        String msg = "Failed to store values for collection entries [size = "
                + map.size() + "]";
        getLog().fatal(msg, cause);
        throw new CacheException(msg, cause);
    }


    static class LoaderWrapper {

    }

}
