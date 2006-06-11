/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheQuery;
import org.coconut.event.bus.EventBus;
import org.coconut.filter.Filter;

/**
 * A class that can be used for easily decorating a cache with a specific
 * behaviour. For example, the following decorator makes sure that only
 * serializeable keys and values are inserted into the cache.
 * 
 * <pre>
 * public class IsSerializeable&lt;K, V&gt; extends CacheDecorator&lt;K, V&gt; {
 *     public IsSerializeable(Cache&lt;K, V&gt; cache) {
 *         super(cache);
 *     }
 * 
 *     public V put(K key, V value) {
 *         assert(key instanceof Serializable);
 *         assert(value instanceof Serializable);
 *         return super.put(key, value);
 *     }
 * 
 *     public void putAll(Map&lt;? extends K, ? extends V&gt; t) {
 *         for (Map.Entry&lt;? extends K, ? extends V&gt; entry : t.entrySet()) {
 *             assert(entry.getKey() instanceof Serializable);
 *             assert(entry.getValue() instanceof Serializable);
 *         }
 *         super.putAll(t);
 *     }
 * }
 * // also override public V put(K key, V value, long timeout, TimeUnit, unit)
 * // and putAll(Map&lt;? extends K, ? extends V&gt; t, long timeout, TimeUnit, unit)
 * </pre>
 * 
 * <p>
 * This decorator will be serializable if the specified cache is serializable.
 * <p>
 * There is currently no way to easily decorate the events that are outputted by
 * the event bus.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheDecorator<K, V> implements Cache<K, V>, Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1696549034956532235L;

    /** The cache that is being decorated. */
    protected final Cache<K, V> cache;

    /**
     * Creates a new AbstractCacheDecorator.
     * 
     * @param cache
     *            the cache to decorate
     */
    public CacheDecorator(Cache<K, V> cache) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return cache.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return cache.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    public void evict() {
        cache.evict();
    }

    /**
     * {@inheritDoc}
     */
    public V get(Object key) {
        return cache.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        return cache.getAll(keys);
    }

    /**
     * {@inheritDoc}
     */
    public EventBus<CacheEvent<K, V>> getEventBus() {
        return cache.getEventBus();
    }

    /**
     * {@inheritDoc}
     */
    public HitStat getHitStat() {
        return cache.getHitStat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return cache.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return cache.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Future<?> load(K key) {
        return cache.load(key);
    }

    /**
     * {@inheritDoc}
     */
    public Future<?> loadAll(Collection<? extends K> keys) {
        return cache.loadAll(keys);
    }

    /**
     * {@inheritDoc}
     */
    public V peek(Object key) {
        return cache.peek(key);
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value, long timeout, TimeUnit unit) {
        return cache.put(key, value, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
        cache.putAll(t, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        cache.putAll(t);
    }

    /**
     * {@inheritDoc}
     */
    public V putIfAbsent(K key, V value) {
        return cache.putIfAbsent(key, value);
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return cache.remove(key, value);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return cache.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean replace(K key, V oldValue, V newValue) {
        return cache.replace(key, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     */
    public V replace(K key, V value) {
        return cache.replace(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public void resetStatistics() {
        cache.resetStatistics();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return cache.size();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return cache.values();
    }

    /**
     * {@inheritDoc}
     */
    public ReadWriteLock getLock(K... keys) {
        return cache.getLock(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return cache.toString();
    }

    /**
     * {@inheritDoc}
     */
    public CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter) {
        return cache.query(filter);
    }

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public CacheEntry<K, V> getEntry(K key) {
        return cache.getEntry(key);
    }
}
