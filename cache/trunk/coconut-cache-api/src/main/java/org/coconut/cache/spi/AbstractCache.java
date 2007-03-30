/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.core.Clock;

/**
 * This class provides a skeletal implementation of the <tt>Cache</tt>
 * interface, to minimize the effort required to implement the Cache interface.
 * <p>
 * This class also contains various lifecycle methods such as
 * {@link AbstractCache#start()} and {@link AbstractCache#shutdown()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCache<K, V> extends AbstractMap<K, V> implements
        Cache<K, V> {

    private final Clock clock;

    private final CacheErrorHandler<K, V> errorHandler;

    /** A UUID used to uniquely distinguish this cache */
    private final String name;

    @SuppressWarnings("unchecked")
    public AbstractCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public AbstractCache(CacheConfiguration<K, V> configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        }
        ConfigurationValidator.getInstance().verify(configuration, (Class) getClass());
        name = configuration.getName();
        errorHandler = configuration.getErrorHandler();
        errorHandler.setCacheName(name);
        this.clock = configuration.getClock();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return peek((K) key) != null;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void evict() {
        // evict is ignored for default implementation
    }

    /**
     * {@inheritDoc}
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, V> h = new HashMap<K, V>();
        for (K key : keys) {
            V value = get(key);
            h.put(key, value);
        }
        return h;
    }

    /**
     * Returns the name of this cache.
     */
    public String getName() {
        return name;
    }

//    public void putAllEntries(Collection<? extends CacheEntry<K, V>> entries) {
//        for (CacheEntry<K, V> entry : entries) {
//            putEntry(entry);
//        }
//    }
//
//    public CacheEntry<K, V> putEntry(CacheEntry<K, V> entry) {
//        throw new UnsupportedOperationException();
//    }

    /**
     * {@inheritDoc}
     */
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (!containsKey(key)) {
            return put(key, value);
        } else {
            return peek(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean remove(Object key, Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        V v = peek((K) key);
        if (v != null && v.equals(value)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (containsKey(key)) {
            return put(key, value);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null) {
            throw new NullPointerException("oldValue is null");
        } else if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }

        if (oldValue.equals(peek(key))) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    public void shutdown() {

    }

    public void preStart() {

    }

    protected void terminated() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        String s = Resources.lookup(AbstractCache.class, "toString", getName(), this
                .getClass().getSimpleName(), size(), super.toString());
        buf.append(s);
        toString0(buf);
        return buf.toString();
    }

    public void trimToSize(int newSize) {
        throw new UnsupportedOperationException("trimToSize not supported");
    }

    public long getCapacity() {
        return size();
    }
    /**
     * Returns the Clock defined for this cache.
     */
    protected Clock getClock() {
        return clock;
    }

    /**
     * Returns the CacheErrorHandler defined for this cache.
     */
    protected CacheErrorHandler<K, V> getErrorHandler() {
        return errorHandler;
    }

    /**
     * Subclasses can override this method to provide a custom toString method.
     * That will appended to the output. See the implementation details of
     * toString on AbstractCache for further details.
     * 
     * @param buf
     *            used for appending text
     */
    protected void toString0(StringBuilder buf) {

    }

    /**
     * @see org.coconut.cache.Cache#getService(java.lang.Class)
     */
    public <T> T getService(Class<T> serviceType) {
        throw new UnsupportedOperationException();
    }
}