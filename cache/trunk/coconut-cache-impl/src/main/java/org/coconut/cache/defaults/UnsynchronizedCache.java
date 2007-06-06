/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.expiration.UnsynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.joinpoint.InternalCacheOperation;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.service.InternalCacheServiceManager;
import org.coconut.cache.internal.service.service.UnsynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps.DefaultAttributeMap;
import org.coconut.filter.Filter;

/**
 * An unsynchronized cache implementation.
 * <p>
 * If multiple threads access this cache concurrently, and at least one of the threads
 * modifies the cache structurally, it <i>must</i> be synchronized externally. (A
 * structural modification is any operation that adds, deletes or changes one or more
 * mappings.) This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class,
        CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> {
    private final InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService;

    private final UnsynchronizedCacheExpirationService<K, V> expiration;

    public final InternalCacheLoadingService<K, V> loadingService;

    private final EntryMap<K, V> map = new EntryMap<K, V>(false);

    private final InternalCacheEventService<K, V> eventService;

    private final InternalCacheOperation<K, V> statistics;

    private final AbstractCacheEntryFactoryService<K, V> entryFactory;

    private final InternalCacheServiceManager serviceManager;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        serviceManager = new UnsynchronizedCacheServiceManager(this, new MyHelper(), conf);
        Defaults.initializeUnsynchronizedCache(serviceManager);
        expiration = serviceManager
                .getService(UnsynchronizedCacheExpirationService.class);
        loadingService = serviceManager.getService(InternalCacheLoadingService.class);
        evictionService = serviceManager.getService(InternalCacheEvictionService.class);
        eventService = serviceManager.getService(InternalCacheEventService.class);
        statistics = serviceManager.getService(DefaultCacheStatisticsService.class);
        entryFactory = serviceManager.getService(AbstractCacheEntryFactoryService.class);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getService(java.lang.Class)
     */
    public final <T> T getService(Class<T> serviceType) {
        checkStarted();
        return serviceManager.getPublicService(serviceType);
    }

    private void checkStarted() {
        serviceManager.lazyStart(false);
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
        eventService.afterCacheClear(this, started, size, capacity, list);
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
        if (expiration != null) {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.iterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                loadingService.reloadIfNeeded(m);
                if (expiration.innerIsExpired(m)) {
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
        eventService.afterCacheEvict(this, started, map.size(), previousSize, map.capacity(),
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
    public long getCapacity() {
        return map.capacity();
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
        eventService.afterTrimToSize(this, started, l);
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
        } else if (expiration.innerIsExpired(prev)) {
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
        eventService.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
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
        eventService.afterRemove(this, started, e);
        return e;
    }

    @Override
    CacheEntry<K, V> doPut(K key, V newValue, boolean putOnlyIfAbsent,
            AttributeMap attributes) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (putOnlyIfAbsent && prev != null) {
            statistics.afterPut(this, started, null, prev, null);
            eventService.afterPut(this, started, null, prev, null);
            return prev;
        }
        AbstractCacheEntry<K, V> e = entryFactory.createEntry(key, newValue, attributes,
                prev);
        doPut(e);
        statistics.afterPut(this, started, Collections.EMPTY_LIST, prev, e.getPolicyIndex() >= 0 ? e
                : null);
        eventService
                .afterPut(this, started, trim(), e.getPolicyIndex() >= 0 ? e : null, prev);
        return prev;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPut(java.lang.Object,
     *      java.lang.Object, java.lang.Object, boolean, boolean,
     *      org.coconut.core.AttributeMap)
     */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (oldValue == null) {
            if (prev == null) {
                statistics.afterPut(this, started, null, prev, null);
                eventService.afterPut(this, started, null, prev, null);
                return null;
            }
        } else {
            if (prev == null || !oldValue.equals(prev.getValue())) {
                statistics.afterPut(this, started, null, prev, null);
                eventService.afterPut(this, started, null, prev, null);
                return null;
            }
        }

        AbstractCacheEntry<K, V> e = entryFactory.createEntry(key, newValue, attributes,
                prev);
        doPut(e);
        statistics.afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e
                : null);
        eventService
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
            doPut(e.getKey(), e.getValue(), false, attributes);
        }
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doGetAll(java.util.Collection)
     */
    @Override
    Map<K, V> doGetAll(Collection<? extends K> keys) {
        HashMap<K, V> result = new HashMap<K, V>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    /**
     * @see org.coconut.cache.Cache#hasService(java.lang.Class)
     */
    public boolean hasService(Class serviceType) {
        return serviceManager.hasPublicService(serviceType);
    }

    class MyHelper implements CacheHelper<K, V> {

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#expire(java.lang.Object)
         */
        public boolean expire(K key) {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#expireAll(java.util.Collection)
         */
        public int expireAll(Collection<? extends K> collection) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#expireAll(org.coconut.filter.Filter)
         */
        public int expireAll(Filter<? extends CacheEntry<K, V>> filter) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#filter(org.coconut.filter.Filter)
         */
        public Collection<? extends CacheEntry<K, V>> filter(
                Filter<? super CacheEntry<K, V>> filter) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#filterKeys(org.coconut.filter.Filter)
         */
        public Collection<? extends K> filterKeys(Filter<? super CacheEntry<K, V>> filter) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#getCache()
         */
        public Cache<K, V> getCache() {
            // TODO Auto-generated method stub
            return UnsynchronizedCache.this;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#getMutex()
         */
        public Object getMutex() {
            throw new UnsupportedOperationException("synchronization not available");
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#isValid(java.lang.Object)
         */
        public boolean isValid(K key) {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#put(java.lang.Object,
         *      java.lang.Object, org.coconut.core.AttributeMap)
         */
        public V put(K key, V value, AttributeMap attributes) {
            CacheEntry<K, V> prev = doPut(key, value, false, attributes);
            return prev == null ? null : prev.getValue();
        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#putAll(java.util.Map,
         *      java.util.Map)
         */
        public void putAll(Map<? extends K, ? extends V> keyValues,
                Map<? extends K, AttributeMap> attributes) {
        // TODO Auto-generated method stub

        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#valueLoaded(java.lang.Object,
         *      java.lang.Object, org.coconut.core.AttributeMap)
         */
        public void valueLoaded(K key, V value, AttributeMap attributes) {
        // TODO Auto-generated method stub

        }

        /**
         * @see org.coconut.cache.internal.spi.CacheHelper#valuesLoaded(java.util.Map,
         *      java.util.Map)
         */
        public void valuesLoaded(Map<? super K, ? extends V> values,
                Map<? extends K, AttributeMap> keys) {
        // TODO Auto-generated method stub

        }

        public void evict(Object key) {
            remove(key);
        }

        public void evictAll(Collection keys) {}

        public void trimToCapacity(long capacity) {}

        public void trimToSize(int size) {}

    }

    public Map<Class<?>, Object> getAllServices() {
        return serviceManager.getAllPublicServices();
    }
}
