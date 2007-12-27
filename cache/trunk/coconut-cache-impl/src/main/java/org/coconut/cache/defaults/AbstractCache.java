/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.spi.ConfigurationValidator;
import org.coconut.core.Clock;
import org.coconut.internal.util.CollectionUtils;

/**
 * An abstract implementation of {@link Cache}. Currently not general usable, hence some
 * methods and constructors have package private access.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /** The default clock for this cache. */
    private final Clock clock;

    /** The name of the cache. */
    private final String name;

    /**
     * Creates a new AbstractCache from the specified configuration.
     *
     * @param configuration
     *            the cache configuration to create the cache from
     */
    @SuppressWarnings("unchecked")
    AbstractCache(CacheConfiguration<K, V> configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        }
        ConfigurationValidator.getInstance().verify(configuration, (Class) getClass());
        String name = configuration.getName();
        clock = configuration.getClock();
        if (name == null) {
            this.name = UUID.randomUUID().toString();
        } else {
            this.name = name;
        }
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return getServiceManager().awaitTermination(timeout, unit);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return peek((K) key) != null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
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
        CollectionUtils.checkCollectionForNulls(keys);
        return doGetAll(keys);
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> entry = doGet(key);
        return entry;
    }

    public CacheServices<K, V> services() {
        return new CacheServices<K, V>(this);
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> serviceType) {
        return getServiceManager().getServiceFromCache(serviceType);
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return getServiceManager().isShutdown();
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return getServiceManager().isStarted();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getServiceManager().isTerminated();
    }

    /** {@inheritDoc} */
    public V peek(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = doPeek(key);
        return e == null ? null : e.getValue();
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> entry = doPeek(key);
        return entry;
    }

    /**
     * Prestarts the Cache.
     */
    public void prestart() {
        size();// calls to size will start the cache, if not already started
    }

    /** {@inheritDoc} */
    public V put(K key, V value) {
        return put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP, false);
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            throw new NullPointerException("m is null");
        }
        CollectionUtils.checkMapForNulls(m);
        doPutAll(m, Attributes.toMap(m.keySet()), false);
    }

    /** {@inheritDoc} */
    public V putIfAbsent(K key, V value) {
        return put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP, true);
    }

    /** {@inheritDoc} */
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        return removed == null ? null : removed.getValue();
    }

    /** {@inheritDoc} */
    public boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        return doRemove(key, value) != null;
    }

    /** {@inheritDoc} */
    public V replace(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doReplace(key, null, value, null);
        return prev == null ? null : prev.getValue();
    }

    /** {@inheritDoc} */
    public boolean replace(K key, V oldValue, V newValue) {
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

    /** {@inheritDoc} */
    public void shutdown() {
        getServiceManager().shutdown();
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        getServiceManager().shutdownNow();
    }

    /**
     * Returns a string representation of this cache. The string representation consists
     * of a list of key-value mappings in the order returned by the caches
     * <tt>entrySet</tt> view's iterator, enclosed in braces (<tt>"{}"</tt>).
     * Adjacent mappings are separated by the characters <tt>", "</tt> (comma and
     * space). Each key-value mapping is rendered as the key followed by an equals sign (<tt>"="</tt>)
     * followed by the associated value. Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this cache
     */
    @Override
    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Cache)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Cache)" : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }

    abstract CacheEntry<K, V> doGet(K key);

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
    abstract CacheEntry<K, V> doPeek(K key);

    /**
     * Adds a non-null key and non-value to the cache.
     *
     * @param key
     * @param value
     * @param putOnlyIfAbsent
     * @param attributes
     * @return the previous entry mapping to the key or <code>null</code> if an no
     *         mapping existed
     */
    abstract CacheEntry<K, V> doPut(K key, V newValue, AttributeMap attributes,
            boolean putOnlyIfAbsent, boolean returnNewEntry);

    abstract Map<K, CacheEntry<K, V>> doPutAll(Map<? extends K, ? extends V> t,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader);

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    abstract CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes);

    Clock getClock() {
        return clock;
    }

    /**
     * Returns the service manager used by the cache.
     *
     * @return the service manager used by the cache
     */
    abstract InternalCacheServiceManager getServiceManager();

    V put(K key, V value, AttributeMap attributes, boolean putOnlyIfAbsent) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, value, attributes, putOnlyIfAbsent, false);
        return prev == null ? null : prev.getValue();
    }

    abstract class AbstractSupport implements InternalCacheSupport<K, V> {

        /** {@inheritDoc} */
        public final V put(K key, V value, AttributeMap attributes) {
            return AbstractCache.this.put(key, value, attributes, false);
        }

        /** {@inheritDoc} */
        public final void putAll(Map<? extends K, ? extends V> keyValues,
                Map<? extends K, AttributeMap> attributes) {
            doPutAll(keyValues, attributes, false);
        }

        /** {@inheritDoc} */
        public final CacheEntry<K, V> valueLoaded(K key, V value, AttributeMap attributes) {
            if (value != null) {
                return doPut(key, value, attributes, false, true);
            }
            return null;
        }

        /** {@inheritDoc} */
        public final Map<K, CacheEntry<K, V>> valuesLoaded(Map<? extends K, ? extends V> values,
                Map<? extends K, AttributeMap> keys) {
            HashMap<? extends K, ? extends V> map = new HashMap<K, V>(values);
            HashMap<? extends K, AttributeMap> attr = new HashMap<K, AttributeMap>(keys);
            for (Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
                if (entry.getValue() == null) {
                    map.remove(entry.getKey());
                    attr.remove(entry.getKey());
                }
            }
            return doPutAll(map, attr, true);
        }
    }
}
