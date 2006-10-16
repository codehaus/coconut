/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.annotation.ThreadSafe;
import org.coconut.apm.ApmGroup;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheQuery;
import org.coconut.cache.defaults.support.CacheStatisticsSupport;
import org.coconut.cache.defaults.support.EventSupport;
import org.coconut.cache.defaults.support.EvictionSupport;
import org.coconut.cache.defaults.support.ExpirationSupport;
import org.coconut.cache.defaults.support.LoaderSupport;
import org.coconut.cache.defaults.support.ManagementSupport;
import org.coconut.cache.defaults.support.StoreSupport;
import org.coconut.cache.defaults.support.LoaderSupport.EntrySupport;
import org.coconut.cache.defaults.util.CacheEntryMap;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheSupport;
import org.coconut.event.bus.EventBus;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * <p>
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
public class UnlimitedCache<K, V> extends AbstractCache<K, V> implements
        ConcurrentMap<K, V> {

    private final CacheEntryMap<K, V, MyEntry> map;

    /* SUPPORT */

    private final EvictionSupport<MyEntry> evictionSupport;

    private final EventSupport<K, V> eventSupport;

    private final ExpirationSupport<K, V> expirationSupport;

    private final EntrySupport<K, V> loaderSupport;

    private final ManagementSupport managementSupport;

    private final CacheStatisticsSupport<K, V> statistics;

    private final StoreSupport.EntrySupport<K, V> storeSupport;

    @SuppressWarnings("unchecked")
    public UnlimitedCache() {
        this(CacheConfiguration.DEFAULT_CONFIGURATION);
    }

    public UnlimitedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        // support
        statistics = CacheStatisticsSupport.createConcurrent(conf);
        evictionSupport = new EvictionSupport<MyEntry>(conf);
        expirationSupport = ExpirationSupport.newFinal(conf);
        loaderSupport = new LoaderSupport.EntrySupport<K, V>(conf);
        managementSupport = new ManagementSupport(conf);
        storeSupport = new StoreSupport.EntrySupport<K, V>(conf);
        eventSupport = new EventSupport<K, V>(conf);
        expirationSupport.addTo(managementSupport.getGroup());
        statistics.addTo(managementSupport.getGroup());
        // important must be last, because of final value being inlined.
        map = new MyMap();
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
    }

    @SuppressWarnings("unchecked")
    public UnlimitedCache(Map<K, V> map) {
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

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return map.valueEntrySet();
    }

    @Override
    public void evict() {
        long start = statistics.cacheEvictStart(this);
        int count = 0;
        try {
            for (Iterator<MyEntry> iterator = map.values().iterator(); iterator.hasNext();) {
                MyEntry m = iterator.next();
                if (expirationSupport.evictRemove(this, m)) {
                    iterator.remove();
                    count++;
                    eventSupport.expired(this, m);
                }
            }
        } finally {
            eventSupport.evicted(this, count);
            statistics.entryExpired(count);
            statistics.cacheEvictStop(this, start);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        CacheEntry<K, V> e = getEntry((K) key);
        return e == null ? null : e.getValue();
    }

    @Override
    public CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        long start = statistics.entryGetStart();
        MyEntry entry = map.get(key);

        if (entry == null) { // Cache Miss
            CacheEntry<K, V> ce = loaderSupport.loadEntry(key);
            if (ce != null) {
                entry = new MyEntry(ce);
                added(entry, null, ce);
                map.put(key, entry);
                entry.accessed();
            }
            V value = ce == null ? null : ce.getValue();
            eventSupport.getAndLoad(this, key, ce);
            statistics.entryGetStop(entry, start, false);
        } else {
            if (expirationSupport.doStrictAndLoad(this, entry)) {
                CacheEntry<K, V> loadEntry = loaderSupport.loadEntry(key);
                statistics.entryExpired();
                // TODO what about lazy.., when does it expire??
                if (loadEntry == null) {
                    map.remove(key);
                    entry = null;
                } else {
                    MyEntry newEntry = new MyEntry(loadEntry);
                    added(newEntry, entry, loadEntry);
                    map.put(key, entry);
                    entry.accessed();
                    entry = newEntry;
                }
                eventSupport.expiredAndGet(this, key, loadEntry);
                statistics.entryGetStop(entry, start, false);
            } else {
                entry.hits++;
                entry.accessed();
                entry.touched();
                eventSupport.getHit(this, entry);
                statistics.entryGetStop(entry, start, true);
            }
        }
        return entry;
    }

    @Override
    public EventBus<CacheEvent<K, V>> getEventBus() {
        return eventSupport.getEventBus();
    }

    public ApmGroup getGroup() {
        return managementSupport.getGroup();
    }

    @Override
    public HitStat getHitStat() {
        return statistics.getHitStat();
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAll(java.util.Collection)
     */
    @Override
    public Future<?> loadAllAsync(Collection<? extends K> keys) {
        return loaderSupport.asyncLoadAllEntries(keys, this);
    }

    /** {@inheritDoc} */
    @Override
    public Future<?> loadAsync(final K key) {
        return loaderSupport.asyncLoadEntry(key, this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public V peek(K key) {
        CacheEntry<K, V> e = peekEntry(key);
        return e == null ? null : e.getValue();
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public CacheEntry<K, V> peekEntry(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        MyEntry entry = map.get(key);
        return entry;
    }

    @Override
    public void putEntries(Collection<CacheEntry<K, V>> entries) {
        ArrayList<MyEntry> am = new ArrayList<MyEntry>(entries.size());
        for (CacheEntry<K, V> entry : entries) {
            am.add(new MyEntry(entry));
        }
        putAllMyEntries(am);
    }

    @Override
    public void putEntry(CacheEntry<K, V> entry) {
        MyEntry me = new MyEntry(entry);
        putMyEntry(me);
    }

    @Override
    public CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter) {
        Collection col = Filters.filter((Collection) map.valueEntrySet(), filter);
        return new CopyQuery<K, V>((CacheEntry[]) col.toArray(new CacheEntry[0]));
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        MyEntry e = map.remove(key);
        evictionSupport.remove(e.policyIndex);
        eventSupport.removed(this, e);
        return e == null ? null : e.getValue();
    }

    @Override
    public void resetStatistics() {
        statistics.cacheReset();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return map.size();
    }

    @Override
    public void start() {
        super.start();
        managementSupport.start(this);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return map.valueValues();
    }

    private void added(MyEntry newEntry, MyEntry prev,
            CacheEntry<K, V> extendedInitializer) {
        if (extendedInitializer != null) {
            if (prev != null) {
                newEntry.policyIndex = prev.policyIndex;
                newEntry.creationTime = prev.creationTime;
                newEntry.version = prev.version + 1;
            } else {
                newEntry.creationTime = extendedInitializer.getCreationTime();
                newEntry.version = extendedInitializer.getVersion();
            }
        } else {
            if (prev != null) {
                newEntry.policyIndex = prev.policyIndex;
                newEntry.creationTime = prev.creationTime;
                newEntry.version = prev.version + 1;
            } else {
                if (newEntry.creationTime <= 0) {
                    newEntry.creationTime = getClock().absolutTime();
                }
            }
        }
        newEntry.lastUpdateTime = getClock().absolutTime();
    }

    private void evictNext() {
        MyEntry e = evictionSupport.evictNext();
        map.remove(e.getKey());
        eventSupport.removed(this, e);
    }

    private MyEntry newE(K key, V value, long timeout, TimeUnit unit) {
        return new MyEntry(key, value, expirationSupport.getDeadline(timeout, unit));
    }

    @Override
    protected V put0(K key, V value, long timeout, TimeUnit unit) {
        MyEntry me = newE(key, value, timeout, unit);
        MyEntry prev = putMyEntry(me);
        return prev == null ? null : prev.getValue();
    }

    @Override
    protected void putAll0(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
        checkMapForNulls(t);
        ArrayList<MyEntry> am = new ArrayList<MyEntry>();
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = t.entrySet()
                .iterator(); i.hasNext();) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            am.add(newE(e.getKey(), e.getValue(), timeout, unit));
        }
        putAllMyEntries(am);
    }

    void putAllMyEntries(Collection<MyEntry> entries) {
        Map<MyEntry, MyEntry> m = map.putAllValues(entries);
        for (Map.Entry<MyEntry, MyEntry> entry : m.entrySet()) {
            MyEntry mm = entry.getKey();
            MyEntry prev = entry.getValue();
            added(mm, prev, null);
            if (mm.policyIndex >= 0) {
                eventSupport.put(this, mm, prev);
            }
        }
    }

    MyEntry putMyEntry(MyEntry me) {
        K key = me.getKey();
        MyEntry prev = map.put(key, me);
        added(me, prev, null);
        me.lastUpdateTime = getClock().absolutTime();
        CacheEntry prv = storeSupport.storeEntry(me);
        // the check for me.policyIndex is for values rejected by a
        // cache policy
        if (me.policyIndex >= 0) {
            eventSupport.put(this, me, prev);
        }
        return prev;
    }

    void trimToSize(int newSize) {
        while (newSize < size()) {
            evictNext();
        }
    }

    final class MyEntry extends CacheEntryMap.Entry<K, V> implements CacheEntry<K, V> {

        double cost;

        long creationTime;

        /** The time at which this element expires. */
        long expirationTime;

        long hits;

        long lastAccessTime;

        long lastUpdateTime;

        /** the index in cache policy, is -1 if not used or initialized. */
        int policyIndex = -1;

        long size;

        long version = 1;

        MyEntry(CacheEntry<K, V> entry) {
            super(entry.getKey(), entry.getValue());
            expirationTime = expirationSupport.getExpirationTimeFromLoaded(entry);
            creationTime = entry.getCreationTime();
            version = entry.getVersion();
            lastUpdateTime = entry.getLastUpdateTime();
            lastAccessTime = entry.getLastAccessTime();
            hits = entry.getHits();
            size = entry.getSize();
            cost = entry.getCost();
        }

        MyEntry(K key, V value, long expirationTime) {
            super(key, value);
            this.expirationTime = expirationTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getCost()
         */
        public double getCost() {
            return cost;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getCreationTime()
         */
        public long getCreationTime() {
            return creationTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getExpirationTime()
         */
        public long getExpirationTime() {
            return expirationTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getHits()
         */
        public long getHits() {
            return hits;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastAccessTime()
         */
        public long getLastAccessTime() {
            return lastAccessTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastUpdateTime()
         */
        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getSize()
         */
        public long getSize() {
            return size;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getVersion()
         */
        public long getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }

        void accessed() {
            lastAccessTime = getClock().absolutTime();
        }

        void touched() {
            evictionSupport.touch(policyIndex);
        }
    }

    class MyMap extends CacheEntryMap<K, V, MyEntry> {
        @Override
        protected boolean elementAdded(MyEntry entry) {
            if (evictionSupport.isEnabled()) {
                // we want to remove an element before adding a new one.
                // because the element (entry) isn't being added until after
                // this method, to avoid that the policy wants to evict the
                // newly added element.
                // However this causes a problem if add rejects the entry and
                // size=maxCapacity (which is usually the case), then we have
                // evicted an entry without
                // actually needing to do so. But this is how it works for now.

                if (evictionSupport.isCapacityReached(UnlimitedCache.this.size())) {
                    evictNext();
                }
                // not initialized
                if (entry.policyIndex == -1) {
                    entry.policyIndex = evictionSupport.add(entry);
                    if (entry.policyIndex < 0) {
                        return false;
                    }
                }
            } else {
                // we tests against policyIndex to see if an value has been
                // added some times
                entry.policyIndex = Integer.MAX_VALUE;
            }
            return true;
        }
    }
}
