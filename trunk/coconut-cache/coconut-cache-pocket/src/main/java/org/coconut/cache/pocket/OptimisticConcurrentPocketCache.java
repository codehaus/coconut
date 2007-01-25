/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A Concurrent map supporting full concurrency of retrievals and adjustable
 * expected concurrency for updates.
 * <p>
 * This applies when constructing the Value doesn't have any irreversible side
 * effects, so it doesn't hurt to throw it away if already entered into the map.
 * When contention is expected to be rare, it is even OK if constructing a new
 * Value is expensive, since it will rarely happen. For general usage don't be
 * afraid of occasionally wasting effort especially on multiprocessors. It is
 * usually cheaper than blocking. But always verify whether this holds in your
 * application.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OptimisticConcurrentPocketCache<K, V> implements PocketCache<K, V>,
        Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -5658657821339289321L;

    /** The concurrent map that we wrap. */
    private final ConcurrentMap<K, V> map;

    /** The value loader used for constructing new values. */
    private final ValueLoader<K, V> loader;

    private final AtomicLong hits = new AtomicLong();

    private final AtomicLong misses = new AtomicLong();

    private final int maxSize = 100;

    private final int pruneSize = 3; // items to prune on evict

    private final int hardLimit = 300;

    private final int softLimit = 100;

    /**
     * Create a new OptimisticConcurrentCache.
     * 
     * @param loader
     *            the value loader used for constructing new values
     */
    public OptimisticConcurrentPocketCache(ValueLoader<K, V> loader) {
        this(loader, new ConcurrentHashMap<K, V>());
    }

    /**
     * Constructs a new OptimisticConcurrentCache using the specified value
     * loader and concurrent map.
     * 
     * @param loader
     *            the value loader used for constructing new values
     * @param map
     *            the concurrent map to wrap
     */
    public OptimisticConcurrentPocketCache(ValueLoader<K, V> loader,
            ConcurrentMap<K, V> map) {
        if (loader == null) {
            throw new NullPointerException("loader is null");
        } else if (map == null) {
            throw new NullPointerException("map is null");
        }
        this.map = map;
        this.loader = loader;
    }

    public void resetStatistics() {
        hits.set(0);
        misses.set(0);
    }

    public long getHits() {
        return hits.get();
    }

    public long getMisses() {
        return misses.get();
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getHitRatio()
     */
    public double getHitRatio() {
        return PocketCaches.getCacheRatio(hits.get(), misses.get());
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return map.hashCode();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        map.putAll(t);
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return map.values();
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key) {
        return get(key, false);
    }

    public V peek(Object key) {
        return get(key, true);
    }

    private V get(Object key, boolean peek) {
        V v = map.get(key);
        if (v == null) {
            K k = (K) key;
            V newValue = loader.load(k);
            v = map.putIfAbsent(k, newValue);
            if (v != null) {
                if (!peek) {
                    misses.incrementAndGet();
                }
                // we lost race to other thread, undo effects
                undoCallingNewValue(loader, k, v, newValue);
            } else {
                if (!peek) {
                    hits.incrementAndGet();
                }
                v = newValue;
            }
        }
        return v;
    }

    public void evict() {
        int evictEntries = map.size() - maxSize;
        if (evictEntries > 0) {
            evictEntries(evictEntries);
        }
    }

    protected Collection<V> evictEntries(int number) {
        ArrayList<V> al = new ArrayList<V>();

        return al;

    }

    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return map.equals(o);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return map.toString();
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return map.remove(key);
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * This method is invoked whenever a value is constructed that is never
     * inserted into the map. This might be used, for example, for closing a
     * connection.
     * 
     * @param loader
     *            the value loader used constructing the value
     * @param key
     *            the key for which the value was constructed
     * @param value
     *            the value that is associated to the key
     * @param discardedValue
     *            the value that was constructed but discarded because a mapping
     *            already existed for the key
     */
    protected void undoCallingNewValue(ValueLoader<K, V> loader, K key, V value,
            V discardedValue) {

    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getAll(java.util.Collection)
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getCapacity()
     */
    public int getCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getEvictWatermark()
     */
    public int getEvictWatermark() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getNumberOfHits()
     */
    public long getNumberOfHits() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getNumberOfMisses()
     */
    public long getNumberOfMisses() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#setCapacity(int)
     */
    public void setCapacity(int limit) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#setEvictWatermark(int)
     */
    public void setEvictWatermark(int trimSize) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#trimToSize(int)
     */
    public void trimToSize(int newSize) {
        // TODO Auto-generated method stub
        
    }
}
