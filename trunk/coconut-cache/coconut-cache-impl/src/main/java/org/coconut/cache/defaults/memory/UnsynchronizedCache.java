/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.defaults.support.CacheStatisticsCacheService;
import org.coconut.cache.defaults.support.EventCacheService;
import org.coconut.cache.defaults.support.EvictionCacheService;
import org.coconut.cache.defaults.support.ManagementCacheService;
import org.coconut.cache.defaults.support.StoreCacheService;
import org.coconut.cache.defaults.support.expiration.ExpirationCacheService;
import org.coconut.cache.defaults.support.expiration.FinalExpirationCacheService;
import org.coconut.cache.defaults.support.loading.EntryCacheService;
import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.spi.service.CacheServiceManager;
import org.coconut.event.EventBus;

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
    private final EventCacheService<K, V> eventSupport;

    private final StoreCacheService.EntrySupport<K, V> storeSupport;

    private final OpenHashMap<K, V> map;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this(CacheConfiguration.DEFAULT_CONFIGURATION);
    }

    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        storeSupport = getCsm().create(StoreCacheService.EntrySupport.class);
        eventSupport = getCsm().create(EventCacheService.class);
        getCsm().initializeApm(getManagementSupport().getGroup());
        // important must be last, because of final value being inlined.
        map = new MyMap();
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(Map<K, V> map) {
        this();
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        putAll(map);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        int size = size();
        map.clear();
        eventSupport.cleared(this, size);
    }

    @Override
    public void evict() {
        int expireCount = 0;
        int evictCount = 0;
        long start = getStatisticsSupport().cacheEvictStart(this);
        try {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.entrySet().iterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                if (getExpirationSupport().evictRemove(this, m)) {
                    iterator.remove();
                    expireCount++;
                    eventSupport.expired(this, m);
                }
            }
            List<AbstractCacheEntry<K, V>> evictThese = getEvictionSupport().evict(
                    map.size(), 0);
            for (AbstractCacheEntry<K, V> e : evictThese) {
                map.remove(e.getKey());
                eventSupport.evicted(this, e);
                evictCount++;
            }
        } finally {
            eventSupport.expired(this, expireCount);
            eventSupport.evicted(this, evictCount);
            getStatisticsSupport().entryExpired(expireCount);
            getStatisticsSupport().cacheEvictStop(this, start, evictCount);
        }
    }

    @Override
    public CacheEntry<K, V> getEntry(K key) {
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
            eventSupport.getAndLoad(this, key, ce);
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
            eventSupport.expiredAndGet(this, key, loadEntry);
            getStatisticsSupport().entryGetStop(entry, start, false);
        } else {
            if (getExpirationSupport().needsRefresh(entry)) {
                loadAsync(entry.getKey());
            }
            entry.increment();
            entry.accessed();
            getEvictionSupport().touch(entry.getPolicyIndex());
            eventSupport.getHit(this, entry);
            getStatisticsSupport().entryGetStop(entry, start, true);
        }
        return entry;
    }

    @Override
    public EventBus<CacheEvent<K, V>> getEventBus() {
        return eventSupport.getEventBus();
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
    public void putEntry(CacheEntry<K, V> entry) {
        AbstractCacheEntry<K, V> me = newEntry(entry, map.get(entry.getKey()), null,
                null, 0, false);
        putMyEntry(me);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        AbstractCacheEntry<K, V> e = map.remove(key);
        if (e != null) {
            getEvictionSupport().remove(e.getPolicyIndex());
            eventSupport.removed(this, e);
        }
        return e == null ? null : e.getValue();
    }

    public void trimToSize(int newSize) {
        while (newSize < size()) {
            evictNext();
        }
    }


    private AbstractCacheEntry<K, V> newEntry(CacheEntry<K, V> entry,
            AbstractCacheEntry<K, V> existing, K key, V value, long expirationTimeMilli,
            boolean isExpired) {
        return AbstractCacheEntry.UnsynchronizedCacheEntry.newEntry(this, entry,
                existing, key, value, expirationTimeMilli, isExpired);
    }

    void evictNext() {
        AbstractCacheEntry<K, V> e = getEvictionSupport().evictNext();
        map.remove(e.getKey());
        eventSupport.removed(this, e);
    }

    @SuppressWarnings("unchecked")
    protected CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf) {
        csm.setService(CacheStatisticsCacheService.class);
        csm.setService(EvictionCacheService.class);
        csm.setService(ExpirationCacheService.class, FinalExpirationCacheService.class);
        csm.setService(EntryCacheService.class);
        csm.setService(ManagementCacheService.class);
        csm.setService(StoreCacheService.EntrySupport.class);
        csm.setService(EventCacheService.class);
        return csm;
    }

    @Override
    protected V put0(K key, V value, long expirationTimeMilli) {
        AbstractCacheEntry<K, V> me = newEntry(null, map.get(key), key, value,
                expirationTimeMilli, false);
        AbstractCacheEntry<K, V> prev = putMyEntry(me);
        return prev == null ? null : prev.getValue();
    }

    @Override
    protected void putAll0(Map<? extends K, ? extends V> t, long expirationTime) {
        checkMapForNulls(t);
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
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> m = map
                .putAll(entries);
        for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entry : m
                .entrySet()) {
            AbstractCacheEntry<K, V> mm = entry.getKey();
            if (mm.getPolicyIndex() >= 0) {
                eventSupport.put(this, mm, entry.getValue());
            }
        }
    }

    AbstractCacheEntry<K, V> putMyEntry(AbstractCacheEntry<K, V> me) {
        storeSupport.storeEntry(me);
        AbstractCacheEntry<K, V> prev = map.put(me);
        if (me.getPolicyIndex() >= 0) {// check rejected by policy
            eventSupport.put(this, me, prev);
        }
        return prev;
    }

    /**
     * @see org.coconut.cache.defaults.memory.SupportedCache#getMap()
     */
    @Override
    OpenHashMap<K, V> getMap() {
        return map;
    }
}
