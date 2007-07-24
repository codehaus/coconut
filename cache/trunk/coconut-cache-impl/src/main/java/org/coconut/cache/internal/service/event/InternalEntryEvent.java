/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.core.AttributeMap;

/**
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
abstract class InternalEntryEvent<K, V> implements CacheEntryEvent.ItemAdded<K, V> {
    private final Cache<K, V> cache;

    private final CacheEntry<K, V> entry;

    InternalEntryEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
        this.cache = cache;
        this.entry = entry;
    }
    /** {@inheritDoc} */
    public AttributeMap getAttributes() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Cache<K, V> getCache() {
        return cache;
    }


    /** {@inheritDoc} */
    public double getCost() {
        return entry.getCost();
    }
    /** {@inheritDoc} */
    public long getCreationTime() {
        return entry.getCreationTime();
    }

    /** {@inheritDoc} */
    public long getExpirationTime() {
        return entry.getExpirationTime();
    }

    /** {@inheritDoc} */
    public long getHits() {
        return entry.getHits();
    }

    /** {@inheritDoc} */
    public K getKey() {
        return entry.getKey();
    }

    /** {@inheritDoc} */
    public long getLastAccessTime() {
        return entry.getLastAccessTime();
    }

    /** {@inheritDoc} */
    public long getLastUpdateTime() {
        return entry.getLastUpdateTime();
    }

    /** {@inheritDoc} */
    public long getSize() {
        return entry.getSize();
    }

    /** {@inheritDoc} */
    public V getValue() {
        return entry.getValue();
    }

    /** {@inheritDoc} */
    public V setValue(V value) {
        return entry.setValue(value);
    }
    /** {@inheritDoc} */
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

    static class AddedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemAdded<K, V> {

        /**
         * @param cache
         * @param entry
         */
        AddedEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
            super(cache, entry);
        }

        /** {@inheritDoc} */
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
            this.previous = previous;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemUpdated.NAME;
        }

        /** {@inheritDoc} */
        public V getPreviousValue() {
            return previous;
        }

        /** {@inheritDoc} */
        public boolean hasExpired() {
            return isExpired;
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

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemAccessed.NAME;
        }
        /** {@inheritDoc} */
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
        /** {@inheritDoc} */
        public AttributeMap getAttributes() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Cache<K, V> getCache() {
            return cache;
        }

        /** {@inheritDoc} */
        public double getCost() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public long getHits() {
            return 0;
        }

        /** {@inheritDoc} */
        public K getKey() {
            return key;
        }

        /** {@inheritDoc} */
        public long getLastAccessTime() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public long getLastUpdateTime() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemAccessed.NAME;
        }

        /** {@inheritDoc} */
        public long getSize() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public V getValue() {
            return null;
        }

        /** {@inheritDoc} */
        public boolean isHit() {
            return false;
        }

        /** {@inheritDoc} */
        public V setValue(V value) {
            throw new UnsupportedOperationException();
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

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemRemoved.NAME;
        }

        /** {@inheritDoc} */
        public boolean hasExpired() {
            return hasExpired;
        }

    }

    static <K, V> CacheEntryEvent<K, V> added(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new AddedEvent<K, V>(cache, entry);
    }

    static <K, V> CacheEntryEvent<K, V> evicted(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, false);
    }

    static <K, V> CacheEntryEvent<K, V> expired(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, true);
    }

    static <K, V> CacheEntryEvent<K, V> hit(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new HitEvent<K, V>(cache, entry);
    }

    static <K, V> CacheEntryEvent<K, V> miss(Cache<K, V> cache, K key) {
        return new MissEvent<K, V>(cache, key);
    }

    static <K, V> CacheEntryEvent<K, V> removed(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, false);
    }

    static <K, V> CacheEntryEvent<K, V> updated(Cache<K, V> cache,
            CacheEntry<K, V> entry, V previous) {
        return new ChangedEvent<K, V>(cache, entry, previous, false);
    }
}
