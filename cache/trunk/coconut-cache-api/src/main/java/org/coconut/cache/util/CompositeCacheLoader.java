/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * A composite cache loader used for allowing multiple loaders to load a value.
 * The composite loader is constructed using either an array or list of cache
 * loaders. When attempting to load a value through on of the load methods in
 * this class. This loader will first attempt to load the values through the
 * specified cache loader with the array index 0 or the first in the specified
 * list. If this loader return <tt>null</tt> for the given key-value mapping
 * the next loader in the array/list is asked to load the value. This keeps
 * repeating until the last loader has tried loading the value for the key. If
 * this loader also returns <tt>null</tt> this loader will also return
 * <tt>null</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 * @url $URL:
 *      https://svn.codehaus.org/coconut/trunk/coconut-cache/coconut-cache-api/src/main/java/org/coconut/cache/util/CompositeCacheLoader.java $
 */
public class CompositeCacheLoader<K, V> implements CacheLoader<K, V>, Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 4594083777845671521L;

    /**
     * The array of loaders used for loading the value.
     */
    private final CacheLoader<K, V>[] loaders;

    /**
     * Creates a new composite cache loader.
     * 
     * @param loaders
     *            the cache loaders used for loading values.
     * @throws NullPointerException
     *             if the specified array of cache loaders is <tt>null</tt> or
     *             if one of the cache loaders in the array is <tt>null</tt>
     */
    public CompositeCacheLoader(CacheLoader<K, V>... loaders) {
        this(Arrays.asList(loaders));
    }

    /**
     * Creates a new composite cache loader.
     * 
     * @param loaders
     *            a list of the cache loaders used for loading values.
     * @throws NullPointerException
     *             if the specified list of cache loaders is <tt>null</tt> or
     *             if the list contains a <tt>null</tt> element
     */
    @SuppressWarnings("unchecked")
    public CompositeCacheLoader(List<? extends CacheLoader<K, V>> loaders) {
        this.loaders = loaders.toArray(new CacheLoader[0]);
        for (int i = 0; i < this.loaders.length; i++) {
            if (this.loaders[i] == null)
                throw new NullPointerException(
                        "The array or list contained a null on index=" + i);
        }
    }

    /**
     * {@inheritDoc}
     */
    public V load(final K key) throws Exception {
        V v = null;
        for (CacheLoader<K, V> loader : loaders) {
            try {
                v = loader.load(key);
            } catch (Exception e) {
                v = loadFailed(loader, key, e);
            }
            if (v != null) {
                break;
            }
        }
        if (v == null) {
            return noValueFoundForKey(key);
        } else {
            return v;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<K, V> loadAll(final Collection<? extends K> keys) throws Exception {
        final HashMap<K, V> result = new HashMap<K, V>(keys.size());
        Collection<K> ks = new HashSet<K>(keys);
        for (CacheLoader<K, V> loader : loaders) {
            Map<K, V> map = null;
            if (ks.size() > 0) {
                try {
                    map = loader.loadAll(ks);
                } catch (Exception e) {
                    map = loadAllFailed(loader, keys, e);
                }
            } else {
                // no keys left that we haven't found a value for
                break;
            }
            // map is not null part of the cache loader contract
            // assert (map != null);
            ks = new HashSet<K>();
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
        for (K key : ks) {
            result.put(key, noValueFoundForKey(key));
        }
        return result;
    }

    /**
     * Returns the list of loaders that this composite loader consists of.
     * 
     * @return the list of loaders that this composite loader consists of.
     */
    public List<CacheLoader<K, V>> getLoaders() {
        return Arrays.asList(loaders);
    }

    /**
     * This method handles failures of {@link CacheLoader#loadAll} method.
     * Override to provide customized handling the default version just makes
     * sure the original exception is thrown.
     * <p>
     * If this method returns a map it <tt>must</tt> provide a mapping for all
     * the specified keys. Either to a value or to <tt>null</tt>. Also the
     * size of the map must be the same as the size of key collection.
     * 
     * @param loader
     *            the cache loader that threw an exception
     * @param keys
     *            the keys the cache loader was trying to load values from
     * @param cause
     *            the exception that was thrown by the cache loader
     * @return a map containing the mapping that should be used instead
     * @throws Exception
     */
    protected Map<K, V> loadAllFailed(CacheLoader<K, V> loader,
            Collection<? extends K> keys, Exception cause) throws Exception {
        throw cause;
    }

    /**
     * @param loader
     *            the cache loader that threw an excepti
     * @param key
     *            the key the loader was trying to retrieve a value for
     * @param cause
     *            the exception that was thrown by the cache loader
     * @return the value for this key
     * @throws Exception
     */
    protected V loadFailed(CacheLoader<K, V> loader, K key, Exception cause)
            throws Exception {
        throw cause;
    }

    /**
     * This method decides which value if returned for keys whose values could
     * not be found in any of the specified loaders. The default value returned
     * is <tt>null</tt>. Can be overridden, for example, to return a default
     * value for non existing key->value mappings or throw an exception
     * indicating an illegal state.
     * <p>
     * 
     * @param key
     *            the key for whose value could not be loaded
     * @return the value that should be mapped to the key
     */
    protected V noValueFoundForKey(K key) throws Exception {
        return null;
    }
}
