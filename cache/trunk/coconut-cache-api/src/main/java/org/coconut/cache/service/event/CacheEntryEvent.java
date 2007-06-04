/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.cache.CacheEntry;

/**
 * A CacheEntryEvent is raised whenever a particular key-value pair in the cache has a
 * significant change in its state. For example, when an entry has been removed or the
 * value of an entry has been changed.
 * <p>
 * Currently 4 standard events are supported.
 * <ul>
 * <li>{@link ItemAccessed}</li>: is raised whenever an entry is accessed through ..
 * <li>{@link ItemAdded}</li>
 * <li>{@link ItemRemoved}</li>
 * <li>{@link ItemUpdated}</li>
 * </ul>
 * <p>
 * Since most usages of a cache has a very high read/write ratio. {@link ItemAccessed} are
 * not posted with the default cache configuration. However, they can be enabled by using:
 * 
 * <pre>
 * CacheConfiguration&lt;?, ?&gt; conf = CacheConfiguration.create();
 * CacheEventConfiguration cec = conf.getServiceConfiguration(CacheEventConfiguration.class);
 * cec.include(CacheEntryEvent.ItemAccessed.class);
 * </pre>
 * 
 * @see CacheEventConfiguration
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("hiding")
public interface CacheEntryEvent<K, V> extends CacheEvent<K, V>, CacheEntry<K, V> {

    /**
     * This event indicates that an entry in the cache was accessed. This happens through
     * either the {@link Cache#get(Object)} or {@link Cache#getAll(java.util.Collection)}
     * method. Calling {@link Cache#peek(Object)} or {@link Cache#peekEntry(Object)} on a
     * cache will not result in an ItemAccessed event being raised.
     * <p>
     * If the item is a miss ({@link isHit()} returns false) and the cache succesfully
     * loads a new cache entry. The {@link #getValue()} will return the new value and
     * {@link #getEntry()} will return the new cache entry.
     * <p>
     * NOTE: This event can be fairly expensive in terms of performance to subscribe for,
     * as it is raised on every access to cache.
     */
    interface ItemAccessed<K, V> extends CacheEntryEvent<K, V> {
        /** The unique name of this event. */
        String NAME = "cacheitem.Accessed";

        /**
         * Whether or not the requested entry was allready in the cache. If the entry was
         * not in the cache {@link #getValue()} will return <code>null</code>. TODO
         * what about if it is loaded??? isn't there a value then.
         */
        boolean isHit();
    }

    /**
     * This event indicates that an entry has been added to the cache. This normally
     * happens either explicitly by using {@link Cache#put(Object, Object)} or implicitly
     * by calling {@link Cache#get(Object)} or {@link Cache#load(Object)} and letting the
     * cache loader fetch the value.
     */
    interface ItemAdded<K, V> extends CacheEntryEvent<K, V> {

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
     * This event indicates that there no longer exists a mapping for a given key. If the
     * value of the entry is merely changed, for example, by loading a new value an
     * {@link ItemUpdated} event will be raised instead. This can happen, for example, if
     * an entry has expired an the cache is configured to automatically fetch fresh
     * elements whenever an element expires.
     */
    interface ItemRemoved<K, V> extends CacheEntryEvent<K, V> {

        /** The unique name of this event. */
        String NAME = "cacheitem.Removed";

        /**
         * Returns true if the item was removed because the timeout value specified for
         * the item had been reached. Otherwise returns false.
         */
        boolean hasExpired();

        // was explicitly removed, moved 2ndary storage
        // boolean wasEvicted??? no room in cache and no 2nd storage
    }

    /**
     * This event indicates that the value of an existing entry entry was changed.
     * Normally this happens when using the put() method or when an entry expires and the
     * cache automatically loads an updated value from the specified cache loader.
     */
    interface ItemUpdated<K, V> extends CacheEntryEvent<K, V> {

        /** The unique name of the event. */
        String NAME = "cacheitem.Updated";

        /**
         * Returns the value that was previously associated with the specified key, or
         * <tt>null</tt> if there was no mapping for the key.
         * <p>
         * Some cache implementations might for optimization reason return
         * <code>null</code>, but this must be clearly specified. The reason for this
         * is that if there exist a entry for the particular in a background store it will
         * need to fetched before this event can be posted.
         */
        V getPreviousValue();

        /**
         * Returns true if the item was updated because the timeout value specified for
         * the item had been reached. Otherwise returns false.
         */
        boolean hasExpired();
    }
}
