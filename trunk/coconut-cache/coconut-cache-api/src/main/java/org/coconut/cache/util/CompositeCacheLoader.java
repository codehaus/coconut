package org.coconut.cache.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.coconut.cache.CacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class CompositeCacheLoader<K, V> implements CacheLoader<K, V> {
    private final CacheLoader<K, V>[] loaders;

    @SuppressWarnings("unchecked")
    public CompositeCacheLoader(CacheLoader<K, V>[] loaders) {
        this.loaders = new CacheLoader[loaders.length];
        System.arraycopy(loaders, 0, this.loaders, 0, loaders.length);
        for (int i = 0; i < this.loaders.length; i++) {
            if (this.loaders[i] == null)
                throw new NullPointerException("The array or list contained a null on index=" + i);
        }
    }

    /**
     * @see coconut.cache.CacheLoader#load(null)
     */
    public V load(final K key) throws Exception {
        V v = null;
        for (CacheLoader<K, V> loader : loaders) {
            try {
                v = loader.load(key);
            } catch (Exception e) {
                v = loadingFailed(loader, key, e);
            }
        }
        if (v == null) {
            return noValueFoundForLoaders(key);
        } else {
            return v;
        }
    }

    /**
     * @see coconut.cache.CacheLoader#loadAll(null)
     */
    public Map<K, V> loadAll(final Collection<? extends K> keys) throws Exception {
        final HashMap<K, V> result = new HashMap<K, V>(keys.size());
        Collection<K> ks = new TreeSet<K>(keys);
        for (CacheLoader<K, V> loader : loaders) {
            Map<K, V> map = null;
            if (ks.size() > 0) {
                try {
                    map = loader.loadAll(ks);
                } catch (Exception e) {
                    map = loadingFailed(loader, keys, e);
                }
            } else {
                //no keys we haven't found a value for
                break;
            }
            if (map != null) {
                result.putAll(map);
                // we don't check that map.size==ks.size
                // we assume this is the case (part of the contract)
                for (Map.Entry<K, V> e : map.entrySet()) {
                    if (e.getValue() == null) {
                        ks.add(e.getKey());
                    } else {
                        result.put(e.getKey(), e.getValue());
                    }
                }
            }
        }
        for (K key : ks) {
            result.put(key, noValueFoundForLoaders(key));
        }
        return result;
    }

    protected V loadingFailed(CacheLoader<K, V> loader, K key, Exception cause) throws Exception {
        throw cause;
    }

    protected V noValueFoundForLoaders(K key) throws Exception {
        return null;
    }

    protected Map<K, V> loadingFailed(CacheLoader<K, V> loader, Collection<? extends K> keys,
            Exception cause) throws Exception {
        throw cause;
    }
}
