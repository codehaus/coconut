/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.util.Map;

import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.filter.Filters.IsTypeFilter;

/**
 * A CacheItemEvent events concerns a particular key-value pair in the cache.
 * These events are raised, for example, when an entry has been removed or the
 * value of an entry has been changed.
 * <p>
 * Currently 4 standard events are supported. However, it is possible to create
 * new events by implementing CacheItemEvent and wiring them to a cache
 * instance.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("hiding")
public interface CacheItemEvent<K, V> extends CacheEvent<K, V>, Map.Entry<K, V> {

    /**
     * A {@link org.coconut.filter.Filter} that will accept all instances of
     * CacheItemEvent.
     */
    Filter ITEM_FILTER = Filters.isType(CacheItemEvent.class);

    /**
     * Returns the cacheentry corresponding to the key-value mapping (optional).
     * A cache might, for performance reasons, not keep track
     * 
     * @throws UnsupportedOperationException
     *             if the entry does not provide cache entries
     */
    CacheEntry<K, V> getEntry();
    
    /**
     * This event indicates that an entry in the cache was accessed. This
     * happens through either the {@link Cache#get(Object)} or
     * {@link Cache#getAll(java.util.Collection)} method. Calling
     * {@link Cache#peek(Object)} on a cache will not result in an ItemAccessed
     * event being raised.
     * <p>
     * NOTE: This event can be fairly expensive in terms of performance to
     * subscribe to as it is raised on every access to cache.
     */
    interface ItemAccessed<K, V> extends CacheItemEvent<K, V> {
        /** A filter that only accepts instances of ItemAccessed events. */
        IsTypeFilter FILTER = Filters.isType(ItemAccessed.class);

        /** The unique name of this event. */
        String NAME = "cacheitem.Accessed";

        /**
         * Whether or not the requested entry was allready in the cache. If the
         * entry was not in the cache {@link #getValue()} will return
         * <code>null</code>. TODO what about if it is loaded??? isn't there
         * a value then.
         */
        boolean isHit();
    }

    /**
     * This event indicates that an entry has been added to the cache. This
     * normally happens either explicitly by using
     * {@link Cache#put(Object, Object)} or implicitly by calling
     * {@link Cache#get(Object)} or {@link Cache#load(Object)} and letting the
     * cache loader fetch the value.
     */
    interface ItemAdded<K, V> extends CacheItemEvent<K, V> {

        /**
         * A {@link org.coconut.filter.Filter} that only accepts instances of
         * ItemUpdated events.
         */
        IsTypeFilter FILTER = Filters.isType(ItemAdded.class);

        /** The unique name of this event. */
        String NAME = "cacheitem.Added";
    }

    // /**
    // * This event indicates that an entry has been moved to secondary storage.
    // * This normally happens when the threshold of the cache has been reach
    // and
    // * a cache policy has decided that the given entry is least likely to be
    // * accessed in the near future. TODO: when is this usefull????? We
    // probably
    // * also need a corresponding event to when the value is loaded into the
    // * cache again. ItemFetched
    // */
    // interface ItemEvicted<K, V> extends CacheItemEvent<K, V> {
    //
    // /**
    // * A {@link org.coconut.filter.Filter} that only accepts instances of
    // * ItemUpdated events.
    // */
    // IsTypeFilter FILTER = Filters.isType(ItemEvicted.class);
    //
    // /** The unique name of this event. */
    // String NAME = "cacheitem.Evicted";
    //
    // /**
    // * Returns the remaining time that the entry will be valid. This method
    // * will return Long.MAX_VALUE if no expiration time has been set for the
    // * entry.
    // */
    // long getTimeToLive(TimeUnit unit);
    // }

    /**
     * This event indicates that there no longer exists a mapping for a given
     * key. If the value of the entry is merely changed, for example, by loading
     * a new value an {@link ItemUpdated} event will be raised instead. This can
     * happen, for example, if an entry has expired an the cache is configured
     * to automatically fetch fresh elements whenever an element expires.
     */
    interface ItemRemoved<K, V> extends CacheItemEvent<K, V> {

        /**
         * A {@link org.coconut.filter.Filter} that only accepts instances of
         * ItemUpdated events.
         */
        IsTypeFilter FILTER = Filters.isType(ItemRemoved.class);

        /** The unique name of this event. */
        String NAME = "cacheitem.Removed";

        /**
         * Returns true if the item was removed because the timeout value
         * specified for the item had been reached. Otherwise returns false.
         */
        boolean hasExpired();

        // was explicitly removed, moved 2ndary storage
        // boolean wasEvicted??? no room in cache and no 2nd storage
    }

    /**
     * This event indicates that the value of an existing entry entry was
     * changed. Normally this happens when using the put() method or when an
     * entry expires and the cache automatically loads an updated value from the
     * specified cache loader.
     */
    interface ItemUpdated<K, V> extends CacheItemEvent<K, V> {

        /**
         * A {@link org.coconut.filter.Filter} that only accepts instances of
         * ItemUpdated events.
         */
        IsTypeFilter FILTER = Filters.isType(ItemUpdated.class);

        /** The unique name of the event. */
        String NAME = "cacheitem.Updated";

        /**
         * Returns the value that was previously associated with the specified
         * key, or <tt>null</tt> if there was no mapping for the key.
         * <p>
         * Some cache implementations might for optimization reason return
         * <code>null</code>, but this must be clearly specified. The reason
         * for this is that if there exist a entry for the particular in a
         * background store it will need to fetched before this event can be
         * posted.
         */
        V getPreviousValue();

        /**
         * Returns true if the item was updated because the timeout value
         * specified for the item had been reached. Otherwise returns false.
         */
        boolean hasExpired();
    }
}
