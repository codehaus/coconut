/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.management;

/**
 * The management interface for a {@link org.coconut.cache.Cache}. Some cache
 * implementations might define additional methods in addition to those defined
 * in this interface. However, all implementations that has JMX support must as
 * a minimum support this interface.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface CacheMXBean {

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
     * Returns the maximum number of elements that this cache can hold. If the
     * cache has no upper limit {@link Integer#MAX_VALUE} is returned.
     * 
     * @return the maximum number of elements that this cache can hold or
     *         {@link Integer#MAX_VALUE} if no such limit exist.
     * @see #setMaximumSize
     * @see #getSize
     */
    int getMaximumSize();

    /**
     * Sets the maximum number of elements that this cache can hold. Unless
     * otherwise specified the cache will try to evict (remove) less accessed
     * elements when the maximum size is reached.
     * <p>
     * To indicate that the cache should not try to limit the number of elements
     * pass {@link Integer#MAX_VALUE} to this method.
     * 
     * @param maximumSize
     *            the maximum number of elements this cache should hold
     * @throws IllegalArgumentException
     *             if the specified maximum size is not a positive number (>0)
     */
    void setMaximumSize(int maximumSize);

    /**
     * Returns the default expiration time for new elements in nanoseconds.
     * {@link org.coconut.cache.Cache#NEVER_EXPIRE} is returned if elements does
     * not expire as default.
     * 
     * @return the default expiration time for new elements in nanoseconds or
     *         {@link org.coconut.cache.Cache#NEVER_EXPIRE} if elements does not
     *         expire as default
     */
    long getDefaultExpiration();

    /**
     * Sets the default expiration time for elements added to the cache. This
     * can be overridden on a per element basis by calling
     * {@link org.coconut.cache.Cache#put(Object, Object, long, java.util.concurrent.TimeUnit)}.
     * {@link org.coconut.cache.Cache#NEVER_EXPIRE} can be used to specify that
     * elements should never expire as default.
     * 
     * @param nanos
     *            the default expiration time in nanoseconds
     * @throws IllegalArgumentException
     *             if the specified default expiration time is not a positive
     *             number (>0)
     */
    void setDefaultExpiration(long nanos);

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
     * <tt>-1</tt> if both the number of misses and hits are equal to zero.
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
     * Evict expired items.
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
