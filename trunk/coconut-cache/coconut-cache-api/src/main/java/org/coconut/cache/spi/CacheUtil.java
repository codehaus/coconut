/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Callback;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheUtil {

    public static final Cache.HitStat STAT00 = new ImmutableHitStat(0, 0);

    final static class ExtendedLoaderToLoader<K, V> implements CacheLoader<K, V> {

        private final CacheLoader<K, ? extends CacheEntry<K, V>> loader;

        ExtendedLoaderToLoader(CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public V load(K key) throws Exception {
            CacheEntry<K, V> i = loader.load(key);
            if (i == null) {
                return null;
            } else {
                return i.getValue();
            }
        }

        /** {@inheritDoc} */
        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
            HashMap<K, V> map = new HashMap<K, V>(keys.size());
            Map<K, ? extends CacheEntry<K, V>> loaded = loader.loadAll(keys);
            for (Map.Entry<K, ? extends CacheEntry<K, V>> e : loaded.entrySet()) {
                if (e.getValue() == null) {
                    map.put(e.getKey(), null);
                } else {
                    map.put(e.getKey(), e.getValue().getValue());
                }
            }
            return map;
        }
    }
    
    public static <K, V> CacheLoader<K, V> fromExtendedCacheLoader(
            CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
        if (loader instanceof AbstractCacheLoader) {
            return new AbstractExtendedLoaderToLoader<K, V>(loader);
        } else {
            return new ExtendedLoaderToLoader<K, V>(loader);
        }
    }

    public static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> toExtendedCacheLoader(
            CacheLoader<K, V> loader) {
        return loader instanceof AsyncCacheLoader ? new AbstractAsyncLoaderToExtendedLoader<K, V>(
                (AsyncCacheLoader) loader)
                : new AbstractLoaderToExtendedLoader(loader);
    }
    

    final static class AbstractAsyncLoaderToExtendedLoader<K, V> extends
            AbstractCacheLoader<K, CacheEntry<K, V>> implements
            AsyncCacheLoader<K, CacheEntry<K, V>> {

        private final AsyncCacheLoader<K, V> loader;

        AbstractAsyncLoaderToExtendedLoader(AsyncCacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> load(K key) throws Exception {
            V v = loader.load(key);
            return v == null ? null : new DefaultCacheEntry<K, V>(key, v);
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoad(final K key, final Callback<CacheEntry<K, V>> c) {
            return loader.asyncLoad(key, new Callback<V>() {

                public void completed(V result) {
                    CacheEntry<K, V> e = result == null ? null
                            : new DefaultCacheEntry<K, V>(key, result);
                    c.completed(e);
                }

                public void failed(Throwable cause) {
                    c.failed(cause);
                }
            });
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoadAll(Collection<? extends K> keys,
                Callback<Map<K, CacheEntry<K, V>>> c) {
            throw new UnsupportedOperationException();
        }
    }


    final static class AbstractExtendedLoaderToLoader<K, V> extends
            AbstractCacheLoader<K, V> {

        private final CacheLoader<K, ? extends CacheEntry<K, V>> loader;

        AbstractExtendedLoaderToLoader(CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public V load(K key) throws Exception {
            return loader.load(key).getValue();
        }
    }

    final static class AbstractLoaderToExtendedLoader<K, V> extends
            AbstractCacheLoader<K, CacheEntry<K, V>> {

        private final CacheLoader<K, V> loader;

        AbstractLoaderToExtendedLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> load(K key) throws Exception {
            V v = loader.load(key);
            return v == null ? null : new DefaultCacheEntry<K, V>(key, v);
        }
    }
    /**
     * Creates new a new HitStat object with the same number of hits and misses
     * as the specified HitStat.
     * 
     * @param copyFrom
     *            the HitStat to copy from
     * @return
     */
    public static Cache.HitStat newImmutableHitStat(Cache.HitStat copyFrom) {
        return new ImmutableHitStat(copyFrom);
    }

    public static Cache.HitStat newImmutableHitStat(long hits, long misses) {
        return new ImmutableHitStat(hits, misses);
    }
    

    /**
     * The default implementation of a <code>HitStat<code>.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     */
    static final class ImmutableHitStat implements Cache.HitStat, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 2775783950714414347L;

        /** The number of cache hits */
        private final long hits;

        /** The number of cache misses */
        private final long misses;

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(Cache.HitStat hitstat) {
            this(hitstat.getNumberOfHits(), hitstat.getNumberOfMisses());
        }

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(long hits, long misses) {
            if (hits < 0) {
                throw new IllegalArgumentException("hits must be 0 or greater");
            } else if (misses < 0) {
                throw new IllegalArgumentException("misses must be 0 or greater");
            }
            this.misses = misses;
            this.hits = hits;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Cache.HitStat)) {
                return false;
            }
            Cache.HitStat hs = (Cache.HitStat) obj;
            return hs.getNumberOfHits() == hits && hs.getNumberOfMisses() == misses;
        }

        /** {@inheritDoc} */
        public float getHitRatio() {
            final long sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return ((float) hits) / sum;
        }

        /** {@inheritDoc} */
        public long getNumberOfHits() {
            return hits;
        }

        /**
         * {@inheritDoc}
         */
        public long getNumberOfMisses() {
            return misses;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            long value = hits ^ misses;
            return (int) (value ^ (value >>> 32));
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Ressources.getString("org.coconut.cache.hitstat", getHitRatio(), hits,
                    misses);
        }
    }
}
