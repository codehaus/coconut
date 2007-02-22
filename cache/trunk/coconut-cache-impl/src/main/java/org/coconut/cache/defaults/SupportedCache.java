/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.util.InternalCacheutil;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class SupportedCache<K, V> extends AbstractCache<K, V> {

    private final CacheEntryLoaderService<K, V> loaderService;

    private final CacheServiceManager<K, V> serviceManager;

    private final DefaultCacheStatisticsService<K, V> statistics;

    public SupportedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        conf.setProperty(Cache.class.getCanonicalName(), this.getClass());
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, getClass()
                .getCanonicalName());
        serviceManager = new CacheServiceManager<K, V>(conf);
        populateCsm(serviceManager, conf);
        statistics = serviceManager.initialize(DefaultCacheStatisticsService.class);
        loaderService = serviceManager.initialize(CacheEntryLoaderService.class);
    }

    CacheServiceManager<K, V> getCsm() {
        return serviceManager;
    }

    /**
     * {@inheritDoc}
     */
    public final void clear() {
        long start = 0;
        if (statistics != null) {
            start = statistics.cacheClearStart(this);
        }
        int size = doClear();
        if (statistics != null) {
            statistics.cacheClearStop(this, start, size);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void evict() {
        long start = 0;
        if (statistics != null) {
            start = statistics.cacheEvictStart(this);
        }
        int size = doEvict();
        if (statistics != null) {
            statistics.cacheEvictStop(this, start, size);
        }
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
        CacheEntry<K, V> entry = doGet(key);
        if (entry != null) {
            entry = new ImmutableCacheEntry<K, V>(this, entry);
        }
        return entry;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getHitStat()
     */
    @Override
    public final org.coconut.cache.Cache.HitStat getHitStat() {
        if (statistics == null) {
            return super.getHitStat();
        } else {
            return statistics.getHitStat();
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getService(java.lang.Class)
     */
    @Override
    public final <T> T getService(Class<T> serviceType) {
        return serviceManager.getService(serviceType);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#load(java.lang.Object)
     */
    @Override
    public final Future<?> load(K key) {
        if (loaderService == null) {
            throw new UnsupportedOperationException("No CacheLoader has been specified");
        } else {
            return loaderService.asyncLoadEntry(key, this);
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAll(java.util.Collection)
     */
    @Override
    public final Future<?> loadAll(Collection<? extends K> keys) {
        if (loaderService == null) {
            throw new UnsupportedOperationException("No CacheLoader has been specified");
        } else {
            return loaderService.asyncLoadAllEntries(keys, this);
        }
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
        CacheEntry<K, V> entry = doPeek(key);
        if (entry != null) {
            entry = new ImmutableCacheEntry<K, V>(this, entry);
        }
        return entry;
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
        long expirationTimeMilli = InternalCacheutil.convert(expirationTime, unit);
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutStart();
        }
        CacheEntry<K, V> prev = doPut(key, value, expirationTimeMilli, -1, false);
        if (statistics != null) {
            statistics.entryPutStop(start);
        }
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
        CacheUtil.checkMapForNulls(m);
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutAllStart();
        }
        doPutAll(m, InternalCacheutil.convert(expirationTime, unit));
        if (statistics != null) {
            statistics.entryPutAllStop(start, m.size());
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntries(java.util.Collection)
     */
    @Override
    public final void putEntries(Collection<CacheEntry<K, V>> entries) {
        if (entries == null) {
            throw new NullPointerException("entries is null");
        }
        CacheUtil.checkCollectionForNulls(entries);
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutAllStart();
        }
        doPutEntries(entries);
        if (statistics != null) {
            statistics.entryPutAllStop(start, entries.size());
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntry(org.coconut.cache.CacheEntry)
     */
    @Override
    public final CacheEntry<K, V> putEntry(CacheEntry<K, V> entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutStart();
        }
        CacheEntry<K, V> e = doPutEntry(entry, -1);
        if (statistics != null) {
            statistics.entryPutStop(start);
        }
        return e;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntryVersion(org.coconut.cache.CacheEntry,
     *      long)
     */
    @Override
    public final CacheEntry<K, V> putEntryVersion(CacheEntry<K, V> entry,
            long previousVersion) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        if (previousVersion < 0) {
            throw new IllegalArgumentException(
                    "version number must a non negative number (>0), was"
                            + previousVersion);
        }
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutStart();
        }
        CacheEntry<K, V> e = doPutEntry(entry, previousVersion);
        if (statistics != null) {
            statistics.entryPutStop(start);
        }
        return e;
    }

    @Override
    public final V putIfAbsent(K key, V value) {
        return putVersion0(key, value, 0, true);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putVersion(java.lang.Object,
     *      java.lang.Object, long)
     */
    @Override
    public final V putVersion(K key, V value, long previousVersion) {
        return putVersion0(key, value, previousVersion, false);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putVersion(java.lang.Object,
     *      java.lang.Object, long)
     */
    public final V putVersion0(K key, V value, long previousVersion, boolean isPutIfAbsent) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        } else if (previousVersion < 0) {
            throw new IllegalArgumentException(
                    "version number must a non negative number (>=0), was"
                            + previousVersion);
        }
        long start = 0;
        if (statistics != null) {
            start = statistics.entryPutStart();
        }
        CacheEntry<K, V> prev = doPut(key, value, Cache.DEFAULT_EXPIRATION,
                previousVersion, isPutIfAbsent);
        if (statistics != null) {
            statistics.entryPutStop(start);
        }
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
        long start = 0;
        if (statistics != null) {
            start = statistics.entryRemoveStart(key);
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        if (statistics != null) {
            start = statistics.entryRemoveStop(start, removed);
        }
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
        long start = 0;
        if (statistics != null) {
            start = statistics.entryRemoveStart(key);
        }
        CacheEntry<K, V> removed = doRemove(key, value);
        if (statistics != null) {
            start = statistics.entryRemoveStop(start, removed);
        }
        return removed != null;
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
        long start = 0;
        if (statistics != null) {
            start = statistics.entryReplaceStart();
        }
        CacheEntry<K, V> prev = doReplace(key, null, value);
        if (statistics != null) {
            statistics.entryReplaceStop(start);
        }
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
        long start = 0;
        if (statistics != null) {
            start = statistics.entryReplaceStart();
        }
        CacheEntry<K, V> prev = doReplace(key, oldValue, newValue);
        if (statistics != null) {
            statistics.entryReplaceStop(start);
        }
        return prev != null;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#resetStatistics()
     */
    @Override
    public final void resetStatistics() {
        if (statistics == null) {
            super.resetStatistics();
        } else {
            statistics.cacheReset();
        }
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

    abstract int doClear();

    abstract int doEvict();

    abstract CacheEntry<K, V> doGet(K key);

    abstract CacheEntry<K, V> doPeek(K key);

    abstract CacheEntry<K, V> doPut(K key, V value, long expirationTimeMilli,
            long version, boolean isPutIfAbsent);

    abstract void doPutAll(Map<? extends K, ? extends V> t, long expirationTimeMilli);

    abstract void doPutEntries(Collection<CacheEntry<K, V>> entries);

    abstract CacheEntry<K, V> doPutEntry(CacheEntry<K, V> entry, long version);

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    abstract CacheEntry<K, V> doReplace(K key, V oldValue, V newValue);

    abstract CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf);

}
