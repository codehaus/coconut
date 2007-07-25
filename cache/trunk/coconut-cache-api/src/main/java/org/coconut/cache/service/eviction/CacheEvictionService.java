/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import org.coconut.cache.CacheServices;

/**
 * This interface contains various eviction-based methods that are available at runtime.
 * <p>
 * An instance of this interface can be retrieved either by using the cache instance to
 * look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = anCache;
 * 
 * CacheEvictionService&lt;?, ?&gt; ces = c.getService(CacheEvictionService.class);
 * </pre>
 * 
 * Or using {@link CacheServices} to look it up
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = anCache;
 * 
 * CacheEvictionService&lt;?, ?&gt; ces = CacheServices.eviction(c);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public interface CacheEvictionService<K, V> {
    
    // TODO copy javadoc from MXBean
    void trimToSize(int size);

    void trimToCapacity(long capacity);

    long getMaximumCapacity();

    int getMaximumSize();

    void setMaximumCapacity(long maximumCapacity);

    /**
     * Sets that maximum number of elements that the cache is allowed to contain. If the
     * limit is reached the cache must evict existing elements before adding new elements.
     * <p>
     * To indicate that a cache can hold an unlimited number of items,
     * {@link Integer#MAX_VALUE} should be specified. This is also refered to as an
     * unlimited cache.
     * <p>
     * If the specified maximum capacity is 0, the cache will never store any elements
     * internally. This can sometimes be useful while testing.
     * 
     * @param maximumSize
     *            the maximum number of elements the cache can hold or Integer.MAX_VALUE
     *            if there is no limit
     * @throws IllegalArgumentException
     *             if the specified maximum size is negative
     * @throws UnsupportedOperationException
     *             if the cache does not support changing the maximum size at runtime
     */
    void setMaximumSize(int maximumSize);
}
