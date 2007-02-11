/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.services.CacheStatisticsCacheService;
import org.coconut.cache.internal.services.EventCacheService;
import org.coconut.cache.internal.services.EvictionCacheService;
import org.coconut.cache.internal.services.ManagementCacheService;
import org.coconut.cache.internal.services.expiration.ExpirationCacheService;
import org.coconut.cache.internal.services.expiration.FinalExpirationCacheService;
import org.coconut.cache.internal.services.loading.CacheEntryLoaderService;
import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.spi.CacheUtil;

/**
 * TODO fix loading. fix event bus make cache services mutable
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@ThreadSafe
@CacheSupport(CacheLoadingSupport = true, CacheEntrySupport = true, querySupport = true, ExpirationSupport = true, statisticsSupport = true, eventSupport = true)
public class SynchronizedCache<K, V> extends SupportedCache<K, V> {

    private final EntryMap<K, V> map;

    @SuppressWarnings("unchecked")
    public SynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public SynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        getCsm().initializeApm(getManagementSupport().getGroup());
        // important must be last, because of final value being inlined.
        map = new MyMap();
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
    }

    @SuppressWarnings("unchecked")
    public SynchronizedCache(Map<K, V> map) {
        this((CacheConfiguration) CacheConfiguration.create().setInitialMap(map));
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void clear() {
        int size = size();
        map.clear();
        getEventService().cleared(this, size);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#containsValue(java.lang.Object)
     */
    @Override
    public synchronized boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    /**
     * @see java.util.AbstractMap#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public synchronized void evict() {
        int expireCount = 0;
        int evictCount = 0;
        long start = getStatisticsSupport().cacheEvictStart(this);
        try {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.entryIterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                if (getExpirationSupport().evictRemove(this, m)) {
                    iterator.remove();
                    expireCount++;
                    getEventService().expired(this, m);
                }
            }
            List<AbstractCacheEntry<K, V>> evictThese = getEvictionSupport().evict(
                    map.size(), 0);
            for (AbstractCacheEntry<K, V> e : evictThese) {
                map.remove(e.getKey());
                getEventService().evicted(this, e);
                evictCount++;
            }
        } finally {
            getEventService().expired(this, expireCount);
            getEventService().evicted(this, evictCount);
            getStatisticsSupport().entryExpired(expireCount);
            getStatisticsSupport().cacheEvictStop(this, start, evictCount);
        }
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#getHitStat()
     */
    @Override
    public synchronized HitStat getHitStat() {
        return super.getHitStat();
    }

    /**
     * @see java.util.AbstractMap#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    @Override
    public synchronized void putEntries(Collection<CacheEntry<K, V>> entries) {
        ArrayList<AbstractCacheEntry<K, V>> am = new ArrayList<AbstractCacheEntry<K, V>>(
                entries.size());
        for (CacheEntry<K, V> entry : entries) {
            am.add(newEntry(entry, map.get(entry.getKey()), null, null, 0, false));
        }
        putAllMyEntries(am);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized V putIfAbsent(K key, V value) {
        return super.putIfAbsent(key, value);
    }

    @Override
    public synchronized V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> e = map.remove(key);
        if (e != null) {
            getEvictionSupport().remove(e.getPolicyIndex());
            getEventService().removed(this, e);
        }
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#remove(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized boolean remove(Object key, Object value) {
        return super.remove(key, value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized V replace(K key, V value) {
        return super.replace(key, value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        return super.replace(key, oldValue, newValue);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#resetStatistics()
     */
    @Override
    public synchronized void resetStatistics() {
        super.resetStatistics();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int size() {
        return super.size();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#toString()
     */
    @Override
    public synchronized String toString() {
        return super.toString();
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#trimToSize(int)
     */
    @Override
    public synchronized void trimToSize(int newSize) {
        super.trimToSize(newSize);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#peekEntry(java.lang.Object,
     *      boolean)
     */
    @Override
    protected synchronized CacheEntry<K, V> peekEntry(K key, boolean doCopy) {
        return super.peekEntry(key, doCopy);
    }

    @SuppressWarnings("unchecked")
    protected CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf) {
        csm.setService(CacheStatisticsCacheService.class);
        csm.setService(EvictionCacheService.class);
        csm.setService(ExpirationCacheService.class, FinalExpirationCacheService.class);
        csm.setService(CacheEntryLoaderService.class);
        csm.setService(ManagementCacheService.class);
        csm.setService(EventCacheService.class);
        return csm;
    }

    @Override
    protected synchronized void putAll(Map<? extends K, ? extends V> t,
            long expirationTime) {
        CacheUtil.checkMapForNulls(t);
        ArrayList<AbstractCacheEntry<K, V>> am = new ArrayList<AbstractCacheEntry<K, V>>();
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = t.entrySet()
                .iterator(); i.hasNext();) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            K key = e.getKey();
            AbstractCacheEntry<K, V> me = newEntry(null, map.get(key), key, e.getValue(),
                    expirationTime, false);
            am.add(me);
        }
        putAllMyEntries(am);
    }

    //package private not Threadsafe
    void evictNext() {
        AbstractCacheEntry<K, V> e = getEvictionSupport().evictNext();
        AbstractCacheEntry removed = map.remove(e.getKey());
        assert (removed != null);
        getEventService().removed(this, e);
    }

    @Override
    synchronized CacheEntry<K, V> getEntry(K key, boolean doCopy) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        long start = getStatisticsSupport().entryGetStart();
        AbstractCacheEntry<K, V> entry = map.get(key);

        if (entry == null) { // Cache Miss
            CacheEntry<K, V> ce = getLoaderSupport().load(key);
            if (ce != null) {
                entry = newEntry(ce, null, null, null, 0, false);
                map.put(entry);
                entry.accessed();
            }
            getEventService().getAndLoad(this, key, ce);
            getStatisticsSupport().entryGetStop(entry, start, false);
        } else if (getExpirationSupport().isExpired(entry)) {
            CacheEntry<K, V> loadEntry = getLoaderSupport().load(key);
            getStatisticsSupport().entryExpired();
            // TODO what about lazy.., when does it expire??
            if (loadEntry == null) {
                map.remove(key);
                entry = null;
            } else {
                AbstractCacheEntry<K, V> newEntry = newEntry(loadEntry, entry, null,
                        null, 0, true);
                map.put(entry);
                entry.accessed();
                entry = newEntry;
            }
            getEventService().expiredAndGet(this, key, loadEntry);
            getStatisticsSupport().entryGetStop(entry, start, false);
        } else {
            if (getExpirationSupport().needsRefresh(entry)) {
                load(entry.getKey());
            }
            entry.increment();
            entry.accessed();
            getEvictionSupport().touch(entry.getPolicyIndex());
            getEventService().getHit(this, entry);
            getStatisticsSupport().entryGetStop(entry, start, true);
        }
        if (entry != null && doCopy) {
            return new ImmutableCacheEntry<K, V>(this, entry);
        } else {
            return entry;
        }
    }

    @Override
    AbstractCacheEntry.EntryFactory<K, V> getEntryFactory() {
        return AbstractCacheEntry.SYNC;
    }

    /**
     * @see org.coconut.cache.defaults.memory.SupportedCache#getMap()
     */
    @Override
    EntryMap<K, V> getMap() {
        return map;
    }

    synchronized void putAllMyEntries(
            Collection<? extends AbstractCacheEntry<K, V>> entries) {
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> m = map.putAll(entries);
        for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entry : m
                .entrySet()) {
            AbstractCacheEntry<K, V> mm = entry.getKey();
            if (mm.getPolicyIndex() >= 0) {
                getEventService().put(this, mm, entry.getValue());
            }
        }
    }

    @Override
    synchronized AbstractCacheEntry<K, V> putMyEntry(AbstractCacheEntry<K, V> me) {
        AbstractCacheEntry<K, V> prev = map.put(me);
        if (me.getPolicyIndex() >= 0) {// check rejected by policy
            getEventService().put(this, me, prev);
        }
        return prev;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getAll(java.util.Collection)
     */
    @Override
    public synchronized Map<K, V> getAll(Collection<? extends K> keys) {
        return super.getAll(keys);
    }

    /**
     * @see java.util.AbstractMap#clone()
     */
    @Override
    protected synchronized Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#putEntry(org.coconut.cache.CacheEntry)
     */
    @Override
    public synchronized void putEntry(CacheEntry<K, V> entry) {
        super.putEntry(entry);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#put(java.lang.Object,
     *      java.lang.Object, long)
     */
    @Override
    protected synchronized V put(K key, V value, long expirationTimeMilli) {
        return super.put(key, value, expirationTimeMilli);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#putVersionized(java.lang.Object,
     *      java.lang.Object, long)
     */
    @Override
    synchronized V putVersionized(K key, V value, long version) {
        return super.putVersionized(key, value, version);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#put(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public synchronized V put(K key, V value, long expirationTime, TimeUnit unit) {
        return super.put(key, value, expirationTime, unit);
    }
}
