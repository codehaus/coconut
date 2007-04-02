/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.internal.service.loading.BulkCacheLoader.LoadRequest;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEntryLoaderWithLoader<K, V> extends
        DefaultCacheEntryLoader<K, V> implements Runnable {

    private final CacheErrorHandler<K, V> errorHandler;

    private final CacheLoader<K, V> loader;

    /**
     * @param eh
     * @param cache
     * @param key
     * @param attributes
     */
    DefaultCacheEntryLoaderWithLoader(EventProcessor<? super LoadRequest<K, V>> eh,
            Cache<K, V> cache, CacheLoader<K, V> loader,
            CacheErrorHandler<K, V> errorHandler, K key, AttributeMap attributes) {
        super(eh, cache, key, attributes);
        this.loader = loader;
        this.errorHandler = errorHandler;
    }

    /**
     * @see org.coconut.cache.internal.service.util.ExtendableFutureTask#call()
     */
    @Override
    protected V call() throws Exception {
        V v = null;
        try {
            v = loader.load(getKey(), getAttributes());
        } catch (Exception e) {
            v = errorHandler.loadFailed(loader, getKey(), getAttributes(), true, e);
        }
        return v;
    }

}
