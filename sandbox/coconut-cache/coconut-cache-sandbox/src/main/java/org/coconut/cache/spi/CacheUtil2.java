/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.util.AbstractCacheLoader;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheUtil2 {

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
}
