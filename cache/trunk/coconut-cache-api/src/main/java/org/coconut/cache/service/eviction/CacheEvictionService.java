/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

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
 */
public interface CacheEvictionService<K, V> {

    /**
     * <p>
     * If this is an in-memory-only cache calling this method has the same effect as
     * calling {@link java.util.Map#remove(Object)} on the cache.
     * 
     * @param the
     *            key whose mapping is to be evicted from the cache
     */
    void evict(K key);

    /**
     * @param keys
     *            the keys whose mappings are to be evicted from the cache
     */
    void evictAll(Collection<? extends K> keys);

    /**
     * Returns the default expiration time for entries. If entries never expire,
     * {@link #NEVER_EXPIRE} is returned.
     * 
     * @param unit
     *            the time unit that should be used for returning the default expiration
     * @return the default expiration time for entries, or {@link #NEVER_EXPIRE} if
     *         entries never expire
     */
    long getDefaultIdleTime(TimeUnit unit);

    /**
     * Sets the default time idle time for new objects that are added to the cache.
     * 
     * @param timeToLive
     *            the time from insertion to the point where the entry should be evicted
     *            from the cache
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToLive(TimeUnit)
     */
    void setDefaultIdleTime(long idleTime, TimeUnit unit);

    /**
     * Checks all elements in the cache for whether or not they are idle and should be
     * evicted.
     */
    void evictIdleElements();

    // TODO copy javadoc from MXBean
    void trimToSize(int size);

    void trimToCapacity(long capacity);

    long getMaximumCapacity();

    int getMaximumSize();

    void setMaximumCapacity(long maximumCapacity);

    void setMaximumSize(int maximumSize);
}
