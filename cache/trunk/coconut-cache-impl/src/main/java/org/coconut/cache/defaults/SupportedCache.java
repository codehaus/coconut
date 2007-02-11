/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.services.CacheStatisticsCacheService;
import org.coconut.cache.internal.services.EventCacheService;
import org.coconut.cache.internal.services.EvictionCacheService;
import org.coconut.cache.internal.services.ManagementCacheService;
import org.coconut.cache.internal.services.expiration.ExpirationCacheService;
import org.coconut.cache.internal.services.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.util.InternalCacheutil;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.event.EventBus;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class SupportedCache<K, V> extends AbstractCache<K, V> {

    /**
     * @see org.coconut.cache.spi.AbstractCache#getService(java.lang.Class)
     */
    @Override
    public <T> T getService(Class<T> serviceType) {
        if (serviceType.equals(ManagementCacheService.class)) {

        } else if (serviceType.equals(CacheEventService.class)) {
            return (T) eventSupport;
        }
        return null;
    }

    private final ExpirationCacheService<K, V> expirationSupport;

    private final CacheServiceManager<K, V> csm;

    private final CacheStatisticsCacheService<K, V> statistics;

    private final ManagementCacheService<K, V> managementSupport;

    private final CacheEntryLoaderService<K, V> loaderSupport;

    private final EvictionCacheService<AbstractCacheEntry<K, V>> evictionSupport;

    private final EventCacheService<K, V> eventSupport;

    final boolean isThreadSafe;

    public static long convert(long timeout, TimeUnit unit) {
        if (timeout == Cache.NEVER_EXPIRE) {
            return Long.MAX_VALUE;
        } else {
            long newTime = unit.toMillis(timeout);
            if (newTime == Long.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Overflow for specified expiration time, was " + timeout + " "
                                + unit);
            }
            return newTime;
        }
    }

    /**
     * @param conf
     */
    SupportedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        conf.setProperty(Cache.class.getCanonicalName(), this.getClass());
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, getClass()
                .getCanonicalName());
        isThreadSafe = InternalCacheutil.isThreadSafe(conf);
        csm = new CacheServiceManager<K, V>(conf);
        populateCsm(csm, conf);
        statistics = csm.create(CacheStatisticsCacheService.class);
        managementSupport = csm.create(ManagementCacheService.class);
        expirationSupport = getCsm().create(ExpirationCacheService.class);
        loaderSupport = getCsm().create(CacheEntryLoaderService.class);
        evictionSupport = getCsm().create(EvictionCacheService.class);
        eventSupport = getCsm().create(EventCacheService.class);
    }

    EvictionCacheService<AbstractCacheEntry<K, V>> getEvictionSupport() {
        return evictionSupport;
    }

    ManagementCacheService<K, V> getManagementSupport() {
        return managementSupport;
    }

    EventCacheService<K, V> getEventService() {
        return eventSupport;
    }

    @Override
    public EventBus<CacheEvent<K, V>> getEventBus() {
        return getEventService().getEventBus();
    }

    CacheEntryLoaderService<K, V> getLoaderSupport() {
        return loaderSupport;
    }

    CacheStatisticsCacheService<K, V> getStatisticsSupport() {
        return statistics;
    }

    CacheServiceManager<K, V> getCsm() {
        return csm;
    }

    public ManagedGroup getGroup() {
        return managementSupport.getGroup();
    }

    @Override
    public void start() {
        super.start();
        getCsm().initializeAll(this);
    }

    abstract CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf);

    abstract EntryMap<K, V> getMap();

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return getMap().keySet(this, isThreadSafe);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return (Set) getMap().entrySet(this, isThreadSafe, true);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#resetStatistics()
     */
    @Override
    public void resetStatistics() {
        if (statistics != null) {
            statistics.cacheReset();
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = getEntry((K) key, false);
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        return getEntry(key, true);
    }

    abstract CacheEntry<K, V> getEntry(K key, boolean isExported);

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V peek(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> e = peekEntry(key, false);
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        return peekEntry(key, true);
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    protected CacheEntry<K, V> peekEntry(K key, boolean doCopy) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> entry = getMap().get(key);
        if (entry != null && doCopy) {
            entry = new ImmutableCacheEntry<K, V>(this, entry);
        }
        return entry;
    }

    @Override
    public HitStat getHitStat() {
        if (statistics != null) {
            return statistics.getHitStat();
        } else {
            return super.getHitStat();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return getMap().values(this, isThreadSafe);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return getMap().size();
    }

    protected Clock getClock() {
        return super.getClock();
    }

    @Override
    public void putEntry(CacheEntry<K, V> entry) {
        AbstractCacheEntry<K, V> me = newEntry(entry, getMap().get(entry.getKey()), null,
                null, 0, false);
        putMyEntry(me);
    }

    public void trimToSize(int newSize) {
        while (newSize < size()) {
            evictNext();
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAll(java.util.Collection)
     */
    @Override
    public Future<?> loadAll(Collection<? extends K> keys) {
        return loaderSupport.asyncLoadAllEntries(keys, this);
    }

    /** {@inheritDoc} */
    @Override
    public Future<?> load(final K key) {
        return loaderSupport.asyncLoadEntry(key, this);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getErrorHandler()
     */
    @Override
    protected CacheErrorHandler<K, V> getErrorHandler() {
        return super.getErrorHandler();
    }

    /**
     * @return the expirationSupport
     */
    ExpirationCacheService<K, V> getExpirationSupport() {
        return expirationSupport;
    }

    protected V put(K key, V value, long expirationTimeMilli) {
        AbstractCacheEntry<K, V> me = newEntry(null, getMap().get(key), key, value,
                expirationTimeMilli, false);
        AbstractCacheEntry<K, V> prev = putMyEntry(me);
        return prev == null ? null : prev.getValue();
    }

    abstract AbstractCacheEntry<K, V> putMyEntry(AbstractCacheEntry<K, V> me);

    abstract void evictNext();

    abstract AbstractCacheEntry.EntryFactory<K, V> getEntryFactory();

    AbstractCacheEntry<K, V> newEntry(CacheEntry<K, V> entry,
            AbstractCacheEntry<K, V> existing, K key, V value, long expirationTimeMilli,
            boolean isExpired) {
        return AbstractCacheEntry.newEntry(this, entry, existing, key, value,
                expirationTimeMilli, isExpired);
    }

    V putVersionized(K key, V value, long version) {
        AbstractCacheEntry<K, V> entry = getMap().get(value);
        if (entry != null && entry.getVersion() == version) {
            return put(key, value);
        } else {
            return null;
        }
    }

    class MyMap extends EntryMap<K, V> {
        @Override
        protected boolean elementAdded(AbstractCacheEntry<K, V> entry) {
            if (getEvictionSupport().isEnabled()) {
                if (entry.getPolicyIndex() == -1) {
                    entry.setPolicyIndex(getEvictionSupport().add(entry));
                    if (entry.getPolicyIndex() != -1
                            && getEvictionSupport().isCapacityReached(
                                    SupportedCache.this.size())) {
                        evictNext();
                    }
                } else {
                    if (!getEvictionSupport().replace(entry.getPolicyIndex(), entry)) {
                        entry.setPolicyIndex(-1);
                    }
                }
            } else {
                // we need to set policy index to a value >-1
                // or else it will be thrown out from the calling
                // method
                entry.setPolicyIndex(Integer.MAX_VALUE);
            }
            // if (SupportedCache<K, V>)

            return entry.getPolicyIndex() >= 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value, long expirationTime, TimeUnit unit) {
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
        long expirationTimeMilli = convert(expirationTime, unit);
        AbstractCacheEntry<K, V> me = newEntry(null, getMap().get(key), key, value,
                expirationTimeMilli, false);
        AbstractCacheEntry<K, V> prev = putMyEntry(me);
        return prev == null ? null : prev.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m, long expirationTime, TimeUnit unit) {
        if (m == null) {
            throw new NullPointerException("m is null");
        } else if (expirationTime < 0) {
            throw new IllegalArgumentException("timeout must not be negative, was "
                    + expirationTime);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        putAll(m, convert(expirationTime, unit));
    }

    abstract void putAll(Map<? extends K, ? extends V> t, long expirationTimeNano);

}
