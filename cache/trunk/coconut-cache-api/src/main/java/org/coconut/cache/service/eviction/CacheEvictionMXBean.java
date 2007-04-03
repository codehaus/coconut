/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEvictionMXBean {
  
    void trimToSize(int size);
    
    void trimToCapacity(long capacity);
    
    /**
     * Returns the maximum allowed capacity of the cache or
     * {@link Long#MAX_VALUE} if there is no limit.
     * 
     * @return the maximum allowed capacity of the cache or Long.MAX_VALUE if
     *         there is no limit.
     * @see #setMaximumCapacity(long)
     */
    @ManagedAttribute(defaultValue = "Maximum Capacity", description = "The maximum capacity of the cache")
    long getMaximumCapacity();

    /**
     * Returns the maximum allowed size of the cache or
     * {@link Integer#MAX_VALUE} if there is no limit.
     * 
     * @return the maximum allowed size of the cache or Integer.MAX_VALUE if
     *         there is no limit.
     * @see #setMaximumSize(int)
     */
    @ManagedAttribute(defaultValue = "Maximum Size", description = "The maximum size of the cache")
    int getMaximumSize();

    /**
     * Sets that maximum capacity of the cache. This feature is only usefull if
     * per element sizing is enabled. In which case the total capacity of the
     * cache is the sum of all the elements size. If the limit is reached the
     * cache must evict existing elements before adding new elements.
     * <p>
     * To indicate that a cache can have an unlimited capacity,
     * {@link Long#MAX_VALUE} should be specified.
     * 
     * @param elements
     *            the maximum capacity.
     * @throws IllegalArgumentException
     *             if the specified maximum capacity is negative
     * @throws UnsupportedOperationException
     *             if the cache does not support changing the maximum capacity
     *             at runtime
     */
    void setMaximumCapacity(long maximumCapacity);

    /**
     * Sets that maximum number of elements that the cache is allowed to
     * contain. If the limit is reached the cache must evict existing elements
     * before adding new elements.
     * <p>
     * To indicate that a cache can hold an unlimited number of items,
     * {@link Integer#MAX_VALUE} should be specified. This is also refered to as
     * an unlimited cache.
     * <p>
     * If the specified maximum capacity is 0, the cache will never store any
     * elements internally.
     * 
     * @param maximumSize
     *            the maximum number of elements the cache can hold or
     *            Integer.MAX_VALUE if there is no limit
     * @throws IllegalArgumentException
     *             if the specified maximum size is negative
     * @throws UnsupportedOperationException
     *             if the cache does not support changing the maximum size at
     *             runtime
     */
    void setMaximumSize(int maximumSize);
}
