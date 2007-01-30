/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.util.Map;

import org.coconut.cache.policy.CostSizeObject;

/**
 * A <tt>CacheEntry</tt> describes a value-key mapping much like
 * {@link java.util.Map.Entry}. However, this interface extends it with
 * information about creation time, access patterns, size and cost.
 * <p>
 * Unless otherwise specified a cache entry obtained from a cache is always an
 * immmutable copy of the existing entry. If the value for a given key is
 * updated while another thread holds a cache entry for the key. It will not be
 * reflected in calls to any of the methods on the cache entry. This is done in
 * order to make sure that all cache entry attributes are consistent.
 * <p>
 * A call to {@link #setValue(Object)} will only update the value of the entry
 * if the version of the cache entry matches the current version of the entry in
 * the cache. For example, if one threads acquires a cache entry with
 * <tt>version = 1</tt> for a key <tt>K</tt>. Another thread then updates
 * the value for <tt>K</tt> resulting in a version bumb to 2. If the first
 * thread now attempts to call {@link #setValue(Object)} on the cache entry
 * (version=1) it will fail. This failure is indicated by returning
 * <tt>null</tt> from setValue instead of returning the existing value.
 * <p>
 * The notion of time, unless otherwise specified by the implementation, is
 * relative to the Unix epoch (on January 1, 1970, 00:00:00 GMT).
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheEntry<K, V> extends Map.Entry<K, V>, CostSizeObject {

    /**
     * Returns the time of creation for the specific cache entry in
     * milliseconds.
     * <p>
     * See the description of the class Date for a discussion of slight
     * discrepancies that may arise between "computer time" and coordinated
     * universal time (UTC).
     * 
     * @return the difference, measured in milliseconds, between the time at
     *         which this entry was created and January 1, 1970 UTC.
     * @see java.util.Date
     */
    long getCreationTime();

    /**
     * Returns the time at which the current value of the cache entry will
     * expire. Whether or not expired entries are served is determined by the
     * configuration of the cache. {@link Long#MAX_VALUE} is returned if the
     * entry will never expire.
     * 
     * @return the difference, measured in milliseconds, between the time at
     *         which the current value of the cache entry will expire and
     *         January 1, 1970 UTC.
     * @see CacheConfiguration.ExpirationStrategy
     */
    long getExpirationTime();

    /**
     * Returns the number of accesses to the value of this entry. Updating the
     * value of an entry does not influence the number of hits.
     */
    long getHits();

    /**
     * Returns the time at which the specific cache entry was last accessed in
     * milliseconds (optional operation). If the value has never been requested,
     * for example, if the entry has been added to the cache due to a call on
     * {@link Cache#load(Object)} this method returns <tt>0</tt>.
     * 
     * @return the difference, measured in milliseconds, between the time at
     *         which the entry was last accessed and January 1, 1970 UTC. Or
     *         <tt>0</tt> if it has never been accessed
     */
    long getLastAccessTime();

    /**
     * Returns when the value of the specific cache entry was last updated in
     * milliseconds (optional operation).
     * 
     * @return the difference, measured in milliseconds, between the time at
     *         which the entry was last updated and January 1, 1970 UTC.
     */
    long getLastUpdateTime();

    /**
     * Returns a version counter. An implementation may use timestamps for this
     * or an incrementing number. Timestamps usually have issues with
     * granularity and are harder to use across clusteres or threads, so an
     * incrementing counter is often safer.
     * 
     * @return the version of the current entry
     */
    long getVersion();
}