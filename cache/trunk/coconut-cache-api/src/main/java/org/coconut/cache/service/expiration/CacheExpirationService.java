/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.filter.Filter;

/**
 * A service used to control the expiration of objects in the cache.
 * <p>
 * Entries are checked for expiration when calling ...
 * <p>
 * Rename to CacheInvalidationService???
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExpirationService<K, V> {

    /**
     * Used in {@link #put(Object, Object, long, TimeUnit)} and
     * {@link #putAll(Map, long, TimeUnit)} to specify that an element should use the
     * default expiration time configured for the cache or never expire if no such default
     * value has been configured for the cache.
     */
    long DEFAULT_EXPIRATION = 0;

    /**
     * Used in {@link #put(Object, Object, long, TimeUnit)} and
     * {@link #putAll(Map, long, TimeUnit)} to specify that an element should never
     * expire.
     */
    long NEVER_EXPIRE = Long.MAX_VALUE;

    /**
     * Expires all the entries currently located in the cache.
     * 
     * @return the number of entries that was expired
     */
    int removeAll();

    /**
     * Attempts to expire all of the mappings for the specified collection of keys. The
     * effect of this call is equivalent to that of calling {@link #expire(Object)} on
     * this service once for each key in the specified collection. However, in some cases
     * it can be much faster to expire several cache items at once, for example, if some
     * of the values must also be expired on a remote host.
     * 
     * @param keys
     *            a collection of keys whose associated mappings are to be expired.
     * @return the number of entries that was expired
     */
    int removeAll(Collection<? extends K> keys);

    /**
     * Attempts to expire all of the mappings that match the specified filter.
     * 
     * @param filter
     *            the filter to match entries against
     * @return the number of entries that was expired
     */
    int removeAll(Filter<? extends CacheEntry<K, V>> filter);

    /**
     * Returns the default expiration time for entries. If entries never expire,
     * {@link #NEVER_EXPIRE} is returned.
     * 
     * @param unit
     *            the time unit that should be used for returning the default expiration
     * @return the default expiration time for entries, or {@link #NEVER_EXPIRE} if
     *         entries never expire
     */
    long getDefaultTimeToLive(TimeUnit unit);

    Filter<CacheEntry<K, V>> getExpirationFilter();

    /**
     * Associates the specified value with the specified key in this cache (optional
     * operation). If the cache previously contained a mapping for this key, the old value
     * is replaced by the specified value. (A cache <tt>c</tt> is said to contain a
     * mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) c.containsKey(k)} would return <tt>true</tt>.))
     * <p>
     * It is often more effective to specify a {@link CacheLoader} that implicitly loads
     * values then to explicitly add them to cache using the various <tt>put</tt> and
     * <tt>putAll</tt> methods.
     * <p>
     * If a backend store is configured for the cache. The value might be stored in this
     * store.
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @param expirationTime
     *            the time from now to when the element can be expired
     * @param unit
     *            the time unit of the timeout parameter.
     * @return previous value associated with specified key, or <tt>null</tt> if there
     *         was no mapping for key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache.
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from being
     *             stored in this cache.
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this cache.
     * @throws NullPointerException
     *             if the specified key, value or timeunit is <tt>null</tt>.
     */
    V put(K key, V value, long expirationTime, TimeUnit unit);

    /**
     * Copies all of the mappings from the specified map to this cache (optional
     * operation). The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object,long,TimeUnit) put(k, v,time,unit)} on this map once for
     * each mapping from key <tt>k</tt> to value <tt>v</tt> in the specified map.
     * 
     * @param t
     *            Mappings to be stored in this cache.
     * @param timeout
     *            the time from now to when the elements can be expired
     * @param unit
     *            the time unit of the timeout parameter.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache.
     * @throws ClassCastException
     *             if the class of a key or value in the specified map prevents it from
     *             being stored in this cache.
     * @throws IllegalArgumentException
     *             some aspect of a key or value in the specified map prevents it from
     *             being stored in this cache.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, contains <tt>null</tt> keys
     *             or values or the specified timeunit is <tt>null</tt>.
     */
    void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit);

    /**
     * Sets the default expiration time for new objetcs that are added to the cache. If no
     * default expiration time has been set, entries will never expire.
     * 
     * @param timeToLive
     *            the time from insertion to the point where the entry should expire
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToLive(TimeUnit)
     */
    void setDefaultTimeToLive(long timeToLive, TimeUnit unit);

    /**
     * Sets a filter that the cache can use to determine if a given cache entry is
     * expired.
     * 
     * @param filter
     * @see #getExpirationFilter()
     */
    void setExpirationFilter(Filter<CacheEntry<K, V>> filter);

}
