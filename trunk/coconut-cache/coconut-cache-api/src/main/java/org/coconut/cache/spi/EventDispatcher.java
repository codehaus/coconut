/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.Cache.HitStat;
import org.coconut.core.Offerable;
import org.coconut.event.bus.EventBus;
import org.coconut.cache.CacheEntryEvent.*;

@SuppressWarnings("hiding")
public class EventDispatcher<K, V> implements CacheEventDispatcher<K, V> {

    public interface NotificationTransformer {
        Notification notification(Object source);
    }

    private final CacheConfiguration<K, V> conf;

    private final Cache<K, V> cache;

    private final Offerable<CacheEvent<K, V>> offerable;

    public EventDispatcher(Cache<K, V> cache, CacheConfiguration<K, V> conf,
            Offerable<CacheEvent<K, V>> offerable) {
        this.cache = cache;
        this.conf = conf;
        this.offerable = offerable;
    }

    public EventBus<CacheEvent<K, V>> getBus() {
        return null;
    }

    public boolean doNotifyChanged() {
        return true;
    }

    public void notifyAdded(final long sequenceID, final K key, final V value,
            CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new AddedEvent<K, V>(cache, entry, sequenceID, key, value);
        dispatch(e);
    }

    public void notifyChanged(final long sequenceID, final K key, final V value,
            final V oldValue, CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new ChangedEvent<K, V>(cache, sequenceID, entry, key, value,
                oldValue);
        dispatch(e);
    }

    public boolean doNotifyCacheCleared() {
        return true;
    }

    public boolean doNotifyRemoved() {
        return true;
    }

    public boolean doNotifyAssessedCleared() {
        return true;
    }

    public void notifyCacheCleared(final long sequenceID, final int previousSize) {
        CacheEvent<K, V> e = new ClearEvent<K, V>(cache, sequenceID, previousSize);
        dispatch(e);
    }

    public void notifyCacheGet(final long sequenceID, final K key, final V value,
            CacheEntry<K, V> entry, boolean wasHit) {
        if (value != null) {
            CacheEvent<K, V> e = new AccessedEvent<K, V>(cache, sequenceID, entry, key,
                    value, wasHit);
            dispatch(e);
        }
    }

    // public void notifyCacheItemLoaded(final long sequenceID, CacheLoader<K,
    // V> loader, final K key,
    // final V value) {
    // if (value != null) {
    // CacheEvent<K, V> e = new ChangedEvent<K, V>(cache, sequenceID, key,
    // value, null);
    // dispatch(e);
    // }
    // }
    //
    public void notifyRemoved(final long sequenceID, final K key, final V value,
            boolean isExpired, final CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, sequenceID, key, value,
                isExpired);
        dispatch(e);
    }

    public void notifyCacheStatisticsReset(final long squenceID, HitStat stat) {
        CacheEvent<K, V> e = new ResetCacheStatisticsEvent<K, V>(cache, squenceID, stat);
        dispatch(e);
    }

    public void notifyAccessed(final long squenceID, K key, V value,
            CacheEntry<K, V> entry, boolean wasHit) {
        AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, squenceID, entry, key,
                value, wasHit);
        dispatch(e);
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    abstract static class AbstractCacheEvent<K, V> implements CacheEvent<K, V>,
            Serializable, NotificationTransformer {

        private final long id;

        private final String name;

        private final Cache<K, V> cache;

        /**
         * @param id
         * @param name
         * @param cache
         */
        public AbstractCacheEvent(final long id, final String name,
                final Cache<K, V> cache) {
            this.id = id;
            this.name = name;
            this.cache = cache;
        }

        /**
         * @see org.coconut.cache.spi.jmx.NotificationTransformer#notification(java.lang.Object)
         */
        public Notification notification(Object source) {
            return new Notification(getName(), source, getSequenceID(), toString());
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
    }

    abstract static class AbstractCacheItemEvent<K, V> extends AbstractCacheEvent<K, V> {

        private final K key;

        private final V value;

        private final CacheEntry<K, V> ce;

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

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public final CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    final static class EvictedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            ItemAdded<K, V> {

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
            ItemRemoved<K, V> {

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
    }

    final static class AccessedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            ItemAccessed<K, V> {

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

    final static class ChangedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            ItemUpdated<K, V> {

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

        public boolean hasExpired() {
            return false;
        }
    }

    final static class AddedEvent<K, V> extends AbstractCacheItemEvent<K, V> implements
            ItemAdded<K, V> {

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

        public boolean hasExpired() {
            return false;
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

    public boolean doNotifyClear() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean doNotifyAccessed() {
        return false;
    }

    public boolean doNotifyAdded() {
        return true;
    }

    public boolean showOldValueForChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.coconut.cache.spi.CacheEventDispatcher#isEnabled()
     */
    public boolean isEnabled() {
        return true;
    }

}