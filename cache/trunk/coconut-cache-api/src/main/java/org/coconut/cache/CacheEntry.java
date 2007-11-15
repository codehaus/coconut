/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Map;

import org.coconut.core.Clock;

/**
 * A <tt>CacheEntry</tt> describes a value-key mapping much like
 * {@link java.util.Map.Entry}. However, this interface extends it with information about
 * creation time, access patterns, size and cost.
 * <p>
 * Unless otherwise specified a cache entry obtained from a cache is always an immmutable
 * copy of the existing entry. If the value for a given key is updated while another
 * thread holds a cache entry for the key. It will not be reflected in calls to
 * {@link #getValue()}.
 * <p>
 * The notion of time, unless otherwise specified by the implementation, is relative to
 * the Unix epoch (on January 1, 1970, 00:00:00 GMT).
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @see Clock#timestamp()
 * @see CacheConfiguration#setClock(Clock)
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public interface CacheEntry<K, V> extends Map.Entry<K, V> {
    /**
     * Returns the value corresponding to this entry.
     * 
     * @return the value corresponding to this entry
     */
    V getValue();

    /**
     * Returns the expected cost of fetching this element. Assigning costs to an element
     * is context dependent. Examples include
     * <ul>
     * <li>Time duration for loading the value from disk.</li>
     * <li>Price for transfering x number of bytes from an external storage service.
     * <li>Number of calculations used to create the element</li>
     * </ul>
     * <p>
     * The cost does not necessarily represent the actual time to fetch the element.
     * However, this is often the case.
     * 
     * @return the expected cost of fetching this element. If the size of the object
     *         cannot be determined {@link CacheAttributes#DEFAULT_COST} should be
     *         returned
     * @see CacheAttributes#setCost(org.coconut.core.AttributeMap, double)
     * @see CacheAttributes#getCost(org.coconut.core.AttributeMap)
     * @see CacheAttributes#DEFAULT_COST
     * @see CacheAttributes#COST
     */
    double getCost();

    /**
     * Returns the time of creation for the specific cache entry in milliseconds.
     * <p>
     * See the description of the class Date for a discussion of slight discrepancies that
     * may arise between "computer time" and coordinated universal time (UTC).
     * 
     * @return the difference, measured in milliseconds, between the time at which this
     *         entry was created and January 1, 1970 UTC.
     * @see java.util.Date
     * @see CacheAttributes#setCreationTime(org.coconut.core.AttributeMap, long)
     * @see CacheAttributes#getCreationTime(org.coconut.core.AttributeMap)
     * @see CacheAttributes#getCreationTime(org.coconut.core.AttributeMap, Clock)
     * @see CacheAttributes#CREATION_TIME
     */
    long getCreationTime();

    /**
     * Returns the time at which the current value of the cache entry will expire. Expired
     * entries are never served by the cache. If {@link Long#MAX_VALUE} is returned if the
     * entry will never expire.
     * 
     * @return the difference, measured in milliseconds, between the time at which the
     *         current value of the cache entry will expire and January 1, 1970 UTC. Or
     *         {@link Long#MAX_VALUE} if it never expires
     * @see org.coconut.cache.service.expiration.CacheExpirationService
     * @see CacheAttributes#setTimeToLive(org.coconut.core.AttributeMap, long,
     *      java.util.concurrent.TimeUnit)
     * @see CacheAttributes#getTimeToLive(org.coconut.core.AttributeMap,
     *      java.util.concurrent.TimeUnit, long)
     * @see CacheAttributes#TIME_TO_LIVE_NS
     */
    long getExpirationTime();

    /**
     * Returns the number of times the object has been previously succesfully requested.
     * 
     * @return the number of times the object has been previously succesfully requested.
     * @see CacheAttributes#setHits(org.coconut.core.AttributeMap, long)
     * @see CacheAttributes#getHits(org.coconut.core.AttributeMap)
     * @see CacheAttributes#HITS
     */
    long getHits();

    /**
     * Returns the time at which the specific cache entry was last accessed in
     * milliseconds (optional operation). If the value has never been requested, for
     * example, if the entry has been added to the cache due to a call on
     * {@link org.coconut.cache.service.loading.CacheLoadingService#load(Object)} this
     * method returns <tt>0</tt>.
     * 
     * @return the difference, measured in milliseconds, between the time at which the
     *         entry was last accessed and January 1, 1970 UTC. Or <tt>0</tt> if it has
     *         never been accessed
     */
    long getLastAccessTime();

    /**
     * Returns when the value of the specific cache entry was last updated in milliseconds
     * (optional operation).
     * 
     * @return the difference, measured in milliseconds, between the time at which the
     *         entry was last updated and January 1, 1970 UTC.
     * @see CacheAttributes#setLastUpdated(org.coconut.core.AttributeMap, long)
     * @see CacheAttributes#getLastUpdated(org.coconut.core.AttributeMap, Clock)
     * @see CacheAttributes#LAST_UPDATED_TIME
     */
    long getLastUpdateTime();

    /**
     * Returns the size of this element. Implementations are free to include overhead of
     * storing the element or just the size of the element itself.
     * <p>
     * The size returned does not necessarily represent the actual number of bytes used to
     * store the element.
     * 
     * @return the size of the element. If the size of the object cannot be determined
     *         {@link CacheAttributes#DEFAULT_SIZE} should be returned
     * @see CacheAttributes#setSize(org.coconut.core.AttributeMap, long)
     * @see CacheAttributes#getSize(org.coconut.core.AttributeMap)
     * @see CacheAttributes#SIZE
     */
    long getSize();
}
