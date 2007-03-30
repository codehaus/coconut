/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.io.Serializable;
import java.util.Map;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.core.AttributeMap;

/**
 * Various utilities for working with cache loaders.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Loaders {

    /**
     * This method converts the specified cache to a cache loader. Calls to
     * {@link CacheLoader#load(Object)} will be converted to calls to
     * {@link Cache#get(Object)}. Calls to
     * {@link CacheLoader#loadAll(Collection)} will be converted to calls to
     * {@link Cache#getAll(Collection)}
     * 
     * @param c
     *            the cache to load entries from
     * @return a cache loader that can load values from another cache
     */
    public static <K, V> CacheLoader<K, V> cacheAsCacheLoader(Cache<K, V> c) {
        return new CacheAsCacheLoader<K, V>(c);
    }

    /**
     * Returns a CacheLoader that will return <tt>null</tt> for any key when
     * invoking {@link CacheLoader#load(Object)}. The
     * {@link CacheLoader#loadAll(Collection)} method will return a map with a
     * mapping for each key to <tt>null</tt>.
     * <p>
     * The returned CacheLoader is serializable.
     * 
     * @return a CacheLoader that returns <tt>null</tt> for any key.
     */
    public static <K, V> CacheLoader<K, V> nullLoader() {
        return new NullLoader<K, V>();
    }

    /**
     * Returns a synchronized (thread-safe) CacheLoader backed by the specified
     * cache loader. In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the wrapped CacheLoader is accomplished
     * through the returned cache loader.
     * <p>
     * The returned CacheLoader is synchronized through <tt>this</tt>.
     * <p>
     * If the specified cache loader is an instance of an
     * {@link org.coconut.cache.util.AbstractCacheLoader} the returned cache
     * loader will also be an instance of AbstractCacheLoader.
     * <p>
     * The returned cache loader will be serializable if the specified cache
     * loader is serializable.
     * 
     * @param loader
     *            the cache loader to be "wrapped" in a synchronized cache
     *            loader.
     * @return a synchronized cache loader using the specified cache loader.
     */
    public static <K, V> CacheLoader<K, V> synchronizedCacheLoader(
            CacheLoader<K, V> loader) {
        if (loader instanceof AbstractCacheLoader) {
            return new SynchronizedAbstractCacheLoader<K, V>(loader);
        } else {
            return new SynchronizedCacheLoader<K, V>(loader);
        }
    }

    /**
     * This class wraps a cache in such a way that it can be used as a cache
     * loader for another cache.
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

        /** {@inheritDoc} */
        public Map<K, V> loadAll(Map<? extends K, AttributeMap> keysWithAttributes) {
            return cache.getAll(keysWithAttributes.keySet());
        }
    }

    final static class NullLoader<K, V> extends AbstractCacheLoader<K, V> implements
            Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -4411446068656772121L;

        /** {@inheritDoc} */
        public V load(K key, AttributeMap attributes) {
            return null;
        }
    }

    @ThreadSafe
    final static class SynchronizedAbstractCacheLoader<K, V> extends
            AbstractCacheLoader<K, V> implements CacheLoader<K, V>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -6859046403446262614L;

        /** the loader to delegate to */
        @GuardedBy("this")
        private final CacheLoader<K, V> loader;

        SynchronizedAbstractCacheLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public synchronized V load(K key, AttributeMap attributes) throws Exception {
            return loader.load(key, attributes);
        }
    }

    @ThreadSafe
    final static class SynchronizedCacheLoader<K, V> implements CacheLoader<K, V>,
            Serializable {

        /** serial version UID */
        private static final long serialVersionUID = 1210861806329274300L;

        /** the loader to delegate to */
        @GuardedBy("this")
        private final CacheLoader<K, V> loader;

        SynchronizedCacheLoader(CacheLoader<K, V> loader) {
            if (loader == null) {
                throw new NullPointerException("loader is null");
            }
            this.loader = loader;

        }

        /** {@inheritDoc} */
        public synchronized V load(K key, AttributeMap attributes) throws Exception {
            return loader.load(key, attributes);
        }

        /** {@inheritDoc} */
        public synchronized Map<K, V> loadAll(Map<? extends K, AttributeMap> mapsWithAttributes)
                throws Exception {
            return loader.loadAll(mapsWithAttributes);
        }
    }
}
