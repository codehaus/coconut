/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.statistics.CacheHitStat;

/**
 * This interface defines the top level type for events published by a {@link
 * Cache}. The events are generally divided into these two categories:
 * <ul>
 * <li> <strong>Item events</strong>, which concerns a particular key-value
 * pair in the cache. For example, an event indicating that a particular
 * key-value has been removed. Item events all inherit from
 * {@link CacheItemEvent}.
 * <li> <strong>Instance events</strong> which are general events concerning a
 * particular cache <tt>instance</tt>. For example, that the cache has been
 * cleared and all values removed. These all inherit from {@link CacheEvent}.
 * </ul>
 * <p>
 * Cache events are usually delivered locally to a single JVM instance only.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@SuppressWarnings("hiding")
public interface CacheEvent<K, V> /* extends AttributedEvent */{

    /**
     * Returns the cache from where this event originated.
     * 
     * @return the cache from where this event originated or <code>null</code>
     *         if the cache is not available (for example, if the event is
     *         handled in another JVM than it was raised in)
     */
    Cache<K, V> getCache();

    /**
     * Returns a unique name that can be used to identify the
     * <tt>type<tt> of the event. This is usual a display friendly name.
     * 
     * @return a unique name that can be used to identify the type of the event
     */
    String getName();

    /**
     * An event indicating that a particular {@link Cache} was cleared.
     */
    interface CacheCleared<K, V> extends CacheEvent<K, V> {

        /** The unique name of the event. */
        String NAME = "cache.cleared";

        /**
         * Returns the number of elements that was in the cache before it was
         * cleared.
         * 
         * @return the number of elements that was in the cache before it was
         *         cleared
         */
        int getPreviousSize();
        
        long getPreviousCapacity();
    }

    /**
     * An event indicating that the statistics of a particular cache has been
     * reset.
     */
    interface CacheStatisticsReset<K, V> extends CacheEvent<K, V> {
        /** The unique name of the event. */
        String NAME = "cache.statisticsReset";

        /**
         * Return the hit statistics of the cache before the statistics was
         * reset.
         * 
         * @return the hit statistics of the cache before the statistics was
         *         reset
         */
        CacheHitStat getPreviousHitStat();
    }

    /**
     * An event indicating that evict was called on a particular {@link Cache}.
     */
    interface CacheEvicted<K, V> extends CacheEvent<K, V> {
        /** The unique name of the event. */
        String NAME = "cache.evicted";

        /**
         * Returns the current number of elements contained in the cache after
         * evict has been called.
         */
        int getCurrentSize();

        /**
         * Return the previous number of elements contained in the cache before
         * the call to evict.
         */
        int getPreviousSize();
    }
}