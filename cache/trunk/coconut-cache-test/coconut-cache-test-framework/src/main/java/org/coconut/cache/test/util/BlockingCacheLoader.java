package org.coconut.cache.test.util;

import java.util.Collection;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoaderCallback;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;

public class BlockingCacheLoader<K, V> extends AbstractCacheLifecycle implements CacheLoader<K, V> {

    public V load(K key, AttributeMap attributes) throws Exception {
        //delay
        //data generator
        return null;
    }
    
    /** {@inheritDoc} */
    public final void loadAll(
            Collection<? extends CacheLoaderCallback<? extends K, ? super V>> loadCallbacks) {
        for (CacheLoaderCallback<? extends K, ? super V> req : loadCallbacks) {
            try {
                V result = load(req.getKey(), req.getAttributes());
                req.completed(result);
            } catch (Throwable t) {
                req.failed(t);
            }
        }
    }

}
