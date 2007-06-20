/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;


/**
 * The management interface for the eviction engine of a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEvictionMXBean {

    /**
     * Keeps evicting entries until the size of the cache is equal to the specified size.
     * If the specified size is greater then the current size of the cache no action is
     * taken.
     * 
     * @param size
     *            the number of elements to trim the cache down to
     * @throws IllegalArgumentException
     *             if the specified size is negative
     */
    void trimToSize(int size);

    /**
     * Keeps evicting entries until the capacity of the cache is equal to the specified
     * capacity. If the specified capacity is greater then the current capacity no action
     * is taken.
     * 
     * @param capacity
     *            the capacity to trim the cache down to
     * @throws IllegalArgumentException
     *             if the specified capacity is negative
     */
    void trimToCapacity(long capacity);

    /**
     * Returns the maximum allowed capacity of the cache or {@link Long#MAX_VALUE} if
     * there is no limit.
     * 
     * @return the maximum allowed capacity of the cache or Long.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumCapacity(long)
     */
    long getMaximumCapacity();

    /**
     * Returns the maximum number of elements that this cache can hold. If the cache has
     * no upper limit {@link Integer#MAX_VALUE} is returned.
     * 
     * @return the maximum number of elements that this cache can hold or
     *         {@link Integer#MAX_VALUE} if no such limit exist
     * @see #setMaximumSize(int)
     * @see {@link java.util.Map#size()}
     */
    int getMaximumSize();

    /**
     * Sets that maximum capacity of the cache. This feature is only usefull if per
     * element sizing is enabled. In which case the total capacity of the cache is the sum
     * of all the elements size. If the limit is reached the cache must evict existing
     * elements before adding new elements.
     * <p>
     * To indicate that a cache can have an unlimited capacity, {@link Long#MAX_VALUE}
     * should be specified.
     * 
     * @param elements
     *            the maximum capacity.
     * @throws IllegalArgumentException
     *             if the specified maximum capacity is negative
     * @throws UnsupportedOperationException
     *             if the cache does not support changing the maximum capacity at runtime
     */
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
     * internally.
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
