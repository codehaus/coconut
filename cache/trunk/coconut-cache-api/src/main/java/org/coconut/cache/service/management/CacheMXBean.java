/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.management;

/**
 * The management interface for a {@link org.coconut.cache.Cache}. Some cache
 * implementations might define additional methods in addition to those defined
 * in this interface. However, all implementations that has JMX support must as
 * a minimum support this interface.
 * <p>
 * The default ObjectName a cache is registered under is
 * <code>org.coconut.cache:name=cache_name,service=General</code>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface CacheMXBean {

    /**
     * The default domain used when registering a cache.
     */
    static final String DEFAULT_JMX_DOMAIN = "org.coconut.cache";

    /**
     * Returns the current number of elements in the cache.
     * <p>
     * This method is equivalent to calling
     * {@link org.coconut.cache.Cache#size()}.
     * 
     * @return the current number of elements in the cache
     */
    int getSize();

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache
     */
    String getName();

    /**
     * Returns the number of retrievels from the cache where the element was
     * already contained in the cache at the time of retrievel.
     * <p>
     * This number is equivalent to that returned by
     * {@link org.coconut.cache.Cache#getHitStat()}.
     * 
     * @return the number of hits
     * @see #getNumberOfMisses
     * @see #resetHitStat
     */
    long getNumberOfHits();

    /**
     * Returns the number of retrievels from the cache where the element was
     * <tt>not</tt> already contained in the cache at the time of retrievel.
     * <p>
     * This number is equivalent to that returned by
     * {@link org.coconut.cache.Cache#getHitStat()}.
     * 
     * @return the number of cache misses.
     */
    long getNumberOfMisses();

    /**
     * Return the ratio between hits and misses. This method will return
     * <tt> {@value java.lang.Double#NaN}</tt> if both the number of misses and
     * hits are equal to zero.
     * 
     * @return the ratio between hits and misses.
     */
    double getHitRatio();

    /**
     * Resets the hit ratio. This sets the number of cache hits and cache misses
     * to zero for the cache.
     * <p>
     * This method is equivalent to calling
     * {@link org.coconut.cache.Cache#resetStatistics()}.
     */
    void resetStatistics();

    /**
     * Clears and removes any element in the cache.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#clear()}.
     */
    void clear();

    /**
     * Evict expired items and do any necessary housekeeping.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#evict()}.
     */
    void evict();

    /**
     * Keep evicting entries (using the configured replacement policy) until the
     * number of elements in the cache has reached the specified size. If the
     * cache does not have a configured replacement policy the cache may remove
     * the elements in any order.
     * 
     * @param newSize
     *            the number of elements that the cache should hold
     */
    void trimToSize(int newSize);
}
