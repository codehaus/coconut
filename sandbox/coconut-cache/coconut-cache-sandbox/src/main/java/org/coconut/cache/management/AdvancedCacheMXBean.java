/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.management;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AdvancedCacheMXBean extends CacheMXBean {

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
    
    /* <!-- Expirimental --> */
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
     * Implementations may choose to include the overhead of the cache into the
     * returned result.
     * 
     * @return the total amount of memory currently used by the cache
     */
    long getMemoryUsage();

    long getMaximumMemoryUsage();

    void setMaximumMemoryUsage(long maxMemory);
    
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
