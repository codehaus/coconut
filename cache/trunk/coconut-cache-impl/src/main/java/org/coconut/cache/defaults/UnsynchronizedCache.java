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

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.UnsynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.internal.util.CollectionUtils;

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
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class, CacheManagementService.class,
        CacheServiceManagerService.class, CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> {

    private final InternalCacheEntryService entryService;

    private final InternalCacheEventService<K, V> eventService;

    private final InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService;

    private final DefaultCacheExpirationService<K, V> expirationService;

    private final InternalCacheLoadingService<K, V> loadingService;

    private final EntryMap<K, V> map;

    private final CacheServiceManager serviceManager;

    private final DefaultCacheStatisticsService<K, V> statisticsService;

    /**
     * Creates a new UnsynchronizedCache with a default configuration.
     */
    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    /**
     * Creates a new UnsynchronizedCache from the specified configuration.
     * 
     * @param conf
     *            the configuration to create the cache from
     * @throws NullPointerException
     *             if the specified configuration is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        Support s = new Support();
        serviceManager = new UnsynchronizedCacheServiceManager(this, s, conf);
        Defaults.initializeUnsynchronizedCache(conf, serviceManager);
        expirationService = serviceManager.getInternalService(DefaultCacheExpirationService.class);
        loadingService = serviceManager.getInternalService(InternalCacheLoadingService.class);
        evictionService = serviceManager.getInternalService(InternalCacheEvictionService.class);
        eventService = serviceManager.getInternalService(InternalCacheEventService.class);
        statisticsService = serviceManager.getInternalService(DefaultCacheStatisticsService.class);
        entryService = serviceManager.getInternalService(AbstractCacheEntryFactoryService.class);
        map = new EntryMap<K, V>(s, false);
    }

    /** {@inheritDoc} */
    public void clear() {
        checkRunning("clear");
        long started = statisticsService.beforeCacheClear(this);
        int size = map.size();
        long capacity = map.volume();
        Collection<? extends AbstractCacheEntry<K, V>> list = Collections.EMPTY_LIST;
        if (size != 0) {
            list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            evictionService.clear();
            map.clear();
        }
        statisticsService.afterCacheClear(this, started, size, capacity, list);
        eventService.afterCacheClear(this, started, size, capacity, list);
    }

    /** {@inheritDoc} */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySetPublic(this);
    }

    /** {@inheritDoc} */
    public long getVolume() {
        checkRunning("size", false);
        return map.volume();
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return map.keySet(this);
    }

    /** {@inheritDoc} */
    public void removeAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(keys);
        long started = statisticsService.beforeRemoveAll(this, keys);
        ArrayList<CacheEntry<K, V>> list = new ArrayList<CacheEntry<K, V>>(keys.size());

        checkRunning("put");
        for (K key : keys) {
            AbstractCacheEntry<K, V> e = map.remove(key, null);
            if (e != null) {
                evictionService.remove(e.getPolicyIndex());
                list.add(e);
            }
        }

        statisticsService.afterRemoveAll(this, started, list);
        eventService.afterRemoveAll(this, started, list);
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        checkRunning("remove");
        long started = statisticsService.beforeRemove(this, key);
        AbstractCacheEntry<K, V> e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }
        statisticsService.afterRemove(this, started, e);
        eventService.afterRemove(this, started, e);
        return e;
    }

    /** {@inheritDoc} */
    public int size() {
        checkRunning("size", false);
        return map.size();
    }

    /** {@inheritDoc} */
    public Collection<V> values() {
        return map.values(this);
    }

    private void checkRunning(String operation) {
        checkRunning(operation, true);
    }

    private void checkRunning(String operation, boolean op) {
        serviceManager.lazyStart(op);
    }

    private List<CacheEntry<K, V>> trimCache() {
        List<CacheEntry<K, V>> list = null;
        while (evictionService.isSizeOrVolumeBreached(map.size(), map.volume())) {
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            list.add(e);
        }
        return list == null ? Collections.EMPTY_LIST : list;
    }

    /** {@inheritDoc} */
    AbstractCacheEntry<K, V> doGet(K key) {
        AbstractCacheEntry<K, V> entry = null;
        boolean isExpired = false;
        long started = statisticsService.beforeGet(this, key);

        checkRunning("get");
        entry = map.get(key);
        if (entry != null) {
            isExpired = expirationService.isExpired(entry);
            if (isExpired) {
                map.remove(key);
                evictionService.remove(entry.getPolicyIndex());
            } else {
                // reload if needed??
                entry.accessed();
                evictionService.touch(entry.getPolicyIndex());
            }
        }

        if (entry != null && !isExpired) {
            statisticsService.afterHit(this, started, key, entry);
            return entry;
        } else {
            if (isExpired) {
                eventService.dexpired(this, started, entry);
            }
            AbstractCacheEntry<K, V> previous = entry;
            entry = loadingService.loadBlocking(key, AttributeMaps.EMPTY_MAP);
            statisticsService.afterMiss(this, started, key, previous, entry, isExpired);
        }
        return entry;
    }

    /** {@inheritDoc} */
    @Override
    Map<K, V> doGetAll(Collection<? extends K> keys) {
        checkRunning("get");
        HashMap<K, V> result = new HashMap<K, V>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    /** {@inheritDoc} */
    Map<K, V> doGetAll2(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(keys);
        Collection<K> loadMe = new ArrayList<K>();
        long started = statisticsService.beforeGetAll(this, keys);
        boolean[] isExpired = new boolean[keys.size()];
        checkRunning("get");
        HashMap<K, V> result = new HashMap<K, V>();
        int i = 0;
        for (K key : keys) {
            AbstractCacheEntry<K, V> entry = map.get(key);
            if (entry != null) {
                isExpired[i] = expirationService.isExpired(entry);
                if (isExpired[i]) {
                    map.remove(key);
                    evictionService.remove(entry.getPolicyIndex());
                    loadMe.add(key);
                } else {
                    // reload if needed??
                    entry.accessed();
                    evictionService.touch(entry.getPolicyIndex());
                    result.put(key, entry.getValue());
                }
            } else {
                loadMe.add(key);
            }
            i++;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    AbstractCacheEntry<K, V> doPeek(K key) {
        checkRunning("get", false);
        return map.get(key);
    }

    CacheServiceManager getServiceManager() {
        return serviceManager;
    }

    /** {@inheritDoc} */
    @Override
    AbstractCacheEntry<K, V> doPut(K key, V newValue, boolean putOnlyIfAbsent,
            AttributeMap attributes) {
        long started = statisticsService.beforePut(this, key, newValue);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        AbstractCacheEntry<K, V> newEntry = null;

        checkRunning("put");
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (prev == null || !putOnlyIfAbsent) {
            newEntry = entryService.createEntry(key, newValue, attributes, prev);
            if (addElement(newEntry)) {
                trimmed = trimCache();
            }
        }

        statisticsService.afterPut(this, started, trimmed, prev, newEntry);
        if (newEntry != null) {
            eventService.afterPut(this, started, trimmed, prev, newEntry);
        }
        return prev;
    }

    /**
     * @param entry
     * @return the
     */
    private boolean addElement(AbstractCacheEntry<K, V> entry) {
        if (entry.getPolicyIndex() == -1) { // entry is newly added
            entry.setPolicyIndex(evictionService.add(entry));
            if (entry.getPolicyIndex() == -1) {
                return false; // entry was rejected
            }
        } else if (!evictionService.replace(entry.getPolicyIndex(), entry)) {
            entry.setPolicyIndex(-1);
            map.remove(entry.getKey());
            return false;
        }
        map.put(entry);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    void doPutAll(Map<? extends K, ? extends V> t, Map<? extends K, AttributeMap> attributes) {
        long started = statisticsService.beforePutAll(this, t, attributes);

        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> newEntries = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();

        checkRunning("put");
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            AbstractCacheEntry<K, V> prev = map.get(key);
            AbstractCacheEntry<K, V> newEntry = entryService.createEntry(key, value, attributes.get(key),
                    prev);
            addElement(newEntry);
            newEntries.put(newEntry, prev);
        }
        Collection<CacheEntry<K, V>> trimmed = trimCache();

        statisticsService.afterPutAll(this, started, trimmed, newEntries);
        eventService.afterPutAll(this, started, trimmed, newEntries);
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes) {
        long started = statisticsService.beforePut(this, key, newValue);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        AbstractCacheEntry<K, V> newEntry = null;

        checkRunning("put");
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (oldValue == null && prev != null || oldValue != null && prev != null
                && oldValue.equals(prev.getValue())) {
            newEntry = entryService.createEntry(key, newValue, attributes, prev);
            addElement(newEntry);
            trimmed = trimCache();
        }

        statisticsService.afterPut(this, started, trimmed, prev, newEntry);
        if (newEntry != null) {
            eventService.afterPut(this, started, trimmed, prev, newEntry);
        }
        return newEntry == null ? null : prev;
    }

    /** A helper class. */
    class Support implements InternalCacheSupport<K, V> {

        public void checkRunning(String operation) {
            UnsynchronizedCache.this.checkRunning(operation);
        }

        public void checkRunning(String operation, boolean shutdown) {
            UnsynchronizedCache.this.checkRunning(operation, shutdown);
        }

        /** {@inheritDoc} */
        public Object getMutex() {
            throw new UnsupportedOperationException("synchronization not available");
        }

        public void load(K key, AttributeMap attributes) {
            AbstractCacheEntry<K, V> e = map.get(key);
            boolean doLoad = e == null;
            if (!doLoad) {
                long timestamp = getClock().timestamp();
                doLoad = e.isExpired(expirationService.getExpirationFilter(), timestamp)
                        || e.needsRefresh(loadingService.getRefreshFilter(), timestamp);
            }

            if (doLoad) {
                loadingService.forceLoad(key, attributes);
            }
        }

        public void loadAll(AttributeMap attributes, boolean force) {
            if (force) {
                Collection<K> keys = new ArrayList(keySet());
                loadingService.forceLoadAll(AttributeMaps.toMap(keys, attributes));
            } else {
                long timestamp = getClock().timestamp();
                Map<K, AttributeMap> keys = new HashMap<K, AttributeMap>();
                for (Iterator<AbstractCacheEntry<K, V>> i = map.iterator(); i.hasNext();) {
                    AbstractCacheEntry<K, V> e = i.next();
                    if (e.isExpired(expirationService.getExpirationFilter(), timestamp)
                            || e.needsRefresh(loadingService.getRefreshFilter(), timestamp)) {
                        keys.put(e.getKey(), attributes);
                    }
                }
                loadingService.forceLoadAll(keys);
            }
        }

        public void loadAll(Map<K, AttributeMap> attributes) {
            for (Map.Entry<K, AttributeMap> e : attributes.entrySet()) {
                loadingService.load(e.getKey(), e.getValue());
            }
        }

        public void purgeExpired() {
            List<AbstractCacheEntry<K, V>> expired = new ArrayList<AbstractCacheEntry<K, V>>();
            long timestamp = getClock().timestamp();
            for (Iterator<AbstractCacheEntry<K, V>> i = map.iterator(); i.hasNext();) {
                AbstractCacheEntry<K, V> e = i.next();
                if (e.isExpired(expirationService.getExpirationFilter(), timestamp)) {
                    expired.add(e);
                    evictionService.remove(e.getPolicyIndex());
                    i.remove();
                }
            }
            eventService.afterPurge(UnsynchronizedCache.this, expired);
        }

        /** {@inheritDoc} */
        public V put(K key, V value, AttributeMap attributes) {
            CacheEntry<K, V> prev = doPut(key, value, false, attributes);
            return prev == null ? null : prev.getValue();
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends K, ? extends V> keyValues,
                Map<? extends K, AttributeMap> attributes) {
            doPutAll(keyValues, attributes);
        }

        /** {@inheritDoc} */
        public void trimToSize(int newSize) {
            if (newSize < 0) {
                throw new IllegalArgumentException("newSize cannot be a negative number, was "
                        + newSize);
            }
            checkRunning("trimming");
            long started = statisticsService.beforeTrimToSize(UnsynchronizedCache.this);
            int numberToTrim = Math.max(0, map.size() - newSize);
            List<AbstractCacheEntry<K, V>> l = evictionService.evict(numberToTrim);
            for (AbstractCacheEntry<K, V> entry : l) {
                map.remove(entry.getKey());
            }
            // this happens if we use a null evictionService
            numberToTrim = map.size() - newSize;
            if (numberToTrim > 0) {
                Iterator<AbstractCacheEntry<K, V>> i = map.iterator();
                while (numberToTrim-- > 0 && i.hasNext()) {
                    AbstractCacheEntry<K, V> e = i.next();
                    l.add(e);
                    evictionService.remove(e.getPolicyIndex());
                    i.remove();
                }
            }
            statisticsService.afterTrimToSize(UnsynchronizedCache.this, started, l);
            eventService.afterTrimToSize(UnsynchronizedCache.this, started, l);
        }

        /** {@inheritDoc} */
        public void trimToVolume(long newVolume) {
            if (newVolume < 0) {
                throw new IllegalArgumentException("newVolume cannot be a negative number, was "
                        + newVolume);
            }

            checkRunning("trimming");
            long started = statisticsService.beforeTrimToSize(UnsynchronizedCache.this);
            Collection<AbstractCacheEntry<K, V>> l = new ArrayList<AbstractCacheEntry<K, V>>();
            while (map.volume() > newVolume) {
                AbstractCacheEntry<K, V> entry = evictionService.evictNext();
                map.remove(entry.getKey());
                l.add(entry);
            }
            statisticsService.afterTrimToSize(UnsynchronizedCache.this, started, l);
            eventService.afterTrimToSize(UnsynchronizedCache.this, started, l);
        }

        /** {@inheritDoc} */
        public AbstractCacheEntry<K, V> valueLoaded(K key, V value, AttributeMap attributes) {
            if (value != null) {
                checkRunning("put");
                long started = statisticsService.beforePut(UnsynchronizedCache.this, key, value);
                AbstractCacheEntry<K, V> prev = map.get(key);
                AbstractCacheEntry<K, V> e = entryService.createEntry(key, value, attributes, prev);
                addElement(e);
                statisticsService.afterPut(UnsynchronizedCache.this, started,
                        Collections.EMPTY_LIST, prev, e.getPolicyIndex() >= 0 ? e : null);
                eventService.afterPut(UnsynchronizedCache.this, started, trimCache(), prev, e
                        .getPolicyIndex() >= 0 ? e : null);
                return e;
            }
            return null;
        }

        /** {@inheritDoc} */
        public void valuesLoaded(Map<? extends K, ? extends V> values,
                Map<? extends K, AttributeMap> keys) {
            for (Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
                valueLoaded(entry.getKey(), entry.getValue(), keys.get(entry.getKey()));
            }
        }
    }
}
