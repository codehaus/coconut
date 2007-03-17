/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.expiration.ExpirationCacheService;
import org.coconut.cache.internal.service.expiration.FinalExpirationCacheService;
import org.coconut.cache.internal.service.joinpoint.InternalCacheOperation;
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

    private final DefaultCacheEvictionService<AbstractCacheEntry<K, V>> evictionService;

    private final CacheEntryLoaderService<K, V> loading;

    private final ExpirationCacheService<K, V> expiration;

    private final InternalCacheOperation<K, V> statistics;

    private final InternalCacheOperation<K, V> notifier = null;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        expiration = getCsm().initialize(ExpirationCacheService.class);
        loading = getCsm().initialize(CacheEntryLoaderService.class);
        evictionService = getCsm().initialize(DefaultCacheEvictionService.class);
        statistics = getCsm().initialize(DefaultCacheStatisticsService.class);
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
    public void clear() {
        checkStarted();
        long started = statistics.beforeCacheClear(this);
        int size = map.size;
        long capacity = map.capacity;
        Collection<? extends AbstractCacheEntry<K, V>> list = null;
        if (size != 0) {
            if (notifier.cacheClearNeedRemoved()) {
                list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            }
            evictionService.clear();
            map.clear();
        }
        notifier.afterCacheClear(this, started, size, capacity, list);
    }

    public void evict() {
        checkStarted();
        long started = statistics.beforeCacheEvict(this);
        int previousSize = map.size;
        long previousCapacity = map.capacity;
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
        notifier.afterCacheEvict(this, started, map.size, previousSize, map.capacity,
                previousCapacity, evicted, expired);
    }

    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        checkStarted();
        long started = statistics.beforeRemove(this, key);
        AbstractCacheEntry<K, V> e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }
        notifier.afterRemove(this, started, e);
        return e;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPut(java.lang.Object,
     *      java.lang.Object, long, java.util.concurrent.TimeUnit, long, double,
     *      long, long, long, long, long)
     */
    @Override
    CacheEntry<K, V> doPut(K key, V oldValue, V newValue, boolean replace,
            long expirationTime, long size, double cost, long hits, long creationTime,
            long lastUpdate, long lastAccess, long requiredVersion) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (requiredVersion == 0 && prev != null) {
            notifier.afterPut(this, started, null, prev, null);
            return prev;
        }
        if (requiredVersion > 0 && requiredVersion != prev.getVersion()) {
            notifier.afterPut(this, started, null, prev, null);
            return null;
        }
        if (replace
                && (prev == null || (oldValue != null && !oldValue
                        .equals(prev.getValue())))) {
            notifier.afterPut(this, started, null, prev, null);
            return null;
        }
        AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration, prev,
                key, newValue, expirationTime);
        doPut(e);
        notifier
                .afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e : null);

        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache2#doPut(java.lang.Object,
     *      java.lang.Object, long)
     */
    CacheEntry<K, V> doPut(K key, V value, long expirationTimeMilli,
            long previousVersion, boolean isPutIfAbsent) {
        checkStarted();
        long started = statistics.beforePut(this, key, value);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (isPutIfAbsent && prev != null) {
            notifier.afterPut(this, started, null, prev, null);
            return prev;
        }
        if (previousVersion > 0 && previousVersion != prev.getVersion()) {
            notifier.afterPut(this, started, null, prev, null);
            return null;
        }
        AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration, prev,
                key, value, expirationTimeMilli);
        doPut(e);
        notifier
                .afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e : null);
        return prev;
    }

    private List<CacheEntry<K, V>> trim() {
        List<CacheEntry<K, V>> list = null;
        while (evictionService.isSizeBreached(map.size)) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        while (evictionService.isCapacityBreached(map.capacity)) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        return list;
    }

    CacheEntry<K, V> doPutEntry(CacheEntry<K, V> entry, long previousVersion) {
        checkStarted();
        long started = statistics.beforePut(this, entry);
        K key = entry.getKey();
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (previousVersion >= 0 && previousVersion != prev.getVersion()) {
            notifier.afterPut(this, started, null, prev, null);
            return null;
        }
        AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration, prev,
                entry);
        doPut(e);
        notifier
                .afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e : null);
        return prev;
    }

    @Override
    void doPutAll(Map<? extends K, ? extends V> t, long expirationTime) {
        checkStarted();
        long started = statistics.beforePutAll(this, t);
        Collection<AbstractCacheEntry<K, V>> o = new ArrayList<AbstractCacheEntry<K, V>>(
                t.size());
        Collection<AbstractCacheEntry<K, V>> n = new ArrayList<AbstractCacheEntry<K, V>>(
                t.size());
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            K key = e.getKey();
            AbstractCacheEntry<K, V> prev = map.get(key);
            o.add(prev);
            AbstractCacheEntry<K, V> me = AbstractCacheEntry.newUnsync(this, expiration,
                    prev, key, e.getValue(), expirationTime);
            doPut(me);
            n.add(me.getPolicyIndex() >= 0 ? me : null);
        }
        notifier.afterPutAll(this, started, trim(), o, n);
    }

    @Override
    void doPutEntries(Collection<? extends CacheEntry<K, V>> entries) {
        checkStarted();
        long started = statistics.beforePutAll(this, entries);
        Collection<AbstractCacheEntry<K, V>> o = new ArrayList<AbstractCacheEntry<K, V>>(
                entries.size());
        Collection<AbstractCacheEntry<K, V>> n = new ArrayList<AbstractCacheEntry<K, V>>(
                entries.size());
        for (CacheEntry<K, V> entry : entries) {
            K key = entry.getKey();
            AbstractCacheEntry<K, V> prev = map.get(key);
            AbstractCacheEntry<K, V> e = AbstractCacheEntry.newUnsync(this, expiration,
                    prev, entry);
            o.add(prev);
            doPut(e);
            n.add(e.getPolicyIndex() >= 0 ? e : null);
        }
        notifier.afterPutAll(this, started, trim(), o, n);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache2#peekEntry(java.lang.Object,
     *      boolean)
     */
    @Override
    AbstractCacheEntry<K, V> doPeek(K key) {
        checkStarted();
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        checkStarted();
        return map.keySet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        checkStarted();
        return map.entrySetPublic(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        checkStarted();
        return map.values(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();// hmm checkstarted???
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#trimToSize(int)
     */
    public void trimToSize(int newSize) {
        checkStarted();
        if (newSize < 0) {
            throw new IllegalArgumentException(
                    "newSize cannot be a negative number, was " + newSize);
        }
        long started = statistics.beforeTrimToSize(this);
        int diff = Math.max(0, map.size - newSize);
        List<AbstractCacheEntry<K, V>> l = evictionService.evict(diff);
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
        notifier.afterTrimToSize(this, started, l);
    }

    @Override
    AbstractCacheEntry<K, V> doGet(K key) {
        checkStarted();
        boolean isHit = false;
        boolean isExpired = false;
        long started = statistics.beforeGet(this, key);

        AbstractCacheEntry<K, V> prev = map.get(key);
        AbstractCacheEntry<K, V> newE = null;
        List<? extends CacheEntry<K, V>> trimmed = null;
        if (prev == null) { // Cache Miss
            CacheEntry<K, V> ce = loading.load(key);
            if (ce != null) {
                newE = AbstractCacheEntry.newUnsync(this, expiration, null, ce);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else if (expiration.isExpired(prev)) {
            isExpired = true;
            CacheEntry<K, V> ce = loading.load(key);
            // TODO what about lazy.., when does it expire??
            if (ce == null) {
                map.remove(key);
                evictionService.remove(prev.getPolicyIndex());
            } else {
                newE = AbstractCacheEntry.newUnsync(this, expiration, prev, ce);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else {
            isHit = true;
            if (expiration.needsRefresh(prev)) {
                loading.load(prev.getKey());
            }
            prev.incrementHits();
            prev.accessed();
            evictionService.touch(prev.getPolicyIndex());
        }
        AbstractCacheEntry<K, V> returnMe = isHit ? prev
                : newE.getPolicyIndex() >= 0 ? newE : null;
        notifier.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
        return returnMe;
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
