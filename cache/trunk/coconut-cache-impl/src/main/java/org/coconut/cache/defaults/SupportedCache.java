/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.util.InternalCacheutil;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.internal.util.CollectionUtils;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class SupportedCache<K, V> extends AbstractCache<K, V> {

    private final CacheEntryLoaderService<K, V> loaderService;

    private final CacheServiceManager<K, V> serviceManager;

    private final DefaultCacheStatisticsService<K, V> statistics;

    private long internalVersion;

    SupportedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        conf.setProperty(Cache.class.getCanonicalName(), this.getClass());
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, getClass()
                .getCanonicalName());
        serviceManager = new CacheServiceManager<K, V>(conf);
        populateCsm(serviceManager, conf);
        statistics = serviceManager.initialize(DefaultCacheStatisticsService.class);
        loaderService = serviceManager.initialize(CacheEntryLoaderService.class);
    }

    protected void checkStarted() {
        serviceManager.checkStarted();
    }

    public void shutdown() {

    }

    CacheServiceManager<K, V> getCsm() {
        return serviceManager;
    }

    long getNextVersion() {
        return internalVersion++;
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
    @Override
    public final <T> T getService(Class<T> serviceType) {
        return serviceManager.getService(serviceType);
    }
//
//    /**
//     * @see org.coconut.cache.spi.AbstractCache#load(java.lang.Object)
//     */
//    public final Future<?> load(K key) {
//        if (loaderService == null) {
//            throw new UnsupportedOperationException("No CacheLoader has been specified");
//        } else {
//            return loaderService.asyncLoadEntry(key, this);
//        }
//    }
//
//    /**
//     * @see org.coconut.cache.spi.AbstractCache#loadAll(java.util.Collection)
//     */
//    public final Future<?> loadAll(Collection<? extends K> keys) {
//        if (loaderService == null) {
//            throw new UnsupportedOperationException("No CacheLoader has been specified");
//        } else {
//            return loaderService.asyncLoadAllEntries(keys, this);
//        }
//    }

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
        CacheEntry<K, V> prev = doPut(key, null, value, false,
                CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
                -1l, -1l);
        return prev == null ? null : prev.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public final V put(K key, V value, long expirationTime, TimeUnit unit) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        } else if (expirationTime < 0) {
            throw new IllegalArgumentException(
                    "timeout must be a non-negative number, was " + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        long expirationTimeNano = InternalCacheutil.convert(expirationTime, unit);
        CacheEntry<K, V> prev = doPut(key, null, value, false, expirationTimeNano, -1l,
                -1.0d, -1l, -1l, -1l, -1l, -1l);
        return prev == null ? null : prev.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public final void putAll(Map<? extends K, ? extends V> m, long expirationTime,
            TimeUnit unit) {
        if (m == null) {
            throw new NullPointerException("m is null");
        } else if (expirationTime < 0) {
            throw new IllegalArgumentException("timeout must not be negative, was "
                    + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        CollectionUtils.checkMapForNulls(m);
        doPutAll(m, InternalCacheutil.convert(expirationTime, unit));
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntries(java.util.Collection)
     */
    @Override
    public final void putAllEntries(Collection<? extends CacheEntry<K, V>> entries) {
        if (entries == null) {
            throw new NullPointerException("entries is null");
        }
        CollectionUtils.checkCollectionForNulls(entries);
        doPutEntries(entries);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntry(org.coconut.cache.CacheEntry)
     */
    @Override
    public final CacheEntry<K, V> putEntry(CacheEntry<K, V> entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        K key = entry.getKey();
        if (key == null) {
            throw new NullPointerException("The key in the specified entry is null");
        }
        V value = entry.getValue();
        if (value == null) {
            throw new NullPointerException("The key in the specified entry is null");
        }
        double cost = entry.getCost();
        if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("The cost of the specified entry is NaN");
        }
        long size = entry.getSize();
        if (size < 0) {

        }
        long hits = entry.getHits();
        long expirationTime = entry.getExpirationTime();
        long creationTime = entry.getCreationTime();
        long lastUpdate = entry.getLastUpdateTime();
        long lastAccess = entry.getLastAccessTime();
        expirationTime = InternalCacheutil.convert(expirationTime, TimeUnit.MILLISECONDS);

        return doPut(key, null, value, false, expirationTime, size, cost, hits,
                creationTime, lastUpdate, lastAccess, -1);
    }

    @Override
    public final V putIfAbsent(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, false,
                CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
                -1l, 0);
        return prev == null ? null : prev.getValue();
    }

    final V putVersion(K key, V value, long previousVersion) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, false,
                CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
                -1l, previousVersion);
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
    @Override
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
    @Override
    public final V replace(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, value, true,
                CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
                -1l, -1l);
        return prev == null ? null : prev.getValue();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public final boolean replace(K key, V oldValue, V newValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (oldValue == null) {
            throw new NullPointerException("value is null");
        } else if (newValue == null) {
            throw new NullPointerException("value is null");
        }
        CacheEntry<K, V> prev = doPut(key, null, newValue, true,
                CacheExpirationService.DEFAULT_EXPIRATION, -1l, -1.0d, -1l, -1l, -1l,
                -1l, -1l);
        return prev != null;
    }

    // We need this because of visibility
    protected Clock getClock() {
        return super.getClock();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getErrorHandler()
     */
    @Override
    protected CacheErrorHandler<K, V> getErrorHandler() {
        return super.getErrorHandler();
    }

    abstract AbstractCacheEntry<K, V> doGet(K key);

    abstract AbstractCacheEntry<K, V> doPeek(K key);

    abstract CacheEntry<K, V> doPut(K key, V oldValue, V newValue, boolean replace,
            long expirationTimeNano, long size, double cost, long hits,
            long creationTime, long lastUpdate, long lastAccess, long requiredVersion);

    abstract void doPutAll(Map<? extends K, ? extends V> t, long expirationTimeMilli);

    abstract void doPutEntries(Collection<? extends CacheEntry<K, V>> entries);

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    abstract CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf);

}
