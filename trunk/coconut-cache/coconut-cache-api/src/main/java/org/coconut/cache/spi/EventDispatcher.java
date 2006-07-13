/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.CacheItemEvent.ItemAccessed;
import org.coconut.cache.CacheItemEvent.ItemAdded;
import org.coconut.cache.CacheItemEvent.ItemRemoved;
import org.coconut.cache.CacheItemEvent.ItemUpdated;
import org.coconut.core.EventHandlers;
import org.coconut.core.Offerable;
import org.coconut.event.bus.EventBus;

@SuppressWarnings("hiding")
public class EventDispatcher<K, V> implements CacheEventDispatcher<K, V>, Serializable {

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

    public void notifyChanged(final long sequenceID, final K key, final V value, final V oldValue,
            CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new ChangedEvent<K, V>(cache, sequenceID, entry, key, value, oldValue);
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
            CacheEvent<K, V> e = new AccessedEvent<K, V>(cache, sequenceID, entry, key, value,
                    wasHit);
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
    public void notifyRemoved(final long sequenceID, final K key,
            final V value, boolean isExpired, final CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, sequenceID, key, value, isExpired);
        dispatch(e);
    }

    public void notifyCacheStatisticsReset(final long squenceID, HitStat stat) {
        CacheEvent<K, V> e = new ResetCacheStatisticsEvent<K, V>(cache, squenceID, stat);
        dispatch(e);
    }

    public void notifyAccessed(final long squenceID, K key, V value, CacheEntry<K, V> entry,
            boolean wasHit) {
        AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, squenceID, entry, key, value, wasHit);
        dispatch(e);
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    final static class EvictedEvent<K, V> implements ItemAdded<K, V>, Serializable,
            NotificationTransformer {

        private static final long serialVersionUID = 3545235834329511987L;

        private final transient Cache<K, V> cache;

        private final long sequenceID;

        private final long nanolifeTime;

        private final K key;

        private final V value;

        private final CacheEntry<K, V> ce;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public EvictedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value, final long nanolifeTime) {
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.nanolifeTime = nanolifeTime;
            this.key = key;
            this.value = value;
            this.ce = ce;
        }

        /**
         * @see org.coconut.cache.spi.jmx.NotificationTransformer#notification(java.lang.Object)
         */
        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return ItemAdded.NAME;
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
            builder.append(key);
            builder.append(", value = ");
            builder.append(value);
            builder.append(", Expires At = ");
            DateFormat df = DateFormat.getDateTimeInstance();
            Date d = new Date(System.currentTimeMillis() + getTimeToLive(TimeUnit.MILLISECONDS));
            builder.append(df.format(d));
            builder.append("]");
            return builder.toString();
        }

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    // final static class LoadedEvent<K, V> implements
    // CacheItemEvent.Loaded<K, V>, Serializable, NotificationTransformer {
    //
    // private static final long serialVersionUID = 3545235834329511987L;
    //
    // private final transient Cache<K, V> cache;
    //
    // private final long sequenceID;
    //
    // private final transient CacheLoader<K, V> loader;
    //
    // private final K key;
    //
    // private final V value;
    //
    // /**
    // * @param cache
    // * @param sequenceID
    // * @param isTimeouted
    // * @param key
    // * @param value
    // */
    // public LoadedEvent(final Cache<K, V> cache, final long sequenceID,
    // final K key, final V value, final CacheLoader<K, V> loader) {
    // super();
    // this.cache = cache;
    // this.sequenceID = sequenceID;
    // this.loader = loader;
    // this.key = key;
    // this.value = value;
    // }
    //
    // public Notification notification(Object source) {
    // return new Notification(getName(), source, sequenceID, toString());
    // }
    //
    // public long getSequenceID() {
    // return sequenceID;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheItemEvent#getKey()
    // */
    // public K getKey() {
    // return key;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheItemEvent#getValue()
    // */
    // public V getValue() {
    // return value;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheEvent#getCache()
    // */
    // public Cache<K, V> getCache() {
    // return cache;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheEvent#getName()
    // */
    // public String getName() {
    // return CacheItemEvent.Loaded.NAME;
    // }
    //
    // /**
    // * @see
    // org.coconut.cache.CacheItemEvent.Evicted#getTimeToLive(java.util.concurrent.TimeUnit)
    // */
    // public CacheLoader<K, V> getLoader() {
    // return loader;
    // }
    //
    // public String toString() {
    // StringBuilder builder = new StringBuilder();
    // builder.append(getSequenceID());
    // builder.append(":");
    // builder.append(getName());
    // builder.append(" [key = ");
    // builder.append(getKey());
    // builder.append(", value = ");
    // builder.append(getValue());
    // builder.append(", Loader = ");
    // builder.append(loader);
    // builder.append("]");
    // return builder.toString();
    // }
    //
    // /**
    // * @see java.util.Map$Entry#setValue(V)
    // */
    // public V setValue(V value) {
    // throw new UnsupportedOperationException("setValue not supported");
    // }
    // }

    final static class RemovedEvent<K, V> implements ItemRemoved<K, V>, Serializable,
            NotificationTransformer {

        private static final long serialVersionUID = 3545235834329511987L;

        private final transient Cache<K, V> cache;

        private final long sequenceID;

        private final K key;

        private final V value;

        private final boolean isExpired;

        private final CacheEntry<K, V> ce;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public RemovedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value, boolean isExpired) {
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.key = key;
            this.value = value;
            this.isExpired = isExpired;
            this.ce = ce;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return ItemRemoved.NAME;
        }

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        public boolean hasExpired() {
            return isExpired;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    // final static class ExpiredEvent<K, V> implements
    // CacheItemEvent.Expired<K, V>, Serializable, NotificationTransformer {
    //
    // private static final long serialVersionUID = 3545235834329511987L;
    //
    // private final transient Cache<K, V> cache;
    //
    // private final long sequenceID;
    //
    // private final K key;
    //
    // private final V value;
    //
    // /**
    // * @param cache
    // * @param sequenceID
    // * @param isTimeouted
    // * @param key
    // * @param value
    // */
    // public ExpiredEvent(final Cache<K, V> cache, final long sequenceID,
    // final K key, final V value) {
    // super();
    // this.cache = cache;
    // this.sequenceID = sequenceID;
    // this.key = key;
    // this.value = value;
    // }
    //
    // public Notification notification(Object source) {
    // return new Notification(getName(), source, sequenceID, toString());
    // }
    //
    // public long getSequenceID() {
    // return sequenceID;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheItemEvent#getKey()
    // */
    // public K getKey() {
    // return key;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheItemEvent#getValue()
    // */
    // public V getValue() {
    // return value;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheEvent#getCache()
    // */
    // public Cache<K, V> getCache() {
    // return cache;
    // }
    //
    // /**
    // * @see org.coconut.cache.CacheEvent#getName()
    // */
    // public String getName() {
    // return CacheItemEvent.Expired.NAME;
    // }
    //
    // /**
    // * @see java.util.Map$Entry#setValue(V)
    // */
    // public V setValue(V value) {
    // throw new UnsupportedOperationException("setValue not supported");
    // }
    // }

    final static class AccessedEvent<K, V> implements ItemAccessed<K, V>, Serializable,
            NotificationTransformer {

        private static final long serialVersionUID = 3545235834329511987L;

        private final transient Cache<K, V> cache;

        private final long sequenceID;

        private final boolean wasHit;

        private final CacheEntry<K, V> ce;

        private final K key;

        private final V value;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public AccessedEvent(final Cache<K, V> cache, final long sequenceID,
                final CacheEntry<K, V> ce, final K key, final V value, boolean wasHit) {
            this.ce = ce;
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.key = key;
            this.value = value;
            this.wasHit = wasHit;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        public boolean isHit() {
            return wasHit;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return ItemAccessed.NAME;
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

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    final static class ChangedEvent<K, V> implements ItemUpdated<K, V>, Serializable,
            NotificationTransformer {

        private static final long serialVersionUID = 3545235834329511987L;

        private final transient Cache<K, V> cache;

        private final long sequenceID;

        private final K key;

        private final V value;

        private final V previous;

        private final CacheEntry<K, V> ce;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public ChangedEvent(final Cache<K, V> cache, final long sequenceID,
                final CacheEntry<K, V> ce, final K key, final V value, final V previous) {
            super();
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.key = key;
            this.value = value;
            this.previous = previous;
            this.ce = ce;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return ItemUpdated.NAME;
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

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        public boolean hasExpired() {
            return false;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    final static class AddedEvent<K, V> implements ItemAdded<K, V>, Serializable,
            NotificationTransformer {

        private static final long serialVersionUID = 3545235834329511987L;

        private final transient Cache<K, V> cache;

        private final long sequenceID;

        private final K key;

        private final V value;

        private final CacheEntry<K, V> ce;

        /**
         * @param cache
         * @param sequenceID
         * @param isTimeouted
         * @param key
         * @param value
         */
        public AddedEvent(final Cache<K, V> cache, final CacheEntry<K, V> ce,
                final long sequenceID, final K key, final V value) {
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.key = key;
            this.value = value;
            this.ce = ce;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see org.coconut.cache.CacheItemEvent#getValue()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return ItemAdded.NAME;
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

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        public boolean hasExpired() {
            return false;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public CacheEntry<K, V> getEntry() {
            return ce;
        }
    }

    final static class ResetCacheStatisticsEvent<K, V> implements
            CacheEvent.CacheStatisticsReset<K, V>, Serializable, NotificationTransformer {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3258410651134211896L;

        private final Cache<K, V> cache;

        private final long sequenceID;

        private final HitStat hitstat;

        /**
         * @param cache
         * @param sequenceID
         * @param hitstat
         */
        public ResetCacheStatisticsEvent(final Cache<K, V> cache, final long sequenceID,
                final HitStat hitstat) {
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.hitstat = hitstat;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        /**
         * @see org.coconut.cache.CacheInstanceEvent.CacheStatisticsReset#getPreviousHitStat()
         */
        public HitStat getPreviousHitStat() {
            return hitstat;
        }

        public long getSequenceID() {
            return sequenceID;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getName()
         */
        public String getName() {
            return CacheEvent.CacheStatisticsReset.NAME;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public Entry<K, V> getEntry() {
            return null;
        }
    }

    final static class ClearEvent<K, V> implements CacheEvent.CacheCleared<K, V>, Serializable,
            NotificationTransformer {
        private static final long serialVersionUID = 3258410651134211896L;

        private final Cache<K, V> cache;

        private final long sequenceID;

        private final int previousSize;

        public ClearEvent(final Cache<K, V> cache, final long sequenceID, int previousSize) {
            this.cache = cache;
            this.sequenceID = sequenceID;
            this.previousSize = previousSize;
        }

        public Notification notification(Object source) {
            return new Notification(getName(), source, sequenceID, toString());
        }

        public int getPreviousSize() {
            return previousSize;
        }

        public long getSequenceID() {
            return sequenceID;
        }

        public Cache<K, V> getCache() {
            return cache;
        }

        public String getName() {
            return CacheEvent.CacheCleared.NAME;
        }

        /**
         * @see org.coconut.cache.CacheEvent#getEntry()
         */
        public Entry<K, V> getEntry() {
            return null;
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
