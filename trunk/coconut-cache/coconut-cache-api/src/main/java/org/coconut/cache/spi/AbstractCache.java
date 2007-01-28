/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
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
        name = configuration.getName();
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

    /**
     * {@inheritDoc}
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        CacheUtil.checkCollectionForNulls(keys);
        return getAll0(keys);
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

    /**
     * Returns the name of this cache.
     */
    public String getName() {
        return name;
    }

    /**
     * The default implementation throws {@link UnsupportedOperationException}
     * 
     * @see org.coconut.cache.Cache#load(Object)
     */
    public Future<?> load(K key) {
        throw new UnsupportedOperationException(
                "loadAsync(K key) not supported for Cache of type " + getClass());

    }

    /**
     * The default implementation throws {@link UnsupportedOperationException}
     * 
     * @see org.coconut.cache.Cache#loadAll(Collection)
     */
    public Future<?> loadAll(Collection<? extends K> keys) {
        throw new UnsupportedOperationException(
                "loadAll(Collection<? extends K> keys) not supported for Cache of type "
                        + getClass());
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value) {
        return put(key, value, DEFAULT_EXPIRATION, TimeUnit.SECONDS);
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
            throw new IllegalArgumentException(
                    "timeout must be a non-negative number, was " + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        return put(key, value, convert(expirationTime, unit));
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        putAll(m, DEFAULT_EXPIRATION,TimeUnit.SECONDS);
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
        putAll(m, convert(expirationTime, unit));
    }

    public void putEntries(Collection<CacheEntry<K, V>> entries) {
        throw new UnsupportedOperationException();
    }

    public void putEntry(CacheEntry<K, V> entry) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (!containsKey(key)) {
            return put(key, value, DEFAULT_EXPIRATION);
        } else {
            return get(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean remove(Object key, Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        V v = peek((K) key);
        if (v != null && v.equals(value)) {
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

    /**
     * Returns the Clock defined for this cache.
     */
    protected Clock getClock() {
        return clock;
    }

    /**
     * Returns the CacheErrorHandler defined for this cache.
     */
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
    protected abstract V put(K key, V value, long expirationTimeNano);

    protected abstract void putAll(Map<? extends K, ? extends V> t, long expirationTimeNano);

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
}