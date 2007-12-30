/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * A service used to control the expiration of cache elements at runtime. See the package
 * documentation for a detailed explanation of the expiration service.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheExpirationService&lt;?, ?&gt; ces = c.getService(CacheExpirationService.class);
 * ces.trimToSize(10);
 * </pre>
 *
 * Or by using {@link CacheServices}
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheExpirationService&lt;?, ?&gt; ces = CacheServices.service.expiration();
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
public interface CacheExpirationService<K, V> {

    /**
     * Returns the default time to live for entries that are added to the cache. If
     * entries do not expire by default, {@link Long#MAX_VALUE} is returned.
     *
     * @param unit
     *            the time unit that should be used for returning the default expiration
     * @return the default time to live for entries that are added to the cache, or
     *         {@link Long#MAX_VALUE} if entries do not expire by default
     */
    long getDefaultTimeToLive(TimeUnit unit);

    /**
     * Removes all expired items from the cache.
     */
    void purgeExpired();

    /**
     * Works as {@link Cache#put(Object, Object)} except that entry added will expire
     * after the specified time to live. The specified time to live will override any
     * default value returned by {@link #getDefaultTimeToLive(TimeUnit)}
     * <p>
     * If the specified timeToLive is 0 the cache will use the value of
     * {@link #getDefaultTimeToLive(TimeUnit)} to calculate the expiration time.
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @param timeToLive
     *            the time from now to when the element will expired
     * @param unit
     *            the time unit of the timeToLive parameter.
     * @return previous value associated with specified key, or <tt>null</tt> if there
     *         was no mapping for key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache.
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from being
     *             stored in this cache.
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this cache.
     * @throws NullPointerException
     *             if the specified key, value or timeunit is <tt>null</tt>.
     */
    V put(K key, V value, long timeToLive, TimeUnit unit);

    /**
     * Copies all of the mappings from the specified map to this cache (optional
     * operation). The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object,long,TimeUnit) put(k, v,time,unit)} on this map once for
     * each mapping from key <tt>k</tt> to value <tt>v</tt> in the specified map.
     * <p>
     * If the specified timeToLive is 0 the cache will use the value of
     * {@link #getDefaultTimeToLive(TimeUnit)} to calculate the expiration time.
     * @param t
     *            Mappings to be stored in this cache.
     * @param timeout
     *            the time from now to when the elements must be expired
     * @param unit
     *            the time unit of the timeout parameter.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache.
     * @throws ClassCastException
     *             if the class of a key or value in the specified map prevents it from
     *             being stored in this cache.
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws IllegalArgumentException
     *             some aspect of a key or value in the specified map prevents it from
     *             being stored in this cache.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, contains <tt>null</tt> keys
     *             or values or the specified timeunit is <tt>null</tt>.
     */
    void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit);

    /**
     * Sets the default expiration time for new elements that are added to the cache. If
     * no default expiration time has been set, entries will never expire.
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
}
