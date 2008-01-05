/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.InternalCacheEntry;
import org.coconut.cache.internal.service.entry.SynchronizedEntryMap;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.internal.util.CollectionUtils;

/**
 * An synchronized cache implementation.
 * <p>
 * It is imperative that the user manually synchronize on the cache when iterating over
 * any of its collection views:
 *
 * <pre>
 *  Cache c = new SynchronizedCache();
 *      ...
 *  Set s = c.keySet();  // Needn't be in synchronized block
 *      ...
 *  synchronized(c) {  // Synchronizing on c, not s!
 *      Iterator i = s.iterator(); // Must be in synchronized block
 *      while (i.hasNext())
 *          foo(i.next());
 *  }
 * </pre>
 *
 * Failure to follow this advice may result in non-deterministic behavior.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
@ThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class, CacheManagementService.class,
        /* CacheParallelService.class, */CacheServiceManagerService.class,
        CacheStatisticsService.class, CacheWorkerService.class })
public class SynchronizedCache<K, V> extends AbstractCache<K, V> {

    private final InternalCacheListener<K, V> listener;

    private final InternalCacheLoadingService<K, V> loadingService;

    private final EntryMap<K, V> map;

    private final InternalCacheServiceManager serviceManager;

    /**
     * Creates a new UnsynchronizedCache with a default configuration.
     */
    @SuppressWarnings("unchecked")
    public SynchronizedCache() {
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
    public SynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        ServiceComposer sc = Defaults.sync(this, new Support(), conf);
        serviceManager = sc.getInternalService(InternalCacheServiceManager.class);
        listener = sc.getInternalService(InternalCacheListener.class);
        loadingService = sc.getInternalService(InternalCacheLoadingService.class);
        map = sc.getInternalService(SynchronizedEntryMap.class);
    }

    /** {@inheritDoc} */
    public void clear() {
        long started = listener.beforeCacheClear(this);
        Collection<? extends CacheEntry<K, V>> list = Collections.EMPTY_LIST;
        long volume = 0;

        synchronized (this) {
            checkRunning("clear", false);
            volume = map.volume();
            list = map.clearAndGetAll();
        }

        listener.afterCacheClear(this, started, list, volume);
    }

    /** {@inheritDoc} */
    public synchronized boolean containsValue(Object value) {
        checkRunning("get", false);
        return map.containsValue(value);
    }

    /** {@inheritDoc} */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet(this);
    }

    /** {@inheritDoc} */
    public synchronized long getVolume() {
        checkRunning("size", false);
        return map.volume();
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return map.keySet(this);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void prestart() {
        super.prestart();
    }

    /** {@inheritDoc} */
    public void removeAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(keys);
        long started = listener.beforeRemoveAll(this, keys);
        ArrayList<CacheEntry<K, V>> list = new ArrayList<CacheEntry<K, V>>(keys.size());

        synchronized (this) {
            checkRunning("put");
            map.removeAll(list, keys);
        }

        listener.afterRemoveAll(this, started, keys, list);
    }

    /** {@inheritDoc} */
    public synchronized int size() {
        checkRunning("size", false);
        return map.peekSize();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String toString() {
        return super.toString();
    }

    /** {@inheritDoc} */
    public Collection<V> values() {
        return map.values(this);
    }

    private boolean checkRunning(String operation) {
        return checkRunning(operation, true);
    }

    private boolean checkRunning(String operation, boolean op) {
        return serviceManager.lazyStart(op);
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doGet(K key) {
        InternalCacheEntry<K, V> entry = null;
        boolean isExpired = false;
        long started = listener.beforeGet(this, key);

        synchronized (this) {
            checkRunning("get");
            entry = map.doGet(key);
            isExpired = entry != null && entry.isExpired();
        }

        if (entry == null) {
            if (loadingService != null) {
                entry = (InternalCacheEntry) loadingService.loadBlocking(key,
                        Attributes.EMPTY_ATTRIBUTE_MAP);
            }
            listener.afterMiss(this, started, key, null, entry, false);
        } else if (isExpired) {
            CacheEntry<K, V> previous = entry;
            listener.dexpired(this, started, entry);
            entry = null;
            if (loadingService != null) {
                entry = (InternalCacheEntry) loadingService.loadBlocking(key,
                        Attributes.EMPTY_ATTRIBUTE_MAP);
            }
            listener.afterMiss(this, started, key, previous, entry, true);
        } else {
            listener.afterHit(this, started, key, entry);
        }
        return entry;
    }

    /** {@inheritDoc} */
    @Override
    Map<K, V> doGetAll(Collection<? extends K> keys) {
        HashMap<K, V> result = new HashMap<K, V>();
        Collection<K> loadMe = null;

        Object[] k = keys.toArray();
        InternalCacheEntry<K, V>[] entries = new InternalCacheEntry[k.length];
        boolean[] isExpired = new boolean[k.length];
        boolean[] isHit = new boolean[k.length];

        long started = listener.beforeGetAll(this, keys);
        synchronized (this) {
            checkRunning("get");
            loadMe = map.doGetAll(keys, entries, isExpired, isHit, result);
        }

        Map<K, V> loadedEntries = Collections.EMPTY_MAP;
        for (int j = 0; j < isExpired.length; j++) {
            if (isExpired[j]) {
                listener.dexpired(this, started, entries[j]);
            }
        }
        if (loadingService != null && loadMe.size() != 0) {
            loadedEntries = loadingService.loadBlockingAll(Attributes.toMap(loadMe));
            result.putAll(loadedEntries);
        }
        listener.afterGetAll(this, started, k, entries, isHit, isExpired, loadedEntries);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    synchronized CacheEntry<K, V> doPeek(K key) {
        checkRunning("get", false);
        return map.peek(key);
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doPut(K key, V newValue, AttributeMap attributes, boolean putOnlyIfAbsent,
            boolean isLoaded) {
        long started = listener.beforePut(this, key, newValue, isLoaded);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        InternalCacheEntry<K, V> newEntry = null;
        InternalCacheEntry<K, V> prev = null;
        InternalCacheEntry<K, V> eventPrev = null;

        synchronized (this) {
            if (!checkRunning("put", !isLoaded) && isLoaded) {
                return null;
            }
            prev = map.peek(key);
            eventPrev = prev;
            if (prev == null || !putOnlyIfAbsent) {
                newEntry = map.add(key, newValue, prev, attributes);
                trimmed = map.trimCache();
            } else {
                eventPrev = null;
            }
        }

        listener.afterPut(this, started, trimmed, eventPrev, newEntry, isLoaded);
        return isLoaded ? newEntry : prev;
    }

    /** {@inheritDoc} */
    @Override
    Map<K, CacheEntry<K, V>> doPutAll(Map<? extends K, ? extends V> t,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader) {
        long started = listener.beforePutAll(this, t, attributes, fromLoader);
        Map<InternalCacheEntry<K, V>, InternalCacheEntry<K, V>> newEntries = new HashMap<InternalCacheEntry<K, V>, InternalCacheEntry<K, V>>();
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;

        synchronized (this) {
            if (!checkRunning("put", !fromLoader) && fromLoader) {
                return null;
            }
            for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
                K key = entry.getKey();
                V value = entry.getValue();
                InternalCacheEntry<K, V> prev = map.peek(key);
                InternalCacheEntry<K, V> newEntry = map.add(key, value, prev, attributes.get(key));
                if (newEntry != null && newEntry.isCachable()) {
                    newEntries.put(newEntry, prev);
                }
            }
            trimmed = map.trimCache();
        }

        listener.afterPutAll(this, started, trimmed, newEntries, fromLoader);
        HashMap<K, CacheEntry<K, V>> result = new HashMap<K, CacheEntry<K, V>>();
        for (CacheEntry<K, V> ace : newEntries.keySet()) {
            result.put(ace.getKey(), ace);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        long started = listener.beforeRemove(this, key, value);
        CacheEntry<K, V> e = null;

        synchronized (this) {
            checkRunning("remove");
            e = map.remove(key, value);
        }

        listener.afterRemove(this, started, e);
        return e;
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes) {
        long started = listener.beforeReplace(this, key, newValue);
        Collection<CacheEntry<K, V>> trimmed = Collections.EMPTY_LIST;
        InternalCacheEntry<K, V> newEntry = null;
        InternalCacheEntry<K, V> prev = null;

        synchronized (this) {
            checkRunning("put");
            prev = map.peek(key);
            if (oldValue == null && prev != null || oldValue != null && prev != null
                    && oldValue.equals(prev.getValue())) {
                newEntry = map.add(key, newValue, prev, attributes);
                trimmed = map.trimCache();
            }
        }

        listener.afterPut(this, started, trimmed, prev, newEntry);
        return newEntry == null ? null : prev;
    }

    /** {@inheritDoc} */
    @Override
    InternalCacheServiceManager getServiceManager() {
        return serviceManager;
    }

    /** A helper class. */
    class Support extends AbstractSupport {

        /** {@inheritDoc} */
        public void checkRunning(String operation) {
            synchronized (SynchronizedCache.this) {
                SynchronizedCache.this.checkRunning(operation);
            }
        }

        /** {@inheritDoc} */
        public void checkRunning(String operation, boolean shutdown) {
            synchronized (SynchronizedCache.this) {
                SynchronizedCache.this.checkRunning(operation, shutdown);
            }
        }

        /** {@inheritDoc} */
        public void load(K key, AttributeMap attributes) {
            boolean doLoad = false;

            synchronized (this) {
                if (SynchronizedCache.this.checkRunning("load", false)) {
                    doLoad = map.needsLoad(key);
                }
            }
            if (doLoad) {
                loadingService.forceLoad(key, attributes);
            }
        }

        /** {@inheritDoc} */
        public void loadAll(AttributeMap attributes, boolean force) {
            final Map<K, AttributeMap> keys;
            synchronized (this) {
                if (!SynchronizedCache.this.checkRunning("load", false)) {
                    return;
                }
                if (force) {
                    keys = Attributes.toMap(new ArrayList(keySet()), attributes);
                } else {
                    keys = map.whoNeedsLoading(attributes);
                }
            }

            loadingService.forceLoadAll(keys);
        }

        /** {@inheritDoc} */
        public void loadAll(Map<? extends K, ? extends AttributeMap> attributes) {
            Map<K, AttributeMap> keys = new HashMap<K, AttributeMap>();

            synchronized (this) {
                if (!SynchronizedCache.this.checkRunning("load", false)) {
                    return;
                }
                map.needsLoad(keys, attributes);
            }

            loadingService.forceLoadAll(keys);
        }

        /** {@inheritDoc} */
        public void purgeExpired() {
            List<InternalCacheEntry<K, V>> expired = Collections.EMPTY_LIST;
            long start = listener.beforeCachePurge(SynchronizedCache.this);
            long timestamp = getClock().timestamp();
            int size = 0;
            int newSize = 0;
            long volume = 0;
            long newVolume = 0;

            synchronized (this) {
                size = map.peekSize();
                volume = map.volume();
                expired = map.purgeExpired(timestamp);
                newSize = map.peekSize();
                newVolume = map.volume();
            }

            listener.afterCachePurge(SynchronizedCache.this, start, expired, size, volume, newSize,
                    newVolume);
        }

        /** {@inheritDoc} */
        @Override
        void doTrimCache(int toSize, long toVolume) {
            long started = listener.beforeTrim(SynchronizedCache.this, toSize, toVolume);
            int size = 0;
            int newSize = 0;
            long volume = 0;
            long newVolume = 0;
            List<CacheEntry<K, V>> l = Collections.EMPTY_LIST;

            synchronized (this) {
                SynchronizedCache.this.checkRunning("trimming");
                size = map.peekSize();
                volume = map.volume();
                l = map.trimCache(toSize, toVolume);
                newSize = map.peekSize();
                newVolume = map.volume();
            }
            listener.afterTrimCache(SynchronizedCache.this, started, l, size, newSize, volume,
                    newVolume);
        }
    }
}
