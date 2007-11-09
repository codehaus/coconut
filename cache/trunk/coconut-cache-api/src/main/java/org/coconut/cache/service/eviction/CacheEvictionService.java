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
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public interface CacheEvictionService<K, V> {

    /**
     * Keeps evicting entries until the size of the cache is equal to the specified size.
     * If the specified size is greater then the current size of the cache no action is
     * taken.
     * <p>
     * If the cache has been shutdown calls to this method is ignored.
     * 
     * @param size
     *            the number of elements to trim the cache down to
     * @throws IllegalArgumentException
     *             if the specified size is negative
     */
    void trimToSize(int size);

    /**
     * Keeps evicting entries until the volume of the cache is equal to the specified
     * volume. If the specified volume is greater then the current volume no action is
     * taken.
     * <p>
     * If the cache has been shutdown calls to this method is ignored.
     * 
     * @param volume
     *            the volume to trim the cache down to
     * @throws IllegalArgumentException
     *             if the specified volume is negative
     */
    void trimToVolume(long volume);

    /**
     * Returns the maximum allowed volume of the cache or {@link Long#MAX_VALUE} if there
     * is no limit.
     * 
     * @return the maximum allowed volume of the cache or Long.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumVolume(long)
     * @see org.coconut.cache.Cache#getVolume()
     */
    long getMaximumVolume();

    /**
     * Returns the maximum number of elements that this cache can hold. If the cache has
     * no upper limit {@link Integer#MAX_VALUE} is returned.
     * 
     * @return the maximum number of elements that this cache can hold or
     *         {@link Integer#MAX_VALUE} if no such limit exist
     * @see #setMaximumSize(int)
     * @see org.coconut.cache.Cache#size()
     */
    int getMaximumSize();

    /**
     * Sets that maximum volume of the cache. The total volume of the cache is the sum of
     * all the individual element sizes. If the limit is reached the cache must evict
     * existing elements before adding new elements.
     * <p>
     * To indicate that a cache can have an unlimited volume, {@link Long#MAX_VALUE}
     * should be specified.
     * 
     * @param maximumVolume
     *            the maximum volume.
     * @throws IllegalArgumentException
     *             if the specified maximum volume is negative
     * @throws UnsupportedOperationException
     *             if the cache does not support changing the maximum volume at runtime
     */
    void setMaximumVolume(long maximumVolume);

    /**
     * Sets that maximum number of elements that the cache is allowed to contain. If the
     * limit is reached the cache must evict existing elements before adding new elements.
     * <p>
     * To indicate that a cache can hold an unlimited number of items,
     * {@link Integer#MAX_VALUE} should be specified.
     * <p>
     * If the specified maximum size is 0, the cache will never store any elements
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
