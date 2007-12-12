/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.eviction.UnsynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.listener.DefaultCacheListener;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.loading.UnsynchronizedCacheLoaderService;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.UnsynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
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
        CacheExpirationService.class, CacheLoadingService.class, CacheServiceManagerService.class,
        CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> {

    /** The default services for this cache. */
    private final static Collection<Class<? extends AbstractCacheLifecycle>> DEFAULTS = Arrays
            .asList(DefaultCacheStatisticsService.class, DefaultCacheListener.class,
                    UnsynchronizedCacheEvictionService.class, DefaultCacheExpirationService.class,
                    UnsynchronizedCacheLoaderService.class, DefaultCacheEventService.class,
                    UnsynchronizedEntryFactoryService.class, DefaultCacheExceptionService.class);

    private final InternalCacheEntryService entryService;

    private final InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService;

    private final DefaultCacheExpirationService<K, V> expirationService;

    private final InternalCacheListener<K, V> listener;

    private final InternalCacheLoadingService<K, V> loadingService;

    private final EntryMap<K, V> map;

    private final InternalCacheServiceManager serviceManager;

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
        serviceManager = new UnsynchronizedCacheServiceManager(this, s, conf, DEFAULTS);
        listener = serviceManager.getInternalService(InternalCacheListener.class);
        expirationService = serviceManager.getInternalService(DefaultCacheExpirationService.class);
        loadingService = serviceManager.getInternalService(InternalCacheLoadingService.class);
        evictionService = serviceManager.getInternalService(InternalCacheEvictionService.class);
        entryService = serviceManager.getInternalService(AbstractCacheEntryFactoryService.class);
        map = new EntryMap<K, V>(s, false);
    }

    /** {@inheritDoc} */
    public void clear() {
        long started = listener.beforeCacheClear(this);
        Collection<? extends AbstractCacheEntry<K, V>> list = Collections.EMPTY_LIST;
        int size = 0;
        long volume = 0;

        checkRunning("clear", false);
        size = map.size();
        volume = map.volume();
        if (size != 0) {
            list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            evictionService.clear();
            map.clear();
        }

        listener.afterCacheClear(this, started, list, volume);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        checkRunning("get", false);
        return map.containsValue(value);
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
        long started = listener.beforeRemoveAll(this, keys);
        ArrayList<CacheEntry<K, V>> list = new ArrayList<CacheEntry<K, V>>(keys.size());

        checkRunning("put");
        for (K key : keys) {
            AbstractCacheEntry<K, V> e = map.remove(key, null);
            if (e != null) {
                evictionService.remove(e.getPolicyIndex());
                list.add(e);
            }
        }

        listener.afterRemoveAll(this, started, keys, list);
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

    /**
     * @param entry
     * @return the
     */
    private boolean addElement(AbstractCacheEntry<K, V> prev, AbstractCacheEntry<K, V> entry) {
        if (entry == null) {
            if (prev != null) {
                prev.setPolicyIndex(-1);
                map.remove(prev.getKey());
            }
            return false;
        }
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

    private boolean checkRunning(String operation) {
        return checkRunning(operation, true);
    }

    private boolean checkRunning(String operation, boolean op) {
        return serviceManager.lazyStart(op);
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
        long started = listener.beforeGet(this, key);

        checkRunning("get");
        entry = map.get(key);
        if (entry != null) {
            isExpired = expirationService.isExpired(entry);
            if (isExpired) {
                map.remove(key);
                evictionService.remove(entry.getPolicyIndex());
            } else {
                // reload if needed??
                entry.accessed(entryService);
                evictionService.touch(entry.getPolicyIndex());
            }
        }

        if (entry != null && !isExpired) {
            listener.afterHit(this, started, key, entry);
            return entry;
        } else {
            AbstractCacheEntry<K, V> previous = entry;
            if (isExpired) {
                listener.dexpired(this, started, entry);
                entry = null;
            }
            if (loadingService != null) {
                entry = loadingService.loadBlocking(key, AttributeMaps.EMPTY_MAP);
            }
            listener.afterMiss(this, started, key, previous, entry, isExpired);
        }
        return entry;
    }

    /** {@inheritDoc} */
    @Override
    Map<K, V> doGetAll(Collection<? extends K> keys) {
        HashMap<K, V> result = new HashMap<K, V>();
        Collection<K> loadMe = new ArrayList<K>();

        Object[] k = keys.toArray();
        AbstractCacheEntry<K, V>[] entries = new AbstractCacheEntry[k.length];
        boolean[] isExpired = new boolean[k.length];
        boolean[] isHit = new boolean[k.length];

        long started = listener.beforeGetAll(this, keys);
        checkRunning("get");

        int i = 0;
        for (K key : keys) {
            entries[i] = map.get(key);
            if (entries[i] != null) {
                isExpired[i] = expirationService.isExpired(entries[i]);
                if (isExpired[i]) {
                    map.remove(key);
                    evictionService.remove(entries[i].getPolicyIndex());
                    loadMe.add(key);
                    result.put(key, null);
                } else {
                    // reload if needed??
                    entries[i].accessed(entryService);
                    evictionService.touch(entries[i].getPolicyIndex());
                    isHit[i] = true;
                    result.put(key, entries[i].getValue());
                }
            } else {
                loadMe.add(key);
                result.put(key, null);
            }
            i++;
        }

        Map<K, AbstractCacheEntry<K, V>> loadedEntries = Collections.EMPTY_MAP;
        for (int j = 0; j < isExpired.length; j++) {
            if (isExpired[j]) {
                listener.dexpired(this, started, entries[j]);
            }
        }
        if (loadingService != null && loadMe.size() != 0) {
            loadedEntries = loadingService.loadAllBlocking(AttributeMaps.toMap(loadMe,
                    AttributeMaps.EMPTY_MAP));
            for (AbstractCacheEntry<K, V> entry : loadedEntries.values()) {
                if (entry != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        listener.afterGetAll(this, started, k, entries, isHit, isExpired, loadedEntries);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    AbstractCacheEntry<K, V> doPeek(K key) {
        checkRunning("get", false);
        return map.get(key);
    }

    /** {@inheritDoc} */
    @Override
    AbstractCacheEntry<K, V> doPut(K key, V newValue, AttributeMap attributes,
            boolean putOnlyIfAbsent, boolean isLoaded) {
        long started = listener.beforePut(this, key, newValue, isLoaded);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        AbstractCacheEntry<K, V> newEntry = null;
        AbstractCacheEntry<K, V> prev = null;

        if (!checkRunning("put", !isLoaded) && isLoaded) {
            return null;
        }
        prev = map.get(key);
        if (prev == null || !putOnlyIfAbsent) {
            newEntry = entryService.createEntry(key, newValue, attributes, prev);
            if (addElement(prev, newEntry)) {
                trimmed = trimCache();
            }
        }

        listener.afterPut(this, started, trimmed, prev, newEntry, isLoaded);
        return isLoaded ? newEntry : prev;
    }

    /** {@inheritDoc} */
    @Override
    Map<K, AbstractCacheEntry<K, V>> doPutAll(Map<? extends K, ? extends V> t,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader) {
        long started = listener.beforePutAll(this, t, attributes, fromLoader);
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> newEntries = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();

        if (!checkRunning("put", !fromLoader) && fromLoader) {
            return null;
        }
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            AbstractCacheEntry<K, V> prev = map.get(key);
            AbstractCacheEntry<K, V> newEntry = entryService.createEntry(key, value, attributes
                    .get(key), prev);
            if (addElement(prev, newEntry)) {
                newEntries.put(newEntry, prev);
            }
        }

        listener.afterPutAll(this, started, trimCache(), newEntries, fromLoader);

        HashMap<K, AbstractCacheEntry<K, V>> result = new HashMap<K, AbstractCacheEntry<K, V>>();
        for (AbstractCacheEntry<K, V> ace : newEntries.keySet()) {
            result.put(ace.getKey(), ace);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        long started = listener.beforeRemove(this, key, value);
        AbstractCacheEntry<K, V> e = null;

        checkRunning("remove");
        e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }

        listener.afterRemove(this, started, e);
        return e;
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes) {
        long started = listener.beforeReplace(this, key, newValue);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        AbstractCacheEntry<K, V> newEntry = null;
        AbstractCacheEntry<K, V> prev = null;

        checkRunning("put");
        prev = map.get(key);
        if (oldValue == null && prev != null || oldValue != null && prev != null
                && oldValue.equals(prev.getValue())) {
            newEntry = entryService.createEntry(key, newValue, attributes, prev);
            if (addElement(prev, newEntry)) {
                trimmed = trimCache();
            }
        }

        listener.afterPut(this, started, trimmed, prev, newEntry);
        return newEntry == null ? null : prev;
    }

    InternalCacheServiceManager getServiceManager() {
        return serviceManager;
    }

    /** A helper class. */
    class Support implements InternalCacheSupport<K, V> {
        /** {@inheritDoc} */
        public void checkRunning(String operation) {
            UnsynchronizedCache.this.checkRunning(operation);
        }

        /** {@inheritDoc} */
        public void checkRunning(String operation, boolean shutdown) {
            UnsynchronizedCache.this.checkRunning(operation, shutdown);
        }

        /** {@inheritDoc} */
        public void load(K key, AttributeMap attributes) {
            boolean doLoad = false;

            if (!UnsynchronizedCache.this.checkRunning("load", false)) {
                return;
            }
            AbstractCacheEntry<K, V> e = map.get(key);
            doLoad = e == null;
            if (!doLoad) {
                long timestamp = getClock().timestamp();
                doLoad = e.isExpired(expirationService.getExpirationFilter(), timestamp)
                        || e.needsRefresh(loadingService.getRefreshPredicate(), timestamp);
            }

            if (doLoad) {
                loadingService.forceLoad(key, attributes);
            }
        }

        /** {@inheritDoc} */
        public void loadAll(AttributeMap attributes, boolean force) {
            final Map<K, AttributeMap> keys;
            long timestamp = getClock().timestamp();

            if (!UnsynchronizedCache.this.checkRunning("load", false)) {
                return;
            }
            if (force) {
                keys = AttributeMaps.toMap(new ArrayList(keySet()), attributes);
            } else {
                keys = new HashMap<K, AttributeMap>();
                for (Iterator<AbstractCacheEntry<K, V>> i = map.iterator(); i.hasNext();) {
                    AbstractCacheEntry<K, V> e = i.next();
                    if (e.isExpired(expirationService.getExpirationFilter(), timestamp)
                            || e.needsRefresh(loadingService.getRefreshPredicate(), timestamp)) {
                        keys.put(e.getKey(), attributes);
                    }
                }
            }

            loadingService.forceLoadAll(keys);
        }

        /** {@inheritDoc} */
        public void loadAll(Map<? extends K, ? extends AttributeMap> attributes) {
            Map<K, AttributeMap> keys = new HashMap<K, AttributeMap>();
            long timestamp = getClock().timestamp();

            if (!UnsynchronizedCache.this.checkRunning("load", false)) {
                return;
            }
            for (Map.Entry<? extends K, ? extends AttributeMap> e : attributes.entrySet()) {
                AbstractCacheEntry<K, V> ce = map.get(e.getKey());
                boolean doLoad = ce == null;
                if (!doLoad) {
                    doLoad = ce.isExpired(expirationService.getExpirationFilter(), timestamp)
                            || ce.needsRefresh(loadingService.getRefreshPredicate(), timestamp);
                }
                if (doLoad) {
                    keys.put(e.getKey(), e.getValue());
                }
            }

            loadingService.forceLoadAll(keys);
        }

        /** {@inheritDoc} */
        public void purgeExpired() {
            List<AbstractCacheEntry<K, V>> expired = new ArrayList<AbstractCacheEntry<K, V>>();
            long start = listener.beforeCachePurge(UnsynchronizedCache.this);
            long timestamp = getClock().timestamp();
            int size = 0;
            int newSize = 0;
            long volume = 0;
            long newVolume = 0;

            size = map.size();
            volume = map.volume();
            for (Iterator<AbstractCacheEntry<K, V>> i = map.iterator(); i.hasNext();) {
                AbstractCacheEntry<K, V> e = i.next();
                if (e.isExpired(expirationService.getExpirationFilter(), timestamp)) {
                    expired.add(e);
                    evictionService.remove(e.getPolicyIndex());
                    i.remove();
                }
            }
            newSize = map.size();
            newVolume = map.volume();

            listener.afterCachePurge(UnsynchronizedCache.this, start, expired, size, volume,
                    newSize, newVolume);
        }

        /** {@inheritDoc} */
        public V put(K key, V value, AttributeMap attributes) {
            return UnsynchronizedCache.this.put(key, value, attributes, false);
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends K, ? extends V> keyValues,
                Map<? extends K, AttributeMap> attributes) {
            doPutAll(keyValues, attributes, false);
        }

        /** {@inheritDoc} */
        public void trimCache(int toSize, long toVolume) {
            if (toSize < 0) {
                throw new IllegalArgumentException("newSize cannot be a negative number, was "
                        + toSize);
            } else if (toVolume < 0) {
                throw new IllegalArgumentException("newVolume cannot be a negative number, was "
                        + toVolume);
            }
            long started = listener.beforeTrim(UnsynchronizedCache.this, toSize, toVolume);
            int size = 0;
            int newSize = 0;
            long volume = 0;
            long newVolume = 0;
            List<AbstractCacheEntry<K, V>> l = Collections.EMPTY_LIST;

            size = map.size();
            volume = map.volume();

            checkRunning("trimming");
            int numberToTrim = Math.max(0, map.size() - toSize);
            l = new ArrayList<AbstractCacheEntry<K, V>>(evictionService.evict(numberToTrim));
            for (AbstractCacheEntry<K, V> entry : l) {
                map.remove(entry.getKey());
            }
            while (map.volume() > toVolume) {
                AbstractCacheEntry<K, V> entry = evictionService.evictNext();
                map.remove(entry.getKey());
                l.add(entry);
            }
            newSize = map.size();
            newVolume = map.volume();

            listener.afterTrimCache(UnsynchronizedCache.this, started, l, size, newSize, volume,
                    newVolume);

        }

        /** {@inheritDoc} */
        public AbstractCacheEntry<K, V> valueLoaded(K key, V value, AttributeMap attributes) {
            if (value != null) {
                return doPut(key, value, attributes, false, true);
            }
            return null;
        }

        /** {@inheritDoc} */
        public Map<K, AbstractCacheEntry<K, V>> valuesLoaded(Map<? extends K, ? extends V> values,
                Map<? extends K, AttributeMap> keys) {
            HashMap<? extends K, ? extends V> map = new HashMap<K, V>(values);
            HashMap<? extends K, AttributeMap> attr = new HashMap<K, AttributeMap>(keys);
            for (Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
                if (entry.getValue() == null) {
                    map.remove(entry.getKey());
                    attr.remove(entry.getKey());
                }
            }
            return doPutAll(map, attr, true);
        }
    }
}
