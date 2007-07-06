/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.io.Serializable;
import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.core.AttributeMap;

/**
 * Various utilities for working with cache loaders.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheLoaders {

    /**
     * This method converts the specified cache to a cache loader. Calls to
     * {@link CacheLoader#load(Object)} will be converted to calls to
     * {@link Cache#get(Object)}. Calls to {@link CacheLoader#loadAll(Collection)} will
     * be converted to calls to {@link Cache#getAll(Collection)}
     * 
     * @param c
     *            the cache to load entries from
     * @return a cache loader that can load values from another cache
     */
    public static <K, V> CacheLoader<K, V> cacheAsCacheLoader(Cache<K, V> c) {
        return new CacheAsCacheLoader<K, V>(c);
    }

    /**
     * Returns a CacheLoader that will return <tt>null</tt> for any key when invoking
     * {@link CacheLoader#load(Object)}. The {@link CacheLoader#loadAll(Collection)}
     * method will return a map with a mapping for each key to <tt>null</tt>.
     * <p>
     * The returned CacheLoader is serializable.
     * 
     * @return a CacheLoader that returns <tt>null</tt> for any key.
     */
    public static <K, V> CacheLoader<K, V> nullLoader() {
        return new NullLoader<K, V>();
    }


    /**
     * This class wraps a cache in such a way that it can be used as a cache loader for
     * another cache.
     */
    final static class CacheAsCacheLoader<K, V> implements CacheLoader<K, V>,
            Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -9182586746716844013L;

        /** The cache used as a cache loader. */
        private final Cache<K, V> cache;

        public CacheAsCacheLoader(Cache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public V load(K key, AttributeMap attributes) {
            return cache.get(key);
        }
    }

    final static class NullLoader<K, V> implements CacheLoader<K, V>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -4411446068656772121L;

        /** {@inheritDoc} */
        public V load(K key, AttributeMap attributes) {
            return null;
        }
    }

}
