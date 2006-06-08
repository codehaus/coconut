/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.management;

/**
 * The management interface for a {@link org.coconut.cache.Cache}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface CacheMXBean {

    /**
     * Returns the current number of elements in the cache. This method is
     * equivalent to calling {@link org.coconut.cache.Cache#size()}.
     * 
     * @return the current number of elements in the cache
     */
    int getSize();

    /**
     * Returns the maximum number of elements that this cache can hold. If the
     * cache has no upper limit {@link Integer#MAX_VALUE} is returned.
     * 
     * @return the maximum number of elements that this cache can hold.
     * @see #setMaximumSize
     * @see #getSize
     */
    int getMaximumSize();

    /**
     * Sets the maximum number of elements that this cache can hold. Unless
     * otherwise specified the cache will try to evict (remove) less accessed
     * elements when the maximum size is reached. To indicate that the cache
     * should not try to limit the number of elements pass
     * {@link Integer#MAX_VALUE} to this method.
     * 
     * @param maximumSize
     *            the maximum number of elements this cache should hold
     * @throws IllegalArgumentException
     *             if the specified maximum size is not a positive number (>0)
     */
    void setMaximumSize(int maximumSize);

    /**
     * Returns the default expiration time for new elements in nanoseconds.
     * {@link org.coconut.cache.Cache#NEVER_EXPIRE} is used to specify that
     * elements does not expire as default.
     * 
     * @return the default expiration time for new elements in nanoseconds or
     *         {@link org.coconut.cache.Cache#NEVER_EXPIRE} if elements does not
     *         expire as default
     */
    long getDefaultExpiration();

    /**
     * Sets the default expiration time for elements added to the cache. This
     * can be overridden on a per element basis by calling
     * {@link org.coconut.cache.Cache#put(Object,Object,long,java.util.concurrent.TimeUnit)}.
     * {@link org.coconut.cache.Cache#NEVER_EXPIRE} should be used to specify
     * that elements does not expire as default.
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
     * Tests if the cache supports the monitoring of memory used by the cache.
     * TODO this needs to be related to size of an element in some way
     * 
     * @return true if the monitoring of used memory is supported ; false
     *         otherwise.
     */
    boolean isMemoryMonitoringSupported();

    /**
     * Returns the total amount of memory currently used by the cache, measured
     * in bytes. Or -1 if not supported.
     * <p>
     * Implementation dependent whether or not the overhead of cache is counted
     * into the returned result.
     * 
     * @return the total amount of memory currently used by the cache
     */
    long getMemoryUsage();

    long getMaximumMemoryUsage();

    void setMaximumMemoryUsage();

    /**
     * Returns the number of retrievels from the cache where the element was
     * already contained in the cache at the time of retrievel.
     * 
     * @return the number of hits
     * @see #getNumberOfMisses
     * @see #resetHitStat
     */
    long getNumberOfHits();

    /**
     * Returns the number of retrievels from the cache where the element was
     * <tt>not</tt> already contained in the cache at the time of retrievel.
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
     */
    void resetHitStat();

    /**
     * Clears and removes any element in the cache. Calling this method is
     * equivalent to calling {@link org.coconut.cache.Cache#clear()}.
     */
    void clear();

    /**
     * Evict expired items. Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#evict()}.
     */
    void evict();

    /**
     * Keep evicting entries (using the configured replacement policy) until the
     * size of the cache has reached the specified new size.
     * 
     * @param newSize
     *            the new size of the cache
     */
    void trimToSize(int newSize);

    // load remove/evict/expire som entry
    // UUID getUUID(); // getHigh / log bit

    // overhead
    // getPuts
    // getGets

    // average load delay
    // average get delay

    // total getdelay

    // String[] getIDs();

    // void load(String key);
    // void loadAll(String[] keys);
    // void remove(String key);
    // void removeAll(String[] keys);
    // CacheEntryInfo
    // -toString, creationTime,TimeToLive, Deadline (creationTime+ttl)

    // void subscribeNotifications(St)
    // void unsubscribeNotifications(St)

    // void unsubscribeAllNotifications(St)

    // long getUUIDMostSignificant();
    // long getUUIDLeastSignificant();
    // setName(), getFullname();

    // is enabled
    // set enabled
}
