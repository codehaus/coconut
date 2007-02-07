/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket.sandbox;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.pocket.ValueLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DoubleLayeredConcurrentMap<K, V> implements ConcurrentMap<K, V> {

    private final transient ConcurrentMap<K, Future<V>> futures = new ConcurrentHashMap<K, Future<V>>();

    /** The concurrent map that we wrap. */
    private final ConcurrentMap<K, V> map;

    /** The value loader used for constructing new values. */
    private final ValueLoader<K, V> loader;

    /**
     * Create a new OptimisticConcurrentCache.
     * 
     * @param loader
     *            the value loader used for constructing new values
     */
    public DoubleLayeredConcurrentMap(ValueLoader<K, V> loader) {
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
    public DoubleLayeredConcurrentMap(ValueLoader<K, V> loader, ConcurrentMap<K, V> map) {
        if (loader == null) {
            throw new NullPointerException("loader is null");
        } else if (map == null) {
            throw new NullPointerException("map is null");
        }
        this.map = map;
        this.loader = loader;
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
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        V v = map.get(key);
        if (v == null) {
            K k = (K) key;
            Future<V> newFuture = new FutureTask<V>(new ValueLoaderCallable(k));
            Future<V> future = futures.putIfAbsent(k, newFuture);

            // there is a small window where we might risk loading
            // a value twice. Unless we check this map again.
            // T1 -> Sees nothing
            // T1 -> puts futuretask into futures
            // T2 -> Sees nothing (V v = map.get(key);)
            // T1 -> loads value and executes map.putIfAbsent(k, v);
            // T1 -> executes futures.remove(k);
            // T2 -> sees nothing in futures
            // T2 -> loads value only to have it rejected by
            // map.putIfAbsent(k, v);

            if (future == null) {
                v = map.get(key);
                if (v != null) {
                    return v;
                }
            }

            try {
                v = future == null ? newFuture.get() : future.get();
            } catch (InterruptedException e) {
                // user interrupted call to get method
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException("Unknown cause", cause);
                }
            }
            if (future != null) { // thread that inserted the future
                map.putIfAbsent(k, v);
                futures.remove(k);
            }
        }
        return v;
    }

    class ValueLoaderCallable implements Callable<V> {
        private final K key;

        ValueLoaderCallable(K key) {
            this.key = key;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            V value = loader.load(key);
            if (value == null) {
                throw new NullPointerException(
                        "ValueLoader returned null, this map does not allow null values");
            }
            return value;
        }

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
}
