/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.core.Clock;
import org.coconut.event.EventBus;

/**
 * This class provides a skeletal implementation of the <tt>Cache</tt>
 * interface, to minimize the effort required to implement the Cache interface.
 * <p>
 * This class also contains various lifecycle methods such as
 * {@link AbstractCache#start()} and {@link AbstractCache#shutdown()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCache<K, V> extends AbstractMap<K, V> implements
        Cache<K, V> {

    public static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

    public static void checkMapForNulls(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry == null) {
                throw new NullPointerException("map contains a null entry");
            } else if (entry.getKey() == null) {
                throw new NullPointerException("map contains a null key");
            } else if (entry.getValue() == null) {
                throw new NullPointerException("map contains a null value for key = "
                        + entry.getKey());
            }
        }
    }

    private final Clock clock;

    private final CacheConfiguration<K, V> conf;

    private final CacheErrorHandler<K, V> errorHandler;

    /** A UUID used to uniquely distinguish this cache */
    private final String name;

    @SuppressWarnings("unchecked")
    public AbstractCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public AbstractCache(CacheConfiguration<K, V> configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        }
        errorHandler = configuration.getErrorHandler();
        name = configuration.getName() == null ? UUID.randomUUID().toString()
                : configuration.getName();
        this.clock = configuration.getClock();
        this.conf = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return peek((K) key) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        for (V entry : values()) {
            if (value.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void evict() {
        // evict is ignored for default implementation
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        CacheEntry<K, V> e = getEntry((K) key);
        return e == null ? null : e.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        checkCollectionForNulls(keys);
        return getAll0(keys);
    }

    public CacheConfiguration<K, V> getConfiguration() {
        // unmodifiable...
        return conf;
    }

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public CacheEntry<K, V> getEntry(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * The default implementation throws {@link UnsupportedOperationException}
     * 
     * @see org.coconut.cache.Cache#getHitStat()
     */
    public EventBus<CacheEvent<K, V>> getEventBus() {
        throw new UnsupportedOperationException(
                "getEventBus() not supported for Cache of type " + getClass());
    }

    /**
     * The default implementation does not keep statistics about the cache
     * usage.
     * 
     * @see org.coconut.cache.Cache#getHitStat()
     */
    public Cache.HitStat getHitStat() {
        return CacheUtil.STAT00;
    }

    public CacheMXBean getInfo() {
        throw new UnsupportedOperationException(
                "This cache does not support jmx monitoring");
    }

    public ReadWriteLock getLock(K... keys) {
        throw new UnsupportedOperationException(
                "locking not supported for Cache of type " + getClass());
    }

    public String getName() {
        return name;
    }

    public void initialize() {

    }

    public EventBus jmxRegistrant() {
        return getEventBus();
    }

    /**
     * The default implementation throws {@link UnsupportedOperationException}
     * 
     * @see org.coconut.cache.Cache#loadAll(Collection)
     */
    public Future<?> loadAll(Collection<? extends K> keys) {
        // default implementation does not support loading of values
        throw new UnsupportedOperationException(
                "loadAll(Collection<? extends K> keys) not supported for Cache of type "
                        + getClass());
    }

    /**
     * The default implementation throws {@link UnsupportedOperationException}
     * 
     * @see org.coconut.cache.Cache#load(Object)
     */
    public Future<?> load(K key) {
        // default implementation does not support loading of values
        throw new UnsupportedOperationException(
                "loadAsync(K key) not supported for Cache of type " + getClass());

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V peek(K key) {
        CacheEntry<K, V> e = peekEntry(key);
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public CacheEntry<K, V> peekEntry(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        return put0(key, value, DEFAULT_EXPIRATION);
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value, long expirationTime, TimeUnit unit) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        } else if (expirationTime < 0) {
            throw new IllegalArgumentException("timeout must not be negative, was "
                    + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        return put0(key, value, convert(expirationTime, unit));
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            throw new NullPointerException("m is null");
        }
        putAll0(m, DEFAULT_EXPIRATION);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m, long expirationTime, TimeUnit unit) {
        if (m == null) {
            throw new NullPointerException("m is null");
        } else if (expirationTime < 0) {
            throw new IllegalArgumentException("timeout must not be negative, was "
                    + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        putAll0(m, convert(expirationTime, unit));
    }

    public void putEntries(Collection<CacheEntry<K, V>> entries) {
        throw new UnsupportedOperationException();
    }

    public void putEntry(CacheEntry<K, V> entry) {
        throw new UnsupportedOperationException();
    };

    /**
     * {@inheritDoc}
     */
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (!containsKey(key)) {
            return put0(key, value, DEFAULT_EXPIRATION);
        } else {
            return get(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean remove(Object key, Object value) {
        if (get((K) key).equals(value)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (containsKey(key)) {
            return put(key, value);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null) {
            throw new NullPointerException("oldValue is null");
        } else if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }
        if (get(key).equals(oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void resetStatistics() {
        // ignore for default implementation
    }

    public void shutdown() {

    }

    public void start() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Cache Name: ");
        buf.append(getName());
        buf.append("\nCache of type: ");
        buf.append(this.getClass().getSimpleName());
        toString0(buf);
        buf.append("\nContains ");
        int elements = this.size();
        buf.append(elements);
        buf.append(" element(s)");
        buf.append(super.toString());
        return buf.toString();
    }

    public abstract void trimToSize(int newSize);

    /**
     * Default implementation use the simple get method.
     */
    protected Map<K, V> getAll0(Collection<? extends K> keys) {
        HashMap<K, V> h = new HashMap<K, V>();
        for (K key : keys) {
            V value = get(key);
            h.put(key, value); //
        }
        return h;
    }

    protected Clock getClock() {
        return clock;
    }

    protected CacheErrorHandler<K, V> getErrorHandler() {
        return errorHandler;
    }

    /**
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @param timeout
     * @param unit
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for the key.
     */
    protected abstract V put0(K key, V value, long expirationTime);

    protected abstract void putAll0(Map<? extends K, ? extends V> t, long expirationTime);

    /**
     * Subclasses can override this method to provide a custom toString method.
     * That will appended to the output. See the implementation details of
     * toString on AbstractCache for further details.
     * 
     * @param buf
     *            used for appending text
     */
    protected void toString0(StringBuilder buf) {

    }

    private static long convert(long timeout, TimeUnit unit) {
        if (timeout == Cache.NEVER_EXPIRE) {
            return Long.MAX_VALUE;
        } else {
            long newTime = unit.toMillis(timeout);
            if (newTime == Long.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Overflow for specified expiration time, was " + timeout + " "
                                + unit);
            }
            return newTime;
        }
    }
}

// /**
// * Returns a unique id for this cache.
// */
// public UUID getID() {
// return id;
// }
//
// /**
// * This can be overridden to provide custom handling for cases where the
// * cache is unable to find a mapping for a given key. This can be used,
// for
// * example, to provide a failfast behaviour if the cache is supposed to
// * contain a value for any given key.
// *
// * <pre>
// * public class MyCacheImpl&lt;K, V&gt; extends AbstractCache&lt;K, V&gt;
// {
// * protected V handleNullGet(K key) {
// * throw new CacheRuntimeException(&quot;No value defined for Key
// [key=&quot; + key + &quot;]&quot;);
// * }
// * }
// * </pre>
// *
// * @param key
// * the key for which no value could be found
// * @return <tt>null</tt> or any value that should be used instead
// */
// protected V handleNullGet(K key) {
// return null; // by default just return null
// }
