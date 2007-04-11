/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.util.ExtendableFutureTask;
import org.coconut.cache.service.loading.LoadRequest;
import org.coconut.core.AttributeMap;
import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEntryLoader<K, V> extends ExtendableFutureTask<V> implements
        InternalLoadRequest<K, V> {
    private final AttributeMap attributes;

    private final Cache<K, V> cache;

    private final K key;

    private final EventProcessor<? super LoadRequest<K, V>> req;

    /**
     * @param loader
     * @param key
     * @param callback
     */
    DefaultCacheEntryLoader(EventProcessor<? super LoadRequest<K, V>> eh,
            Cache<K, V> cache, final K key, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        this.req = eh;
        this.key = key;
        this.cache = cache;
        this.attributes = attributes;
    }

    /**
     * @see org.coconut.cache.internal.spi.ExtendedExecutorRunnable.LoadKey#getAttributeMap()
     */
    public AttributeMap getAttributes() {
        return attributes;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.BulkCacheLoader.LoadRequest#getCache()
     */
    public Cache<K, V> getCache() {
        return cache;
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader.LoadKeyRunnable#getKey()
     */
    public K getKey() {
        return key;
    }

    /**
     * @see org.coconut.cache.internal.service.util.ExtendableFutureTask#done()
     */
    @Override
    protected void done() {
        req.process(this);
    }
}
