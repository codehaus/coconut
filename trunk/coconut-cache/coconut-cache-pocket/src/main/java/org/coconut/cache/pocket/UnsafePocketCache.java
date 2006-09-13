/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * You should attemp to create a PocketCacheMXBean out of it.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsafePocketCache<K, V> extends AbstractMap<K, V> {

    private final HashMap<K, MyEntry<V>> map = new HashMap<K, MyEntry<V>>();

    private long hits;

    private long misses;

    /** The value loader used for constructing new values. */
    private final ValueLoader<K, V> loader;

    private Queue<K> order = new LinkedList<K>();

    public UnsafePocketCache(ValueLoader<K, V> loader) {
        if (loader == null) {
            throw new NullPointerException("loader is null");
        }
        this.loader = loader;
    }

    /**
     * ValueSet implementation.
     */
    final class ValueSet extends AbstractSet<V> {
        public void clear() {
            UnsafePocketCache.this.clear();
        }

        public boolean contains(Object value) {
            if (value == null) {
                throw new NullPointerException("value is null");
            }
            return UnsafePocketCache.this.containsValue(value);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException("c is null");
            }
            return super.containsAll(c);
        }

        public Iterator<V> iterator() {
            return null;
            // return new ValueSetIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return super.remove(o);
        }

        public int size() {
            return UnsafePocketCache.this.size();
        }
    }

    /**
     * EntrySet implementation.
     */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        public void clear() {
            UnsafePocketCache.this.clear();
        }

        public boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            MyEntry m = UnsafePocketCache.this.map.get(e.getKey());
            return m != null && m.v.equals(e.getValue());
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return null;
        }

        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            MyEntry m = UnsafePocketCache.this.map.get(e.getKey());
            if (m != null && m.v.equals(e.getValue())) {
                return UnsafePocketCache.this.remove(e.getKey()) != null;
            } else {
                return false;
            }

        }

        public int size() {
            return UnsafePocketCache.this.size();
        }
    }

    /**
     * ValueSet iterator.
     */

    static class MyEntry<V> {
        int index;

        V v;
    }

    public V peek(Object key) {
        return map.get(key).v;
    }

    public void trimToSize(int newSize) {

    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#evict()
     */
    public void evict() {
        // TODO Auto-generated method stub

    }

    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    public long getNumberOfHits() {
        return hits;
    }

    public long getNumberOfMisses() {
        return misses;
    }

    public double getHitRatio() {
        return hits == 0 && misses == 0 ? Double.NaN : (hits / (misses + hits));
    }

    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        V v = map.get(key).v;
        if (v == null) {
            misses++;
            K k = (K) key;
            v = loader.load(k);
            // check null, throw exception?
            if (v == null) {
                v = handleNullGet(k);
            }
            if (v != null) {

                // map.put(k, v);
            }
        } else {
            hits++;
        }
        return v;
    }
    /**
     * @see org.coconut.cache.pocket.PocketCache#getAll(java.util.Collection)
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, V> h = new HashMap<K, V>();
        for (K key : keys) {
            h.put(key, get(key));
        }
        return h;
    }

    /**
     * This can be overridden to provide custom handling for cases where the
     * cache is unable to find a mapping for a given key. This can be used, for
     * example, to provide a failfast behaviour if the cache is supposed to
     * contain a value for any given key.
     * 
     * <pre>
     * public class MyCacheImpl&lt;K, V&gt; extends AbstractCache&lt;K, V&gt; {
     *     protected V handleNullGet(K key) {
     *         throw new CacheRuntimeException(&quot;No value defined for Key [key=&quot; + key + &quot;]&quot;);
     *     }
     * }
     * </pre>
     * 
     * @param key
     *            the key for which no value could be found
     * @return <tt>null</tt> or any value that should be used instead
     */
    protected V handleNullGet(K key) {
        return null; // by default just return null
    }

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

}
