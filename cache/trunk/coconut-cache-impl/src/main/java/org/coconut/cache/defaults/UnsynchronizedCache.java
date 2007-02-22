/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.event.InternalEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.expiration.ExpirationCacheService;
import org.coconut.cache.internal.service.expiration.FinalExpirationCacheService;
import org.coconut.cache.internal.service.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.annotations.CacheServiceSupport;
import org.coconut.cache.spi.annotations.CacheSupport;

/**
 * An unsynchronized cache implementation.
 * <p>
 * If multiple threads access this cache concurrently, and at least one of the
 * threads modifies the cache structurally, it <i>must</i> be synchronized
 * externally. (A structural modification is any operation that adds, deletes or
 * changes one or more mappings.) This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@CacheSupport(CacheLoadingSupport = true, CacheEntrySupport = true, ExpirationSupport = true)
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheManagementService.class })
public class UnsynchronizedCache<K, V> extends SupportedCache<K, V> {
    private final EntryMap<K, V> map = new EntryMap<K, V>(false);

    private final InternalEventService<K, V> eventService;

    private final DefaultCacheEvictionService<AbstractCacheEntry<K, V>> evictionService;

    private final DefaultCacheStatisticsService<K, V> statistics;

    private final CacheEntryLoaderService<K, V> loading;

    private final ExpirationCacheService<K, V> expiration;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        statistics = getCsm().initialize(DefaultCacheStatisticsService.class);
        expiration = getCsm().initialize(ExpirationCacheService.class);
        loading = getCsm().initialize(CacheEntryLoaderService.class);
        evictionService = getCsm().initialize(DefaultCacheEvictionService.class);
        eventService = getCsm().initialize(InternalEventService.class);

        // important must be last, because of final value being inlined.
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
    int doClear() {
        int size = map.size;
        if (size != 0) {
            long capacity = map.capacity;
            Collection<? extends AbstractCacheEntry<K, V>> list = null;
            if (eventService != null && eventService.isRemoveEventsFromClear()) {
                list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            }
            evictionService.clear();
            map.clear();
            /* Events */
            if (eventService != null) {
                eventService.cacheCleared(this, size, capacity, list);
            }
        }
        return size;
    }

    @Override
    int doEvict() {
        List<AbstractCacheEntry<K, V>> expired = new ArrayList<AbstractCacheEntry<K, V>>();
        if (expiration != null) {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.iterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                if (expiration.evictRemove(this, m)) {
                    expired.add(m);
                    evictionService.remove(m.getPolicyIndex());
                    iterator.remove();
                }
            }
        }
        List<AbstractCacheEntry<K, V>> evicted = evictionService.evict(map.size(), map
                .capacity());
        for (AbstractCacheEntry<K, V> e : evicted) {
            map.remove(e.getKey());
        }
        if (eventService != null) {
            eventService.expired(this, expired);
            eventService.entriesEvicted(this, evicted);
        }
        if (statistics != null) {
            statistics.entryExpired(expired.size());
        }
        return evicted.size();
    }

    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        AbstractCacheEntry<K, V> e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
            if (eventService != null) {
                eventService.removed(this, e);
            }
        }
        return e;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache2#doPut(java.lang.Object,
     *      java.lang.Object, long)
     */
    @Override
    CacheEntry<K, V> doPut(K key, V value, long expirationTimeMilli,
            long previousVersion, boolean isPutIfAbsent) {
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (isPutIfAbsent && prev != null) {
            return prev;
        }
        if (previousVersion > 0 && previousVersion != prev.getVersion()) {
            return null;
        }
        AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration, prev,
                key, value, expirationTimeMilli);
        doPut(e);
        trimToSize();
        if (eventService != null) {
            eventService.put(this, e, prev, e.getPolicyIndex() >= 0);
        }
        return prev;
    }

    private void trimToSize() {
        while (evictionService.isSizeBreached(map.size)) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            AbstractCacheEntry removed = map.remove(e.getKey());
            assert (removed != null);
            if (eventService != null) {
                eventService.removed(this, e);
            }
        }
        while (evictionService.isCapacityBreached(map.capacity)) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            AbstractCacheEntry removed = map.remove(e.getKey());
            assert (removed != null);
            if (eventService != null) {
                eventService.removed(this, e);
            }
        }
    }

    @Override
    CacheEntry<K, V> doPutEntry(CacheEntry<K, V> entry, long previousVersion) {
        K key = entry.getKey();
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (previousVersion >= 0 && previousVersion != prev.getVersion()) {
            return null;
        }
        AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration, prev,
                entry);
        doPut(e);
        trimToSize();
        if (eventService != null) {
            eventService.put(this, e, prev, e.getPolicyIndex() >= 0);
        }
        return prev;
    }

    @Override
    void doPutAll(Map<? extends K, ? extends V> t, long expirationTime) {
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> map = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            K key = e.getKey();
            AbstractCacheEntry<K, V> me = AbstractCacheEntry.newUnsync(this, expiration,
                    map.get(key), key, e.getValue(), expirationTime);
            map.put(me, doPut(me));
        }

        trimToSize();
        if (eventService != null) {
            for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> e : map
                    .entrySet()) {
                AbstractCacheEntry<K, V> newE = e.getKey();
                AbstractCacheEntry<K, V> prev = e.getValue();
                if (newE != null) {
                    eventService.put(this, newE, prev, newE.getPolicyIndex() >= 0);
                }
            }
        }
    }

    @Override
    void doPutEntries(Collection<CacheEntry<K, V>> entries) {
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> map = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();
        for (CacheEntry<K, V> entry : entries) {
            K key = entry.getKey();
            AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration,
                    map.get(key), entry);
            map.put(e, doPut(e));
        }

        trimToSize();
        if (eventService != null) {
            for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> e : map
                    .entrySet()) {
                eventService.put(this, e.getKey(), e.getValue(), e.getKey()
                        .getPolicyIndex() >= 0);
            }
        }
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache2#peekEntry(java.lang.Object,
     *      boolean)
     */
    @Override
    CacheEntry<K, V> doPeek(K key) {
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        return map.keySet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySetPublic(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        return map.values(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#trimToSize(int)
     */
    public void trimToSize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException(
                    "newSize cannot be a negative number, was " + newSize);
        }
        int diff = Math.max(0, map.size - newSize);
        final List<AbstractCacheEntry<K, V>> l = evictionService.evict(diff);
        for (AbstractCacheEntry<K, V> entry : l) {
            map.remove(entry.getKey());
        }
        // this happens if we use a null evictionService
        diff = map.size - newSize;
        if (diff > 0) {
            Iterator<AbstractCacheEntry<K, V>> i = map.iterator();
            while (diff-- > 0 && i.hasNext()) {
                AbstractCacheEntry<K, V> e = i.next();
                l.add(e);
                evictionService.remove(e.getPolicyIndex());
                i.remove();
            }
        }
        if (eventService != null) {
            eventService.entriesEvicted(this, l);
        }
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doReplace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue) {
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (prev != null && (oldValue == null || oldValue.equals(prev.getValue()))) {
            AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration,
                    prev, key, newValue, Cache.DEFAULT_EXPIRATION);
            doPut(e);
            if (eventService != null) {
                eventService.put(this, e, prev, e.getPolicyIndex() >= 0);
            }
            return prev;
        }
        return null;
    }

    @Override
    CacheEntry<K, V> doGet(K key) {
        long start = 0;
        boolean isHit = false;
        if (statistics != null) {
            start = statistics.entryGetStart(key);
        }

        AbstractCacheEntry<K, V> entry = map.get(key);
        if (entry == null) { // Cache Miss
            CacheEntry<K, V> ce = loading.load(key);
            if (ce != null) {
                entry = AbstractCacheEntry.newUnsync(this, expiration, null, ce);
                doPut(entry);
                entry.accessed();
                trimToSize();
            }
            if (eventService != null) {
                eventService.getAndLoad(this, key, ce);
            }
        } else if (expiration.isExpired(entry)) {
            CacheEntry<K, V> ce = loading.load(key);
            statistics.entryExpired();
            // TODO what about lazy.., when does it expire??
            if (ce == null) {
                map.remove(key);
                evictionService.remove(entry.getPolicyIndex());
                entry = null;
            } else {
                AbstractCacheEntry<K, V> newEntry = AbstractCacheEntry.newUnsync(this,
                        expiration, entry, ce);
                doPut(newEntry);
                newEntry.accessed();
                trimToSize();
                entry = newEntry;
            }
            if (eventService != null) {
                eventService.expiredAndGet(this, key, ce);
            }
        } else {
            if (expiration.needsRefresh(entry)) {
                load(entry.getKey());
            }
            entry.increment();
            entry.accessed();
            evictionService.touch(entry.getPolicyIndex());
            isHit = true;
            if (eventService != null) {
                eventService.getHit(this, entry);
            }
        }
        if (statistics != null) {
            statistics.entryGetStop(entry, start, isHit);
        }
        return entry;
    }

    @SuppressWarnings("unchecked")
    protected CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf) {
        csm.addService(DefaultCacheStatisticsService.class);
        csm.addService(DefaultCacheEvictionService.class);
        csm.addService(FinalExpirationCacheService.class);
        csm.addService(CacheEntryLoaderService.class);
        csm.addService(DefaultCacheManagementService.class);
        csm.addService(DefaultCacheEventService.class);
        return csm;
    }

    AbstractCacheEntry<K, V> doPut(AbstractCacheEntry<K, V> entry) {
        if (entry.getPolicyIndex() == -1) { // entry is newly added
            entry.setPolicyIndex(evictionService.add(entry));
            if (entry.getPolicyIndex() < 0) {
                return null;
            }
        } else if (!evictionService.replace(entry.getPolicyIndex(), entry)) {
            map.remove(entry.getKey());
            return null;
        }
        return map.put(entry);
    }
}
