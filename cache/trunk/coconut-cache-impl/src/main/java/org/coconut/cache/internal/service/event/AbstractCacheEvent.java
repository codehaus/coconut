/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.internal.service.event.DefaultCacheEventService.NotificationTransformer;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheEvent<K, V> implements CacheEvent<K, V>, Serializable,
        NotificationTransformer {

    private final Cache<K, V> cache;

    private final long id;

    private final String name;

    /**
     * @param id
     * @param name
     * @param cache
     */
    public AbstractCacheEvent(final long id, final String name, final Cache<K, V> cache) {
        this.id = id;
        this.name = name;
        this.cache = cache;
    }

    /**
     * @see org.coconut.cache.CacheEvent#getAttributes()
     */
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    /**
     * @see org.coconut.cache.CacheEvent#getCache()
     */
    public final Cache<K, V> getCache() {
        return cache;
    }

    /**
     * @see org.coconut.cache.CacheEvent#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * @see org.coconut.core.Sequenced#getSequenceID()
     */
    public final long getSequenceID() {
        return id;
    }

    /**
     * @see org.coconut.cache.spi.jmx.NotificationTransformer#notification(java.lang.Object)
     */
    public Notification notification(Object source) {
        return new Notification(getName(), source, getSequenceID(), toString());
    }
    
    abstract static class AbstractCacheItemEvent<K, V> extends AbstractCacheEvent<K, V> {

        private final CacheEntry<K, V> ce;

        private final K key;

        private final V value;

        /**
         * @param id
         * @param name
         * @param cache
         */
        public AbstractCacheItemEvent(long id, String name, Cache<K, V> cache,
                final CacheEntry<K, V> ce, final K key, final V value) {
            super(id, name, cache);
            this.key = key;
            this.value = value;
            this.ce = ce;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public final CacheEntry<K, V> getEntry() {
            return ce;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public final K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public final V getValue() {
            return value;
        }

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public final V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }
    }

    final static class AccessedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed<K, V> {

        private static final long serialVersionUID = 3545235834329511987L;

        private final boolean wasHit;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public AccessedEvent(final Cache<K, V> cache, final long sequenceID,
                final CacheEntry<K, V> ce, final K key, final V value, boolean wasHit) {
            super(sequenceID, ItemAccessed.NAME, cache, ce, key, value);

            this.wasHit = wasHit;
        }

        public boolean isHit() {
            return wasHit;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getSequenceID());
            builder.append(":");
            builder.append(getName());
            builder.append("[key = ");
            builder.append(getKey());
            builder.append(", value = ");
            builder.append(getValue());
            builder.append("]");
            return builder.toString();
        }

    }

    final static class AddedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            org.coconut.cache.service.event.CacheEntryEvent.ItemAdded<K, V> {

        private static final long serialVersionUID = 3545235834329511987L;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public AddedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value) {
            super(sequenceID, ItemAdded.NAME, cache, ce, key, value);
        }

        public boolean hasExpired() {
            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getSequenceID());
            builder.append(":");
            builder.append(getName());
            builder.append("   [key = ");
            builder.append(getKey());
            builder.append(", value = ");
            builder.append(getValue());
            builder.append("]");
            return builder.toString();
        }

    }

    final static class ChangedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated<K, V> {

        private static final long serialVersionUID = 3545235834329511987L;

        private final V previous;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public ChangedEvent(final Cache<K, V> cache, final long sequenceID,
                final CacheEntry<K, V> ce, final K key, final V value, final V previous) {
            super(sequenceID, ItemUpdated.NAME, cache, ce, key, value);
            this.previous = previous;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent.ItemUpdated#getPreviousValue()
         */
        public V getPreviousValue() {
            return previous;
        }

        public boolean hasExpired() {
            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getSequenceID());
            builder.append(":");
            builder.append(getName());
            builder.append("   [key = ");
            builder.append(getKey());
            builder.append(", value = ");
            builder.append(getValue());
            builder.append(", previousValue = ");
            builder.append(getPreviousValue());
            builder.append("]");
            return builder.toString();
        }
    }

    final static class ClearEvent<K, V> extends AbstractCacheEvent<K, V> implements
            CacheEvent.CacheCleared<K, V> {
        private static final long serialVersionUID = 3258410651134211896L;

        private final int previousSize;

        public ClearEvent(final Cache<K, V> cache, final long sequenceID, int previousSize) {
            super(sequenceID, CacheEvent.CacheCleared.NAME, cache);
            this.previousSize = previousSize;
        }

        public int getPreviousSize() {
            return previousSize;
        }

    }

    final static class EvictedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            org.coconut.cache.service.event.CacheEntryEvent.ItemAdded<K, V> {

        private static final long serialVersionUID = 3545235834329511987L;

        private final long nanolifeTime;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public EvictedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value, final long nanolifeTime) {
            super(sequenceID, ItemAdded.NAME, cache, ce, key, value);
            this.nanolifeTime = nanolifeTime;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent.ItemEvicted#getTimeToLive(java.util.concurrent.TimeUnit)
         */
        public long getTimeToLive(TimeUnit unit) {
            return unit.convert(nanolifeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getSequenceID());
            builder.append(":");
            builder.append(getName());
            builder.append(" [key = ");
            builder.append(getKey());
            builder.append(", value = ");
            builder.append(getValue());
            builder.append(", Expires At = ");
            DateFormat df = DateFormat.getDateTimeInstance();
            Date d = new Date(System.currentTimeMillis()
                    + getTimeToLive(TimeUnit.MILLISECONDS));
            builder.append(df.format(d));
            builder.append("]");
            return builder.toString();
        }

    }

    final static class RemovedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved<K, V> {

        private static final long serialVersionUID = 3545235834329511987L;

        private final boolean isExpired;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public RemovedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value, boolean isExpired) {
            super(sequenceID, ItemRemoved.NAME, cache, ce, key, value);
            this.isExpired = isExpired;
        }

        public boolean hasExpired() {
            return isExpired;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getSequenceID());
            builder.append(":");
            builder.append(getName());
            builder.append("   [key = ");
            builder.append(getKey());
            builder.append(", value = ");
            builder.append(getValue());
            builder.append("]");
            return builder.toString();
        }
    }

    final static class ResetCacheStatisticsEvent<K, V> extends AbstractCacheEvent<K, V>
            implements CacheEvent.CacheStatisticsReset<K, V> {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3258410651134211896L;

        private final HitStat hitstat;

        /**
         * @param cache
         * @param sequenceID
         * @param hitstat
         */
        public ResetCacheStatisticsEvent(final Cache<K, V> cache, final long sequenceID,
                final HitStat hitstat) {
            super(sequenceID, CacheEvent.CacheStatisticsReset.NAME, cache);
            this.hitstat = hitstat;
        }

        /**
         * @see org.coconut.cache.CacheInstanceEvent.CacheStatisticsReset#getPreviousHitStat()
         */
        public HitStat getPreviousHitStat() {
            return hitstat;
        }

    }
}
