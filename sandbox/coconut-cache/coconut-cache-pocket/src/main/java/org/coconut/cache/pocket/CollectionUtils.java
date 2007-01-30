/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class CollectionUtils {

    static class SynchronizedCollection<E> implements Collection<E>, Serializable {

        /* Backing Collection. */
        final Collection<E> col;

        /* Object on which to synchronize. */
        final Object mutex;

        SynchronizedCollection(Collection<E> col) {
            if (col == null) {
                throw new NullPointerException("col is null");
            }
            this.col = col;
            mutex = this;
        }

        SynchronizedCollection(Collection<E> col, Object mutex) {
            this.col = col;
            this.mutex = mutex;
        }

        public boolean add(E o) {
            synchronized (mutex) {
                return col.add(o);
            }
        }

        public boolean addAll(Collection<? extends E> coll) {
            synchronized (mutex) {
                return col.addAll(coll);
            }
        }

        public void clear() {
            synchronized (mutex) {
                col.clear();
            }
        }

        public boolean contains(Object o) {
            synchronized (mutex) {
                return col.contains(o);
            }
        }

        public boolean containsAll(Collection<?> coll) {
            synchronized (mutex) {
                return col.containsAll(coll);
            }
        }

        public boolean isEmpty() {
            synchronized (mutex) {
                return col.isEmpty();
            }
        }

        public Iterator<E> iterator() {
            // Must be manually synched by the user!
            return col.iterator();
        }

        public boolean remove(Object o) {
            synchronized (mutex) {
                return col.remove(o);
            }
        }

        public boolean removeAll(Collection<?> coll) {
            synchronized (mutex) {
                return col.removeAll(coll);
            }
        }

        public boolean retainAll(Collection<?> coll) {
            synchronized (mutex) {
                return col.retainAll(coll);
            }
        }

        public int size() {
            synchronized (mutex) {
                return col.size();
            }
        }

        public Object[] toArray() {
            synchronized (mutex) {
                return col.toArray();
            }
        }

        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return col.toArray(a);
            }
        }

        public String toString() {
            synchronized (mutex) {
                return col.toString();
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {
                s.defaultWriteObject();
            }
        }
    }

    static class SynchronizedConcurrentMap<K, V> extends SynchronizedMap<K, V> implements
            ConcurrentMap<K, V> {

        final ConcurrentMap<K, V> cMap;

        SynchronizedConcurrentMap(ConcurrentMap<K, V> map) {
            super(map);
            this.cMap = map;
        }

        SynchronizedConcurrentMap(ConcurrentMap<K, V> map, Object mutex) {
            super(map, mutex);
            this.cMap = map;
        }

        /**
         * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
         *      java.lang.Object)
         */
        public V putIfAbsent(K key, V value) {
            synchronized (mutex) {
                return cMap.putIfAbsent(key, value);
            }
        }

        /**
         * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
         *      java.lang.Object)
         */
        public boolean remove(Object key, Object value) {
            synchronized (mutex) {
                return cMap.remove(key, value);
            }
        }

        /**
         * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
         *      java.lang.Object)
         */
        public V replace(K key, V value) {
            synchronized (mutex) {
                return cMap.replace(key, value);
            }
        }

        /**
         * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
         *      java.lang.Object, java.lang.Object)
         */
        public boolean replace(K key, V oldValue, V newValue) {
            synchronized (mutex) {
                return cMap.replace(key, oldValue, newValue);
            }
        }

    }

    static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {

        private transient Set<Map.Entry<K, V>> entrySet;

        private transient Set<K> keySet;

        private transient Collection<V> values;

        final Map<K, V> map; // Backing Map

        final Object mutex; // Object on which to synchronize

        SynchronizedMap(Map<K, V> map) {
            if (map == null) {
                throw new NullPointerException("map is null");
            }
            this.map = map;
            mutex = this;
        }

        SynchronizedMap(Map<K, V> map, Object mutex) {
            this.map = map;
            this.mutex = mutex;
        }

        public void clear() {
            synchronized (mutex) {
                map.clear();
            }
        }

        public boolean containsKey(Object key) {
            synchronized (mutex) {
                return map.containsKey(key);
            }
        }

        public boolean containsValue(Object value) {
            synchronized (mutex) {
                return map.containsValue(value);
            }
        }

        public Set<Map.Entry<K, V>> entrySet() {
            synchronized (mutex) {
                if (entrySet == null)
                    entrySet = new SynchronizedSet<Map.Entry<K, V>>(
                            (Set<Map.Entry<K, V>>) map.entrySet(), mutex);
                return entrySet;
            }
        }

        public boolean equals(Object o) {
            synchronized (mutex) {
                return map.equals(o);
            }
        }

        public V get(Object key) {
            synchronized (mutex) {
                return map.get(key);
            }
        }

        public int hashCode() {
            synchronized (mutex) {
                return map.hashCode();
            }
        }

        public boolean isEmpty() {
            synchronized (mutex) {
                return map.isEmpty();
            }
        }

        public Set<K> keySet() {
            synchronized (mutex) {
                if (keySet == null)
                    keySet = new SynchronizedSet<K>(map.keySet(), mutex);
                return keySet;
            }
        }

        public V put(K key, V value) {
            synchronized (mutex) {
                return map.put(key, value);
            }
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (mutex) {
                this.map.putAll(map);
            }
        }

        public V remove(Object key) {
            synchronized (mutex) {
                return map.remove(key);
            }
        }

        public int size() {
            synchronized (mutex) {
                return map.size();
            }
        }

        public String toString() {
            synchronized (mutex) {
                return map.toString();
            }
        }

        public Collection<V> values() {
            synchronized (mutex) {
                if (values == null)
                    values = new SynchronizedCollection<V>(map.values(), mutex);
                return values;
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {
                s.defaultWriteObject();
            }
        }
    }

    static class SynchronizedPocketCache<K, V> extends SynchronizedConcurrentMap<K, V>
            implements PocketCache<K, V> {

        final PocketCache<K, V> cache;

        SynchronizedPocketCache(PocketCache<K, V> cache) {
            super(cache);
            this.cache = cache;
        }

        SynchronizedPocketCache(PocketCache<K, V> cache, Object mutex) {
            super(cache, mutex);
            this.cache = cache;
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#evict()
         */
        public void evict() {
            synchronized (mutex) {
                cache.evict();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getAll(java.util.Collection)
         */
        public Map<K, V> getAll(Collection<? extends K> keys) {
            synchronized (mutex) {
                return cache.getAll(keys);
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getHardLimit()
         */
        public int getCapacity() {
            synchronized (mutex) {
                return cache.getCapacity();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getDefaultTrimSize()
         */
        public int getEvictWatermark() {
            synchronized (mutex) {
                return cache.getEvictWatermark();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getHitRatio()
         */
        public double getHitRatio() {
            synchronized (mutex) {
                return cache.getHitRatio();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getNumberOfHits()
         */
        public long getNumberOfHits() {
            synchronized (mutex) {
                return cache.getNumberOfHits();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#getNumberOfMisses()
         */
        public long getNumberOfMisses() {
            synchronized (mutex) {
                return cache.getNumberOfMisses();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#peek(java.lang.Object)
         */
        public V peek(Object key) {
            synchronized (mutex) {
                return cache.peek(key);
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#resetStatistics()
         */
        public void resetStatistics() {
            synchronized (mutex) {
                cache.resetStatistics();
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#setHardLimit(int)
         */
        public void setCapacity(int limit) {
            synchronized (mutex) {
                cache.setCapacity(limit);
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#setDefaultTrimSize(int)
         */
        public void setEvictWatermark(int trimSize) {
            synchronized (mutex) {
                cache.setEvictWatermark(trimSize);
            }
        }

        /**
         * @see org.coconut.cache.pocket.PocketCache#trimToSize()
         */
        public void trimToSize(int newSize) {
            synchronized (mutex) {
                cache.trimToSize(newSize);
            }
        }
    }

    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {

        SynchronizedSet(Set<E> set) {
            super(set);
        }

        SynchronizedSet(Set<E> set, Object mutex) {
            super(set, mutex);
        }

        public boolean equals(Object o) {
            synchronized (mutex) {
                return col.equals(o);
            }
        }

        public int hashCode() {
            synchronized (mutex) {
                return col.hashCode();
            }
        }
    }

    static class UnmodifyablePocketCache<K,V> implements PocketCache<K,V> {
        private final PocketCache<K,V> cache;

        /**
         * @param cache
         */
        public UnmodifyablePocketCache(final PocketCache<K, V> cache) {
            this.cache = cache;
        }

        /**
         * 
         * @see java.util.Map#clear()
         */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @return
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            return cache.containsKey(key);
        }

        /**
         * @param value
         * @return
         * @see java.util.Map#containsValue(java.lang.Object)
         */
        public boolean containsValue(Object value) {
            return cache.containsValue(value);
        }

        /**
         * @return
         * @see java.util.Map#entrySet()
         */
        public Set<Entry<K, V>> entrySet() {
            return Collections.unmodifiableSet(cache.entrySet());
        }

        /**
         * @param o
         * @return
         * @see java.util.Map#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            return cache.equals(o);
        }

        /**
         * 
         * @see org.coconut.cache.pocket.PocketCache#evict()
         */
        public void evict() {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @return
         * @see org.coconut.cache.pocket.PocketCache#get(java.lang.Object)
         */
        public V get(Object key) {
            return cache.peek(key);
        }

        /**
         * @param keys
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getAll(java.util.Collection)
         */
        public Map<K, V> getAll(Collection<? extends K> keys) {
            //TODO HMM, can't really do peek here
            return cache.getAll(keys);
        }

        /**
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getCapacity()
         */
        public int getCapacity() {
            return cache.getCapacity();
        }

        /**
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getEvictWatermark()
         */
        public int getEvictWatermark() {
            return cache.getEvictWatermark();
        }

        /**
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getHitRatio()
         */
        public double getHitRatio() {
            return cache.getHitRatio();
        }

        /**
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getNumberOfHits()
         */
        public long getNumberOfHits() {
            return cache.getNumberOfHits();
        }

        /**
         * @return
         * @see org.coconut.cache.pocket.PocketCache#getNumberOfMisses()
         */
        public long getNumberOfMisses() {
            return cache.getNumberOfMisses();
        }

        /**
         * @return
         * @see java.util.Map#hashCode()
         */
        public int hashCode() {
            return cache.hashCode();
        }

        /**
         * @return
         * @see java.util.Map#isEmpty()
         */
        public boolean isEmpty() {
            return cache.isEmpty();
        }

        /**
         * @return
         * @see java.util.Map#keySet()
         */
        public Set<K> keySet() {
            return Collections.unmodifiableSet(cache.keySet());
        }

        /**
         * @param key
         * @return
         * @see org.coconut.cache.pocket.PocketCache#peek(java.lang.Object)
         */
        public V peek(Object key) {
            return cache.peek(key);
        }

        /**
         * @param key
         * @param value
         * @return
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param m
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param value
         * @return
         * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object, java.lang.Object)
         */
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @return
         * @see java.util.Map#remove(java.lang.Object)
         */
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param value
         * @return
         * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object, java.lang.Object)
         */
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param value
         * @return
         * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object)
         */
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param oldValue
         * @param newValue
         * @return
         * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        /**
         * 
         * @see org.coconut.cache.pocket.PocketCache#resetStatistics()
         */
        public void resetStatistics() {
            throw new UnsupportedOperationException();
        }

        /**
         * @param limit
         * @see org.coconut.cache.pocket.PocketCache#setCapacity(int)
         */
        public void setCapacity(int limit) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param trimSize
         * @see org.coconut.cache.pocket.PocketCache#setEvictWatermark(int)
         */
        public void setEvictWatermark(int trimSize) {
            throw new UnsupportedOperationException();
        }

        /**
         * @return
         * @see java.util.Map#size()
         */
        public int size() {
            return cache.size();
        }

        /**
         * @param newSize
         * @see org.coconut.cache.pocket.PocketCache#trimToSize(int)
         */
        public void trimToSize(int newSize) {
            throw new UnsupportedOperationException();
        }

        /**
         * @return
         * @see java.util.Map#values()
         */
        public Collection<V> values() {
            return Collections.unmodifiableCollection(cache.values());
        }
    }
    static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

    static <K, V> ConcurrentMap<K, V> synchronizedConcurrentMap(
            ConcurrentMap<K, V> m) {
        return new SynchronizedConcurrentMap<K, V>(m);
    }

    static <K, V> ConcurrentMap<K, V> synchronizedConcurrentMap(
            ConcurrentMap<K, V> m, Object mutex) {
        return new SynchronizedConcurrentMap<K, V>(m, mutex);
    }

    static <K, V> Map<K, V> synchronizedMap(Map<K, V> m, Object mutex) {
        return new SynchronizedMap<K, V>(m, mutex);
    }

    static <K, V> PocketCache<K, V> synchronizedPocketCache(PocketCache<K, V> cache) {
        return new SynchronizedPocketCache<K, V>(cache);
    }

    static <K, V> PocketCache<K, V> synchronizedPocketCache(
            PocketCache<K, V> cache,
            Object mutex) {
        return new SynchronizedPocketCache<K, V>(cache, mutex);
    }
}
