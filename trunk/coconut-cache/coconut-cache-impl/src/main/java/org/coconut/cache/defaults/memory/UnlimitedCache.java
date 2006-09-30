/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.management.NotCompliantMBeanException;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.CacheQuery;
import org.coconut.cache.Caches;
import org.coconut.cache.CacheConfiguration.ExpirationStrategy;
import org.coconut.cache.defaults.util.CacheEntryMap;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.AbstractCacheMXBean;
import org.coconut.cache.spi.CacheEventDispatcher;
import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.spi.EventDispatcher;
import org.coconut.cache.spi.LoaderSupport;
import org.coconut.cache.store.CacheStore;
import org.coconut.event.bus.DefaultEventBus;
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
@CacheSupport(CacheLoadingSuppurt = true, CacheEntrySupport = true, querySupport = true, ExpirationSupport = true, statisticsSupport = true, eventSupport = true)
@ThreadSafe(false)
public class UnlimitedCache<K, V> extends AbstractCache<K, V> implements
        ConcurrentMap<K, V> {

    private final CacheEntryMap<K, V, MyEntry> map;

    private int maxCapacity;

    private final ReplacementPolicy<MyEntry> cp;

    private int hits;

    private int misses;

    private long eventId = 0;

    private final CacheStore<K, V> store;

    private final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

    private final CacheConfiguration.StorageStrategy storageStrategy;

    private final CacheEventDispatcher<K, V> ed;

    private final EventBus<CacheEvent<K, V>> eb = new DefaultEventBus<CacheEvent<K, V>>();

    private final ExpirationStrategy expirationStrategy;

    private long defaultExpirationTime;

    private final Filter<CacheEntry<K, V>> expireFilter;

    protected boolean isExpired(CacheEntry<K, V> entry) {
        if (expireFilter != null && expireFilter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == Cache.NEVER_EXPIRE ? false : getClock().hasExpired(expTime);

    }

    public long getDefaultExpirationNanoTime() {
        return defaultExpirationTime;
    }

    void setDefaultExpirationTime(long nanos) {
        defaultExpirationTime = nanos;
    }

    public ExpirationStrategy getExpirationStrategy() {
        return expirationStrategy;
    }

    public UnlimitedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        cp = conf.eviction().getPolicy();
        maxCapacity = conf.eviction().getMaximumCapacity();
        if (maxCapacity != Integer.MAX_VALUE && cp == null) {
            throw new IllegalArgumentException("Must define a cache policy");
        }
        // ed = new EventDispatcher(this, conf, EventHandlers
        // .toOfferable(EventHandlers.toSystemOut()));
        ed = new EventDispatcher(this, conf, eb);
        store = conf.backend().getStore();
        storageStrategy = CacheConfiguration.StorageStrategy.WRITE_THROUGH;
        this.expirationStrategy = conf.expiration().getStrategy();
        this.defaultExpirationTime = conf.expiration().getDefaultTimeout(
                TimeUnit.NANOSECONDS);
        expireFilter = conf.expiration().getFilter();

        // ed = null;
        // important must be last, because of final value being inlined.
        map = new MyMap();
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
        loader = LoaderSupport.getLoader(conf);
    }

    @SuppressWarnings("unchecked")
    public UnlimitedCache() {
        this(CacheConfiguration.DEFAULT_CONFIGURATION);
    }

    @SuppressWarnings("unchecked")
    public UnlimitedCache(Map<K, V> map) {
        this(CacheConfiguration.DEFAULT_CONFIGURATION);
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        putAll(map);
    }

    /**
     * Returns the next id used for sequencing events.
     * 
     * @return the next id used for sequencing events.
     */
    private long nextSequenceId() {
        return ++eventId;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return map.valueValues();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return map.valueEntrySet();
    }

    /** {@inheritDoc} */
    @Override
    public Future<?> load(final K key) {
        return LoaderSupport.loadEntry(this, loader, key);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAll(java.util.Collection)
     */
    @Override
    public Future<?> loadAll(Collection<? extends K> keys) {
        return LoaderSupport.loadAllEntries(this, loader, keys);
    }

    void trimToSize(int newSize) {
        while (newSize < size()) {
            MyEntry key = cp.evictNext();
            MyEntry prev = map.remove(key.getKey());
            V value = prev == null ? null : prev.getValueSilent();
            if (value != null && ed.doNotifyRemoved()) {
                ed.notifyRemoved(nextSequenceId(), prev.getKey(), value, false, prev);
            }
        }
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        MyEntry prev = map.remove(key);
        if (cp != null) {
            cp.remove(prev.policyIndex);
        }
        V value = prev == null ? null : prev.getValueSilent();
        if (value != null && ed.doNotifyRemoved()) {
            ed.notifyRemoved(nextSequenceId(), prev.getKey(), value, false, prev);
        }
        return value;

    }

    private MyEntry newE(K key, V value, long timeout, TimeUnit unit) {
        final MyEntry me;
        if (timeout == Cache.NEVER_EXPIRE) {
            me = new MyEntry(key, value);
        } else if (timeout == Cache.DEFAULT_EXPIRATION) {
            long time = getClock().getDeadlineFromNow(getDefaultExpirationNanoTime(),
                    TimeUnit.NANOSECONDS);
            me = new MyEntry(key, value, time);
        } else {
            long time = getClock().getDeadlineFromNow(timeout, unit);
            me = new MyEntry(key, value, time);
        }
        return me;
    }

    @Override
    protected V put0(K key, V value, long timeout, TimeUnit unit) {
        MyEntry me = newE(key, value, timeout, unit);
        MyEntry prev = putMyEntry(me);
        return prev == null ? null : prev.getValueSilent();
    }

    @Override
    protected void putEntry(CacheEntry<K, V> entry) {
        MyEntry me = new MyEntry(entry);
        putMyEntry(me);
        // return me;
    }

    protected MyEntry putMyEntry(MyEntry me) {
        K key = me.getKey();
        MyEntry prev = map.put(key, me);
        added(me, prev, null);
        me.lastUpdateTime = getClock().absolutTime();
        V val = prev == null ? null : prev.getValueSilent();
        // the check for me.policyIndex is for values rejected by a
        // cache policy
        V value = me.getValueSilent();
        if (ed != null && me.policyIndex >= 0) {
            if (prev == null) {
                if (ed.doNotifyAdded()) {
                    ed.notifyAdded(nextSequenceId(), key, value, me);
                }
            } else {
                if (ed.doNotifyChanged() && !value.equals(val)) {
                    ed.notifyChanged(nextSequenceId(), key, value, val, me);
                }
            }
        }
        return prev;

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

    @Override
    protected void putEntries(Collection<CacheEntry<K, V>> entries) {
        ArrayList<MyEntry> am = new ArrayList<MyEntry>(entries.size());
        for (CacheEntry<K, V> entry : entries) {
            am.add(new MyEntry(entry));
        }
        putAllMyEntries(am);
    }

    protected void putAllMyEntries(Collection<MyEntry> entries) {
        Map<MyEntry, MyEntry> m = map.putAllValues(entries);

        boolean postEvents = ed != null && (ed.doNotifyChanged() || ed.doNotifyAdded());
        for (Map.Entry<MyEntry, MyEntry> entry : m.entrySet()) {
            MyEntry mm = entry.getKey();
            MyEntry prev = entry.getValue();
            added(mm, prev, null);
            if (postEvents) {
                if (mm.policyIndex >= 0) {
                    V preVal = prev == null ? null : prev.getValueSilent();
                    if (prev == null) {
                        if (ed.doNotifyAdded()) {
                            ed.notifyAdded(nextSequenceId(), mm.getKey(), mm.getValue(),
                                    mm);
                        }
                    } else {
                        if (ed.doNotifyChanged() && !mm.getValue().equals(preVal)) {
                            ed.notifyChanged(eventId++, mm.getKey(), mm.getValue(),
                                    preVal, mm);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected V get0(K key, boolean isPeeking) {
        boolean wasLoaded = false;
        MyEntry entry = map.get(key);
        MyEntry newEntry = null;
        V value = entry == null ? null : entry.getValue();
        V prev = value;
        // TODO handle null value
        if (!isPeeking) {
            if (value == null) {
                CacheEntry<K, V> ce = LoaderSupport.loadEntryNow(this, loader, key);
                if (ce != null) {
                    value = ce.getValue();
                    wasLoaded = true;
                    newEntry = new MyEntry(ce);
                    added(newEntry, null, ce);
                    map.put(key, newEntry);
                    newEntry.lastAccessTime = getClock().absolutTime();
                }

                misses++;
                ed.notifyAccessed(nextSequenceId(), key, value, newEntry, false);
                if (wasLoaded) {
                    ed.notifyAdded(nextSequenceId(), key, value, newEntry);
                }
            } else {
                if (getExpirationStrategy() == ExpirationStrategy.ON_EVICT) {
                    hits++;
                    entry.hits++;
                    entry.lastAccessTime = getClock().absolutTime();
                    ed.notifyAccessed(nextSequenceId(), key, value, entry, true);
                    // what if we need to reload value, miss right but what
                    // about
                    // cache policy?
                } else {
                    if (isExpired(entry)) {
                        CacheEntry<K, V> e = LoaderSupport
                                .loadEntryNow(this, loader, key);
                        value = e == null ? null : e.getValue();
                        if (value != null) {
                            wasLoaded = true;
                            MyEntry me = new MyEntry(key, value);
                            map.put(key, me);
                        } else {
                            // entry dead
                            map.remove(key);
                            // TODO notify removal
                        }
                        misses++;
                        entry.lastAccessTime = getClock().absolutTime();
                        ed.notifyAccessed(nextSequenceId(), key, value, entry, false);
                        if (wasLoaded) {
                            ed.notifyChanged(nextSequenceId(), key, value, prev, entry);
                        }
                    } else {
                        hits++;
                        entry.lastAccessTime = getClock().absolutTime();
                        ed.notifyAccessed(nextSequenceId(), key, value, entry, true);
                    }
                }
                if (cp != null) {
                    cp.touch(entry.policyIndex);
                }
            }
        }
        return value;
    }

    class MyMap extends CacheEntryMap<K, V, MyEntry> {
        @Override
        protected boolean elementAdded(MyEntry entry) {
            if (cp != null) {
                // we want to remove an element before adding a new one.
                // because the element (entry) isn't being added until after
                // this method, to avoid that the policy wants to evict the
                // newly added element.
                // However this causes a problem if add rejects the entry and
                // size=maxCapaciry, then we have evicted an entry without
                // actually needing to do so. But this is how it works for now.

                if (UnlimitedCache.this.size() == maxCapacity) {
                    MyEntry key = cp.evictNext();
                    MyEntry prev = map.remove(key.getKey(), false);
                    V value = prev == null ? null : prev.getValueSilent();
                    if (value != null && ed.doNotifyRemoved()) {
                        ed.notifyRemoved(nextSequenceId(), prev.getKey(), value, false,
                                prev);
                    }
                }
                // not initialized
                if (entry.policyIndex == -1) {
                    entry.policyIndex = cp.add(entry);
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

    @Override
    public CacheEntry<K, V> getEntry(K key) {
        MyEntry entry = map.get(key);
        return entry;
    }

    class MyEntry extends CacheEntryMap.Entry<K, V> implements CacheEntry<K, V> {

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }

        /** The time at which this element expires. */
        long expirationTime;

        /** the index in cache policy, is -1 if not used or initialized. */
        int policyIndex = -1;

        long creationTime;

        long version = 1;

        long lastUpdateTime;

        long lastAccessTime;

        long hits;

        long size;

        double cost;

        MyEntry(K key, V value, long expirationTime) {
            super(key, value);
            this.expirationTime = expirationTime;
        }

        MyEntry(CacheEntry<K, V> entry) {
            super(entry.getKey(), entry.getValue());
            expirationTime = entry.getExpirationTime();
            creationTime = entry.getCreationTime();
            version = entry.getVersion();
            lastUpdateTime = entry.getLastUpdateTime();
            lastAccessTime = entry.getLastAccessTime();
            hits = entry.getHits();
            size = entry.getSize();
            cost = entry.getCost();
        }

        MyEntry(K key, V value) {
            this(key, value, Long.MAX_VALUE);
        }

        @Override
        public V getValueSilent() {
            return super.getValue();
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
         * @see org.coconut.cache.CacheEntry#getVersion()
         */
        public long getVersion() {
            return version;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getSize()
         */
        public long getSize() {
            return size;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getCost()
         */
        public double getCost() {
            return cost;
        }
    }

    @Override
    public CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter) {
        Collection col = Filters.filter((Collection) map.valueEntrySet(), filter);
        return new MyCacheQuery<K, V>((CacheEntry[]) col.toArray(new CacheEntry[0]));
    }

    static class MyCacheQuery<K, V> implements CacheQuery<K, V> {

        final CacheEntry<K, V>[] entries;

        int index;

        private Iterator<CacheEntry<K, V>> iterator;

        MyCacheQuery(CacheEntry<K, V>[] entries) {
            this.entries = entries;
        }

        /**
         * integer.max==end??
         * 
         * @see org.coconut.cache.CacheQuery#getCurrentIndex()
         */
        public int getCurrentIndex() {
            return index;
        }

        /**
         * @see org.coconut.cache.CacheQuery#getNext(int)
         */
        public List<CacheEntry<K, V>> getNext(int count) {
            int realCount = count + index > entries.length ? entries.length - index
                    : count;
            CacheEntry<K, V>[] e = new CacheEntry[realCount];
            System.arraycopy(entries, index, e, 0, realCount);
            index += realCount;
            return Arrays.asList(e);
        }

        /**
         * @see org.coconut.cache.CacheQuery#getTotalCount()
         */
        public int getTotalCount() {
            return entries.length;
        }

        /**
         * @see org.coconut.cache.CacheQuery#isIndexable()
         */
        public boolean isIndexable() {
            return false;
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<CacheEntry<K, V>> iterator() {
            if (iterator == null) {
                iterator = new Iter();
            }
            return iterator;
        }

        class Iter implements Iterator<CacheEntry<K, V>> {

            /**
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                return index < entries.length;
            }

            /**
             * @see java.util.Iterator#next()
             */
            public CacheEntry<K, V> next() {
                if (index >= entries.length) {
                    throw new NoSuchElementException();
                }
                return entries[index++];
            }

            /**
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * @see org.coconut.cache.CacheQuery#get(int, int)
         */
        public List<CacheEntry<K, V>> get(int from, int to) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.CacheQuery#getAll()
         */
        public List<CacheEntry<K, V>> getAll() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return index < entries.length;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public CacheEntry<K, V> next() {
            if (index >= entries.length) {
                throw new NoSuchElementException();
            }
            return entries[index++];
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void evict() {
        Iterator<MyEntry> iter = map.values().iterator();
        for (Iterator<MyEntry> iterator = map.values().iterator(); iterator.hasNext();) {
            MyEntry m = (MyEntry) iterator.next();
            if (isExpired(m)) {
                iter.remove();
                V value = m.getValueSilent();
                if (value != null && ed.doNotifyRemoved()) {
                    ed.notifyRemoved(nextSequenceId(), m.getKey(), value, true, m);
                }
            }
        }
    }

    @Override
    public CacheMXBean getInfo() {
        return super.getInfo();
    }

    class jmxInfo extends AbstractCacheMXBean {

        /**
         * @param cache
         * @param bus
         * @param messages
         * @throws NotCompliantMBeanException
         */
        public jmxInfo(Cache cache, EventBus bus, Properties messages)
                throws NotCompliantMBeanException {
            super(cache, bus, messages);
        }

        @Override
        public long getDefaultExpiration() {
            return super.getDefaultExpiration();
        }

        @Override
        public void setMaximumSize(long maximumCapacity) {
            super.setMaximumSize(maximumCapacity);
        }

        @Override
        public void trimToSize(int newSize) {
            UnlimitedCache.this.trimToSize(newSize);
            super.trimToSize(newSize);
        }

        public void handle(Object event) {
        }

    }

    @Override
    public HitStat getHitStat() {
        return Caches.newImmutableHitStat(hits, misses);
    }

    @Override
    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    @Override
    public EventBus<CacheEvent<K, V>> getEventBus() {
        return eb;
    }
}
