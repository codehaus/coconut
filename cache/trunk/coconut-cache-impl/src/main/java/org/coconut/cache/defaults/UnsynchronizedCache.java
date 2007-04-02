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
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.expiration.InternalCacheExpirationService;
import org.coconut.cache.internal.service.joinpoint.AfterCacheOperation;
import org.coconut.cache.internal.service.joinpoint.InternalCacheOperation;
import org.coconut.cache.internal.service.loading.AbstractCacheLoadingService;
import org.coconut.cache.internal.service.loading.DefaultCacheLoaderService;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.service.threading.NoThreadingCacheService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.annotations.CacheServiceSupport;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps.DefaultAttributeMap;

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
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class,
        CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends SupportedCache<K, V> {
    private final InternalCacheEvictionService<AbstractCacheEntry<K, V>> evictionService;

    private final InternalCacheExpirationService<K, V> expiration;

    public final InternalCacheLoadingService<K, V> loadingService;

    private final EntryMap<K, V> map = new EntryMap<K, V>(false);

    private final AfterCacheOperation<K, V> notifier;

    private final InternalCacheOperation<K, V> statistics;

    private AbstractCacheEntryFactoryService<K, V> entryFactory;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        expiration = getCsm().getComponent(InternalCacheExpirationService.class);
        loadingService = getCsm().getComponent(InternalCacheLoadingService.class);
        evictionService = getCsm().getComponent(InternalCacheEvictionService.class);
        notifier = getCsm().getComponent(DefaultCacheEventService.class);
        statistics = getCsm().getComponent(DefaultCacheStatisticsService.class);
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
        int size = map.size();
        long capacity = map.capacity();
        Collection<? extends AbstractCacheEntry<K, V>> list = null;
        if (size != 0) {
            if (statistics.needElementsAfterClear()) {
                list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            }
            evictionService.clear();
            map.clear();
        }
        statistics.afterCacheClear(this, started, size, capacity, list);
        notifier.afterCacheClear(this, started, size, capacity, list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySetPublic(this);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#evict()
     */
    public void evict() {
        checkStarted();
        long started = statistics.beforeCacheEvict(this);
        int previousSize = map.size();
        long previousCapacity = map.capacity();
        List<AbstractCacheEntry<K, V>> expired = new ArrayList<AbstractCacheEntry<K, V>>();
        if (expiration != null && loadingService.isDummy()) {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.iterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                loadingService.reloadIfNeeded(m);
                if (expiration.isExpired(m)) {
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
        statistics.afterCacheEvict(this, started, map.size(), previousSize, map
                .capacity(), previousCapacity, evicted, expired);
        notifier.afterCacheEvict(this, started, map.size(), previousSize, map.capacity(),
                previousCapacity, evicted, expired);
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
    public int size() {
        return map.size();
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
        int diff = Math.max(0, map.size() - newSize);
        List<AbstractCacheEntry<K, V>> l = evictionService.evict(diff);
        for (AbstractCacheEntry<K, V> entry : l) {
            map.remove(entry.getKey());
        }
        // this happens if we use a null evictionService
        diff = map.size() - newSize;
        if (diff > 0) {
            Iterator<AbstractCacheEntry<K, V>> i = map.iterator();
            while (diff-- > 0 && i.hasNext()) {
                AbstractCacheEntry<K, V> e = i.next();
                l.add(e);
                evictionService.remove(e.getPolicyIndex());
                i.remove();
            }
        }
        statistics.afterTrimToSize(this, started, l);
        notifier.afterTrimToSize(this, started, l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        return map.values(this);
    }

    private AbstractCacheEntry<K, V> doPut(AbstractCacheEntry<K, V> entry) {
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

    private List<CacheEntry<K, V>> trim() {
        List<CacheEntry<K, V>> list = null;
        while (evictionService.isSizeBreached(map.size())) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        while (evictionService.isCapacityBreached(map.capacity())) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected void registerServices(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf) {
        csm.addService(DefaultCacheStatisticsService.class);
        csm.addService(DefaultCacheEvictionService.class);
        csm.addService(DefaultCacheExpirationService.class);
        csm.addService(DefaultCacheLoaderService.class);
        csm.addService(DefaultCacheManagementService.class);
        csm.addService(DefaultCacheEventService.class);
        csm.addService(NoThreadingCacheService.class);
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doGet(java.lang.Object)
     */
    AbstractCacheEntry<K, V> doGet(K key) {
        checkStarted();
        boolean isHit = false;
        boolean isExpired = false;
        long started = statistics.beforeGet(this, key);

        AbstractCacheEntry<K, V> prev = map.get(key);
        AbstractCacheEntry<K, V> newE = null;
        List<? extends CacheEntry<K, V>> trimmed = null;
        if (prev == null) { // Cache Miss
            AttributeMap attributes = new DefaultAttributeMap();
            V newValue = loadingService.loadBlocking(key, attributes);
            if (newValue != null) {
                newE = entryFactory.createEntry(key, newValue, attributes, prev);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else if (expiration.isExpired(prev)) {
            isExpired = true;
            AttributeMap attributes = new DefaultAttributeMap();
            V newValue = loadingService.loadBlocking(key, attributes);
            // TODO what about lazy.., when does it expire??
            if (newValue == null) {
                map.remove(key);
                evictionService.remove(prev.getPolicyIndex());
            } else {
                newE = entryFactory.createEntry(key, newValue, attributes, prev);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else {
            isHit = true;
            loadingService.reloadIfNeeded(prev);
            prev.incrementHits();
            prev.accessed();
            evictionService.touch(prev.getPolicyIndex());
        }
        final AbstractCacheEntry<K, V> returnMe;
        if (isHit) {
            returnMe = prev;
        } else {
            returnMe = newE != null && newE.getPolicyIndex() >= 0 ? newE : null;
        }
        statistics.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
        notifier.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
        return returnMe;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache2#peekEntry(java.lang.Object,
     *      boolean)
     */
    @Override
    AbstractCacheEntry<K, V> doPeek(K key) {
        return map.get(key);
    }

    //
    // @Override
    // void doPutAll(Map<? extends K, ? extends V> t, long expirationTime) {
    // checkStarted();
    // long started = statistics.beforePutAll(this, t);
    // Collection<AbstractCacheEntry<K, V>> o = new
    // ArrayList<AbstractCacheEntry<K, V>>(
    // t.size());
    // Collection<AbstractCacheEntry<K, V>> n = new
    // ArrayList<AbstractCacheEntry<K, V>>(
    // t.size());
    // for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
    // K key = e.getKey();
    // AbstractCacheEntry<K, V> prev = map.get(key);
    // o.add(prev);
    // AbstractCacheEntry<K, V> me = AbstractCacheEntry.newUnsync(this,
    // expiration,
    // prev, key, e.getValue(), expirationTime);
    // doPut(me);
    // n.add(me.getPolicyIndex() >= 0 ? me : null);
    // }
    // statistics.afterPutAll(this, started, trim(), o, n);
    // notifier.afterPutAll(this, started, trim(), o, n);
    // }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doRemove(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        checkStarted();
        long started = statistics.beforeRemove(this, key);
        AbstractCacheEntry<K, V> e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }
        statistics.afterRemove(this, started, e);
        notifier.afterRemove(this, started, e);
        return e;
    }

   
    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPut(java.lang.Object,
     *      java.lang.Object, java.lang.Object, boolean, boolean,
     *      org.coconut.core.AttributeMap)
     */
    @Override
    CacheEntry<K, V> doPut(K key, V oldValue, V newValue, boolean replace,
            boolean putIfAbsent, AttributeMap attributes) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        //TODO add replace
        if (putIfAbsent && prev != null) {
            statistics.afterPut(this, started, null, prev, null);
            notifier.afterPut(this, started, null, prev, null);
            return prev;
        }

        AbstractCacheEntry<K, V> e = entryFactory.createEntry(key, newValue, attributes,
                prev);
        doPut(e);
        statistics.afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e
                : null);
        notifier
                .afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e : null);
        return prev;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPutAll(java.util.Map,
     *      org.coconut.core.AttributeMap)
     */
    @Override
    void doPutAll(Map<? extends K, ? extends V> t, AttributeMap attributes) {
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            doPut(e.getKey(), null, e.getValue(), false, false, attributes);
        }
    }

}
