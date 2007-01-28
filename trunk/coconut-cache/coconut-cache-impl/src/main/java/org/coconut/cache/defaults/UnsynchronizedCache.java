/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.annotation.ThreadSafe;
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
 * <b>Note that this implementation is not synchronized.</b> If multiple
 * threads access this cache concurrently, and at least one of the threads
 * modifies the cache structurally, it <i>must</i> be synchronized externally.
 * (A structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with a key that an instance
 * already contains is not a structural modification.) This is typically
 * accomplished by synchronizing on some object that naturally encapsulates the
 * cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@CacheSupport(CacheLoadingSupport = true, CacheEntrySupport = true, querySupport = true, ExpirationSupport = true, statisticsSupport = true, eventSupport = true)
@ThreadSafe(false)
public class UnsynchronizedCache<K, V> extends SupportedCache<K, V> {
    private final EntryMap<K, V> map;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        getCsm().initializeApm(getManagementSupport().getGroup());
        // important must be last, because of final value being inlined.
        map = new MyMap();
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(Map<K, V> map) {
        this((CacheConfiguration) CacheConfiguration.create().setInitialMap(map));
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        int size = size();
        map.clear();
        getEventService().cleared(this, size);
    }

    @Override
    public void evict() {
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

    @Override
    protected CacheEntry<K, V> getEntry(K key, boolean doCopy) {
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
        if (entry!=null && doCopy) {
            return new ImmutableCacheEntry<K, V>(this, entry);
        } else {
            return entry;
        }
    }

    @Override
    public void putEntries(Collection<CacheEntry<K, V>> entries) {
        ArrayList<AbstractCacheEntry<K, V>> am = new ArrayList<AbstractCacheEntry<K, V>>(
                entries.size());
        for (CacheEntry<K, V> entry : entries) {
            am.add(newEntry(entry, map.get(entry.getKey()), null, null, 0, false));
        }
        putAllMyEntries(am);
    }


    @Override
    public V remove(Object key) {
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

    @Override
    AbstractCacheEntry.EntryFactory<K, V> getEntryFactory() {
        return AbstractCacheEntry.UNSYNC;
    }

    void evictNext() {
        AbstractCacheEntry<K, V> e = getEvictionSupport().evictNext();
        map.remove(e.getKey());
        getEventService().removed(this, e);
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
    protected void putAll(Map<? extends K, ? extends V> t, long expirationTime) {
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

    void putAllMyEntries(Collection<? extends AbstractCacheEntry<K, V>> entries) {
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
    AbstractCacheEntry<K, V> putMyEntry(AbstractCacheEntry<K, V> me) {
        AbstractCacheEntry<K, V> prev = map.put(me);
        if (me.getPolicyIndex() >= 0) {// check rejected by policy
            getEventService().put(this, me, prev);
        }
        return prev;
    }

    /**
     * @see org.coconut.cache.defaults.memory.SupportedCache#getMap()
     */
    @Override
    EntryMap<K, V> getMap() {
        return map;
    }


}
