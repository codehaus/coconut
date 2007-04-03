/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.ImmutableCacheEntry;
import org.coconut.cache.internal.service.loading.DefaultCacheLoaderService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.spi.ConfigurationValidator;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.AttributeMap;
import org.coconut.internal.util.CollectionUtils;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
abstract class SupportedCache<K, V> extends AbstractMap<K, V> implements
        Cache<K, V> {

    private final DefaultCacheLoaderService<K, V> loaderService;

    public final CacheServiceManager<K, V> serviceManager;

    private final DefaultCacheStatisticsService<K, V> statistics;

    private long internalVersion;

    private final String name;

    SupportedCache(CacheConfiguration<K, V> conf) {
        if (conf == null) {
            throw new NullPointerException("configuration is null");
        }
        ConfigurationValidator.getInstance().verify(conf, (Class) getClass());
        conf.setProperty(Cache.class.getCanonicalName(), this.getClass());
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, getClass()
                .getCanonicalName());
        this.name=conf.getName();
//        String name = conf.getName();
//        if (name == null) {
//            this.name = UUID.randomUUID().toString();
//        } else {
//            this.name = name;
//        }
        serviceManager = new CacheServiceManager<K, V>(this, conf);
        registerServices(serviceManager, conf);
        serviceManager.initializeAll();
        statistics = serviceManager.getComponent(DefaultCacheStatisticsService.class);
        loaderService = serviceManager.getComponent(DefaultCacheLoaderService.class);
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

    protected void checkStarted() {
        serviceManager.checkStarted();
    }

    public String getName() {
        return name;
    }

    public void shutdown() {

    }

    CacheServiceManager<K, V> getCsm() {
        return serviceManager;
    }

    long getNextVersion() {
        return internalVersion++;
    }

    /**
     * Easy bean access.
     */
    public int getSize() {
        return size();
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

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public final CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> entry = doGet(key);
        return entry == null ? null : new ImmutableCacheEntry<K, V>(this, entry);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getService(java.lang.Class)
     */
    public final <T> T getService(Class<T> serviceType) {
        T service = serviceManager.getService(serviceType);
        if (service == null) {
            // System.out.println(serviceManager.container.getComponentAdapters());
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return service;
    }

    public final V peek(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = doPeek(key);
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public final CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> entry = doPeek(key);
        return entry == null ? null : new ImmutableCacheEntry<K, V>(this, entry);
    }

    public final V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, false, true, null);
        return prev == null ? null : prev.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#put(java.lang.Object, java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public V put(K key, V value, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, false, true, attributes);
        return prev == null ? null : prev.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public final void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            throw new NullPointerException("m is null");
        }
        CollectionUtils.checkMapForNulls(m);
        doPutAll(m, null);
    }

    public final V putIfAbsent(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, false, true, null);
        return prev == null ? null : prev.getValue();
    }

    /**
     * @see java.util.AbstractMap#remove(java.lang.Object)
     */
    @Override
    public final V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        return removed == null ? null : removed.getValue();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public final boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        return doRemove(key, value) != null;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public final V replace(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, true, false, null);
        return prev == null ? null : prev.getValue();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public final boolean replace(K key, V oldValue, V newValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (oldValue == null) {
            throw new NullPointerException("oldValue is null");
        } else if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }
        CacheEntry<K, V> prev = doPut(key, oldValue, newValue, true, false, null);
        return prev != null;
    }

    // /**
    // * @see org.coconut.cache.spi.AbstractCache#getErrorHandler()
    // */
    // @Override
    // protected CacheErrorHandler<K, V> getErrorHandler() {
    // return super.getErrorHandler();
    // }

    abstract AbstractCacheEntry<K, V> doGet(K key);

    abstract AbstractCacheEntry<K, V> doPeek(K key);

    abstract CacheEntry<K, V> doPut(K key, V oldValue, V newValue, boolean replace,
            boolean putIfAbsent, AttributeMap attributes);

    abstract void doPutAll(Map<? extends K, ? extends V> t, AttributeMap attributes);

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    abstract void registerServices(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf);

    /*
     * else
     */
    // final V putVersion(K key, V value, long previousVersion) {
    // if (key == null) {
    // throw new NullPointerException("key is null");
    // } else if (value == null) {
    // throw new NullPointerException("value is null");
    // }
    // CacheEntry<K, V> prev = doPut(key, null, value, false,
    // CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
    // -1l, previousVersion);
    // return prev == null ? null : prev.getValue();
    // }
    //    
    // /**
    // * @see
    // org.coconut.cache.spi.AbstractCache#putEntries(java.util.Collection)
    // */
    // @Override
    // public final void putAllEntries(Collection<? extends CacheEntry<K, V>>
    // entries) {
    // if (entries == null) {
    // throw new NullPointerException("entries is null");
    // }
    // CollectionUtils.checkCollectionForNulls(entries);
    // doPutEntries(entries);
    // }
    // /**
    // * @see
    // org.coconut.cache.spi.AbstractCache#putEntry(org.coconut.cache.CacheEntry)
    // */
    // @Override
    // public final CacheEntry<K, V> putEntry(CacheEntry<K, V> entry) {
    // if (entry == null) {
    // throw new NullPointerException("entry is null");
    // }
    // K key = entry.getKey();
    // if (key == null) {
    // throw new NullPointerException("The key in the specified entry is null");
    // }
    // V value = entry.getValue();
    // if (value == null) {
    // throw new NullPointerException("The key in the specified entry is null");
    // }
    // double cost = entry.getCost();
    // if (Double.isNaN(cost)) {
    // throw new IllegalArgumentException("The cost of the specified entry is
    // NaN");
    // }
    // long size = entry.getSize();
    // if (size < 0) {
    //
    // }
    // long hits = entry.getHits();
    // long expirationTime = entry.getExpirationTime();
    // long creationTime = entry.getCreationTime();
    // long lastUpdate = entry.getLastUpdateTime();
    // long lastAccess = entry.getLastAccessTime();
    // expirationTime = InternalCacheutil.convert(expirationTime,
    // TimeUnit.MILLISECONDS);
    //
    // return doPut(key, null, value, false, expirationTime, size, cost, hits,
    // creationTime, lastUpdate, lastAccess, -1);
    // }
}
