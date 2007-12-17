/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * The eviction service controls the size of the cache and what entries to evict at
 * runtime.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = c.getService(CacheEvictionService.class);
 * ces.trimToSize(10);
 * </pre>
 * 
 * Or by using {@link CacheServices}
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = CacheServices.eviction(c);
 * ces.setMaximumSize(10000);
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
     * Returns the maximum number of elements that this cache can hold. If the cache has
     * no upper limit {@link Integer#MAX_VALUE} is returned.
     * 
     * @return the maximum number of elements that this cache can hold or
     *         {@link Integer#MAX_VALUE} if no such limit exist
     * @see #setMaximumSize(int)
     * @see Cache#size()
     */
    int getMaximumSize();

    /**
     * Returns the maximum allowed volume of the cache or {@link Long#MAX_VALUE} if there
     * is no limit.
     * 
     * @return the maximum allowed volume of the cache or {@link Long#MAX_VALUE} if there
     *         is no limit.
     * @see #setMaximumVolume(long)
     * @see Cache#getVolume()
     */
    long getMaximumVolume();

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
     * Keeps evicting entries until the volume of the cache is equal to or less then the
     * specified volume. If the specified volume is greater then the current volume no
     * action is taken.
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
     * Returns whether or not caching is disabled.
     * 
     * @return <code>true</code> if caching is disabled, otherwise <code>false</code>
     */
    public boolean isDisabled();

    /**
     * Sets whether or not caching is disabled. If caching is disabled, the cache will not
     * cache any items added. This can sometimes be useful while testing.
     * <p>
     * Note: setting this value to <code>true</code> will not remove elements already
     * present in the cache
     * 
     * @param isDisabled
     *            whether or not caching is disabled
     */
    public void setDisabled(boolean isDisabled);
}
