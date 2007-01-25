/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.services.CacheStatisticsCacheService;
import org.coconut.cache.internal.services.EventCacheService;
import org.coconut.cache.internal.services.EvictionCacheService;
import org.coconut.cache.internal.services.ManagementCacheService;
import org.coconut.cache.internal.services.expiration.ExpirationCacheService;
import org.coconut.cache.internal.services.loading.CacheEntryLoaderService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.core.Clock;
import org.coconut.event.EventBus;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class SupportedCache<K, V> extends AbstractCache<K, V> {

    private final ExpirationCacheService<K, V> expirationSupport;

    private final CacheServiceManager<K, V> csm;

    private final CacheStatisticsCacheService<K, V> statistics;

    private final ManagementCacheService<K, V> managementSupport;

    private final CacheEntryLoaderService<K, V> loaderSupport;

    private final EvictionCacheService<AbstractCacheEntry<K, V>> evictionSupport;

    private final EventCacheService<K, V> eventSupport;

    /**
     * @param conf
     */
    SupportedCache(CacheConfiguration<K, V> conf) {
        super(conf);
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
        return getMap().keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return (Set) getMap().entrySet();
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

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    @Override
    public CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> entry = getMap().get(key);
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
        return getMap().values();
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

    /* Helper methods for Synchronized Cache */
    Future<?> loadAsync(AbstractCache<K, V> callback, K key) {
        return loaderSupport.asyncLoadEntry(key, callback);
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

    @Override
    protected V put0(K key, V value, long expirationTimeMilli) {
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
            return entry.getPolicyIndex() >= 0;
        }
    }
}
