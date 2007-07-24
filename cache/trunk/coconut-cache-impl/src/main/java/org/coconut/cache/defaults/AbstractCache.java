/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.ImmutableCacheEntry;
import org.coconut.cache.spi.ConfigurationValidator;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.internal.util.CollectionUtils;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCache<K, V> extends AbstractMap<K, V> implements
        Cache<K, V> {

    /** The name of the cache. */
    private final String name;

    /**
     * Creates a new AbstractCache from the specified configuration.
     * 
     * @param conf
     *            the cache configuration to create the cache from
     */
    @SuppressWarnings("unchecked")
    AbstractCache(CacheConfiguration<K, V> conf) {
        if (conf == null) {
            throw new NullPointerException("configuration is null");
        }
        ConfigurationValidator.getInstance().verify(conf, (Class) getClass());
        String name = conf.getName();
        if (name == null) {
            this.name = UUID.randomUUID().toString();
        } else {
            this.name = name;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return peek((K) key) != null;
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        for (V entry : values()) {
            if (value.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public final V get(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = doGet((K) key);
        return e == null ? null : e.getValue();
    }

    /** {@inheritDoc} */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        for (K key : keys) {
            if (key == null) {
                throw new NullPointerException("Collection of keys contains a null");
            }
        }
        return doGetAll(keys);
    }

    /** {@inheritDoc} */
    public final CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> entry = doGet(key);
        return entry == null ? null : new ImmutableCacheEntry<K, V>(entry);
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** @return the size of the cache */
    public int getSize() {
        return size();
    }

    /** {@inheritDoc} */
    public final V peek(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = doPeek(key);
        return e == null ? null : e.getValue();
    }

    /** {@inheritDoc} */
    public final CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> entry = doPeek(key);
        return entry == null ? null : new ImmutableCacheEntry<K, V>(entry);
    }

    /** {@inheritDoc} */
    public final V put(K key, V value) {
        return put(key, value, AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public V put(K key, V value, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        CacheEntry<K, V> prev = doPut(key, value, false, attributes);
        return prev == null ? null : prev.getValue();
    }

    /** {@inheritDoc} */
    public final void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            throw new NullPointerException("m is null");
        }
        CollectionUtils.checkMapForNulls(m);
        doPutAll(m, null);
    }

    /** {@inheritDoc} */
    public final V putIfAbsent(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, value, true, null);
        return prev == null ? null : prev.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public final V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        return removed == null ? null : removed.getValue();
    }

    /** {@inheritDoc} */
    public final boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        return doRemove(key, value) != null;
    }

    /** {@inheritDoc} */
    public final V replace(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doReplace(key, null, value, null);
        return prev == null ? null : prev.getValue();
    }

    /** {@inheritDoc} */
    public final boolean replace(K key, V oldValue, V newValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (oldValue == null) {
            throw new NullPointerException("oldValue is null");
        } else if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }
        CacheEntry<K, V> prev = doReplace(key, oldValue, newValue, null);
        return prev != null;
    }

    public void shutdown() {

    }

    abstract AbstractCacheEntry<K, V> doGet(K key);

    abstract Map<K, V> doGetAll(Collection<? extends K> keys);

    /**
     * Peeks an entry for the specified non-null key. The returned AbstractCacheEntry must
     * not be published.
     * 
     * @param key
     *            the key for which to peek for
     * @return an AbstractCacheEntry if an exists for the specified key, otherwise
     *         <code>null</code>
     */
    abstract AbstractCacheEntry<K, V> doPeek(K key);

    abstract CacheEntry<K, V> doPut(K key, V newValue, boolean putOnlyIfAbsent,
            AttributeMap attributes);

    abstract CacheEntry<K, V> doReplace(K key, V oldValue, V newValue,
            AttributeMap attributes);

    abstract void doPutAll(Map<? extends K, ? extends V> t, AttributeMap attributes);

    abstract CacheEntry<K, V> doRemove(Object key, Object value);
}
