package org.coconut.cache.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.coconut.cache.store.OldCacheStore;


/**
 * An abstract implementation of a {@link org.coconut.cache.OldCacheStore}.
 * Override this class if you only want to override a small number of methods.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class AbstractCacheStore<K, V> extends
        AbstractCacheLoader<K, V> implements OldCacheStore<K, V> {

    /**
     * @see org.coconut.cache.OldCacheStore#eraseAll()
     */
    public void eraseAll() throws Exception {
        for (K k : keys()) {
            erase(k);
        }
    }

    /**
     * @see coconut.cache.CacheStore#eraseAll(null)
     */
    public void eraseAll(Collection<? extends K> colKeys) throws Exception {
        for (K k : colKeys) {
            erase(k);
        }
    }

    /**
     * @see org.coconut.cache.OldCacheStore#keys()
     */
    public Iterable<K> keys() throws Exception {
        final Iterator<Map.Entry<? extends K, ? extends V>> i = AbstractCacheStore.this
                .entries().iterator();
        return new Iterable<K>() {
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public K next() {
                        return i.next().getKey();
                    }

                    public void remove() {
                        i.remove();
                    }
                };
            }
        };
    }

    /**
     * @see org.coconut.cache.OldCacheStore#loadAll()
     */
    public Map<K, V> loadAll() throws Exception {
        Map<K, V> map = new HashMap<K, V>();
        for (Map.Entry<? extends K, ? extends V> entry : entries()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * @see org.coconut.cache.OldCacheStore#size()
     */
    public int size() throws Exception {
        return loadAll().size();
    }

    /**
     * @see coconut.cache.CacheStore#storeAll(null)
     */
    public void storeAll(Map<? extends K, ? extends V> mapEntries)
            throws Exception {
        for (Map.Entry<? extends K, ? extends V> entry : mapEntries.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }

    }

    /**
     * @see org.coconut.cache.OldCacheStore#values()
     */
    public Iterable<V> values() throws Exception {
        final Iterator<Map.Entry<? extends K, ? extends V>> i = AbstractCacheStore.this
                .entries().iterator();
        return new Iterable<V>() {
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public V next() {
                        return i.next().getValue();
                    }

                    public void remove() {
                        i.remove();
                    }
                };
            }
        };
    }
}