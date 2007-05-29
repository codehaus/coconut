/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.event.CacheEntryEvent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class InternalEntryEvent<K, V> implements CacheEntryEvent.ItemAdded<K, V> {
    private final Cache<K, V> cache;

    private final CacheEntry<K, V> entry;

    InternalEntryEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
        this.cache = cache;
        this.entry = entry;
    }

    /**
     * @see org.coconut.cache.service.event.CacheEvent#getCache()
     */
    public Cache<K, V> getCache() {
        return cache;
    }

    /**
     * @return
     * @see org.coconut.cache.policy.PolicyObject#getCost()
     */
    public double getCost() {
        return entry.getCost();
    }

    /**
     * @return
     * @see org.coconut.cache.CacheEntry#getCreationTime()
     */
    public long getCreationTime() {
        return entry.getCreationTime();
    }

    /**
     * @return
     * @see org.coconut.cache.CacheEntry#getExpirationTime()
     */
    public long getExpirationTime() {
        return entry.getExpirationTime();
    }

    /**
     * @return
     * @see org.coconut.cache.policy.PolicyObject#getHits()
     */
    public long getHits() {
        return entry.getHits();
    }

    /**
     * @return
     * @see java.util.Map.Entry#getKey()
     */
    public K getKey() {
        return entry.getKey();
    }

    /**
     * @return
     * @see org.coconut.cache.CacheEntry#getLastAccessTime()
     */
    public long getLastAccessTime() {
        return entry.getLastAccessTime();
    }

    /**
     * @return
     * @see org.coconut.cache.CacheEntry#getLastUpdateTime()
     */
    public long getLastUpdateTime() {
        return entry.getLastUpdateTime();
    }

    /**
     * @return
     * @see org.coconut.cache.policy.PolicyObject#getSize()
     */
    public long getSize() {
        return entry.getSize();
    }

    /**
     * @return
     * @see java.util.Map.Entry#getValue()
     */
    public V getValue() {
        return entry.getValue();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(":");
        builder.append("[key = ");
        builder.append(getKey());
        builder.append(", value = ");
        builder.append(getValue());
        builder.append("]");
        return builder.toString();
    }

    static <K, V> CacheEntryEvent<K, V> added(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new AddedEvent<K, V>(cache, entry);
    }

    static <K, V> CacheEntryEvent<K, V> updated(Cache<K, V> cache,
            CacheEntry<K, V> entry, V previous) {
        return new ChangedEvent<K, V>(cache, entry, previous, false);
    }

    static <K, V> CacheEntryEvent<K, V> expired(Cache<K, V> cache,
            CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, true);
    }

    static <K, V> CacheEntryEvent<K, V> evicted(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, false);
    }

    static <K, V> CacheEntryEvent<K, V> removed(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, false);
    }

    static <K, V> CacheEntryEvent<K, V> miss(Cache<K, V> cache, K key) {
        return new MissEvent<K, V>(cache, key);
    }

    static <K, V> CacheEntryEvent<K, V> hit(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new HitEvent<K, V>(cache, entry);
    }

    /**
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    public V setValue(V value) {
        return entry.setValue(value);
    }

    static class AddedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemAdded<K, V> {

        /**
         * @param cache
         * @param entry
         */
        AddedEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
            super(cache, entry);
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEntryEvent.ItemAdded.NAME;
        }
    }

    static class ChangedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemUpdated<K, V> {
        private boolean isExpired;

        private V previous;

        /**
         * @param cache
         * @param entry
         */
        ChangedEvent(Cache<K, V> cache, CacheEntry<K, V> entry, V previous,
                boolean isExpired) {
            super(cache, entry);
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEntryEvent.ItemAdded.NAME;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated#getPreviousValue()
         */
        public V getPreviousValue() {
            return previous;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated#hasExpired()
         */
        public boolean hasExpired() {
            return isExpired;
        }
    }

    static class RemovedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemRemoved<K, V> {

        private boolean hasExpired;

        /**
         * @param cache
         * @param entry
         */
        RemovedEvent(Cache<K, V> cache, CacheEntry<K, V> entry, boolean hasExpired) {
            super(cache, entry);
            this.hasExpired = hasExpired;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEntryEvent.ItemRemoved.NAME;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved#hasExpired()
         */
        public boolean hasExpired() {
            return hasExpired;
        }

    }

    static class HitEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemAccessed<K, V> {
        /**
         * @param cache
         * @param entry
         */
        HitEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
            super(cache, entry);
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEntryEvent.ItemAccessed.NAME;
        }

        public boolean isHit() {
            return true;
        }
    }

    static class MissEvent<K, V> implements CacheEntryEvent.ItemAccessed<K, V> {
        private final Cache<K, V> cache;

        private final K key;

        /**
         * @param cache
         * @param entry
         */
        MissEvent(Cache<K, V> cache, K key) {
            this.cache = cache;
            this.key = key;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEntryEvent.ItemAccessed.NAME;
        }

        public boolean isHit() {
            return false;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getCreationTime()
         */
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.CacheEntry#getExpirationTime()
         */
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastAccessTime()
         */
        public long getLastAccessTime() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastUpdateTime()
         */
        public long getLastUpdateTime() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.CacheEntry#getVersion()
         */
        public long getVersion() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see java.util.Map.Entry#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see java.util.Map.Entry#getValue()
         */
        public V getValue() {
            return null;
        }

        /**
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.PolicyObject#getCost()
         */
        public double getCost() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.PolicyObject#getHits()
         */
        public long getHits() {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * @see org.coconut.cache.policy.PolicyObject#getSize()
         */
        public long getSize() {
            throw new UnsupportedOperationException();
        }
    }
}
