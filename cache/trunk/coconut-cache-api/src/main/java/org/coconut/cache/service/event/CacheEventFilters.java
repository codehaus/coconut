/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.service.event.CacheEvent.CacheCleared;
import org.coconut.cache.service.event.CacheEvent.CacheEvicted;
import org.coconut.cache.service.event.CacheEvent.CacheStatisticsReset;
import org.coconut.core.Transformer;
import org.coconut.core.Transformers;
import org.coconut.filter.CollectionFilters;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * Factory and utility methods for for creating different types of filters for
 * cache events and entries.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: CacheFilters.java 265 2007-02-06 23:06:04Z kasper $
 */
public class CacheEventFilters {

    /** A filter that only accepts instances of CacheEvent events. */

    public static final Filter<?> CACHEEVENT_FILTER = Filters.isType(CacheEvent.class);

    /**
     * A filter that only accepts all instance events (events that are not
     * instances of {@link CacheItemEvent}).
     */
    @SuppressWarnings("unchecked")
    public static final Filter<?> CACHE_INSTANCE_FILTER = Filters.not(Filters
            .isType(CacheEntryEvent.class));

    /** A filter that only accepts instances of CacheCleared events. */
    public static final Filter CACHE_CLEARED_FILTER = Filters.isType(CacheCleared.class);

    /** A filter that only accepts instances of CacheStatisticsReset events. */
    public static final Filter CACHE_RESET_STATISTICS_FILTER = Filters
            .isType(CacheStatisticsReset.class);

    /** A filter that only accepts instances of CacheEvicted events. */
    public static final Filter CACHE_EVICTED_FILTER = Filters.isType(CacheEvicted.class);

    /**
     * A {@link org.coconut.filter.Filter} that will accept all instances of
     * CacheItemEvent.
     */
    public final static Filter CACHEENTRYEVENT_FILTER = Filters
            .isType(CacheEntryEvent.class);

    /** A filter that only accepts instances of ItemAccessed events. */
    public final static Filter CACHEENTRY_ACCESSED_FILTER = Filters
            .isType(ItemAccessed.class);

    /**
     * A {@link org.coconut.filter.Filter} that only accepts instances of
     * ItemUpdated events.
     */
    public final static Filter CACHEENTRY_REMOVED_FILTER = Filters
            .isType(ItemRemoved.class);

    /**
     * A {@link org.coconut.filter.Filter} that only accepts instances of
     * ItemUpdated events.
     */
    public final static Filter CACHEENTRY_UPDATED_FILTER = Filters
            .isType(ItemUpdated.class);

    /**
     * A {@link org.coconut.filter.Filter} that only accepts instances of
     * ItemUpdated events.
     */
    public final static Filter CACHEENTRY_ADDED_FILTER = Filters.isType(ItemAdded.class);

    private final static Transformer<CacheEvent, Cache> EVENT_TO_CACHE_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getCache");

    private final static Transformer<CacheEvent, String> EVENT_TO_NAME_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getName");

    /**
     * Returns a Filter that filters {@link org.coconut.cache.CacheEvent}s
     * originating from a particular cache.
     */
    public static <K, V> Filter<CacheEvent<K, V>> cacheEqualsFilter(Cache<K, V> cache) {
        return cacheFilter(Filters.same(cache));
    }

    /**
     * Returns a Filter that filters {@link org.coconut.cache.CacheEvent}s
     * depending of some property/attribute regarding the originating cache. For
     * example, the following Filter filters only accepts Cache events where the
     * size of originating cache size is greater then 10.
     * 
     * <pre>
     * Filter&lt;CacheEvent&lt;Integer, String&gt;&gt; filter = cacheFilter(new Filter&lt;Cache&lt;Integer, String&gt;&gt;() {
     *     public boolean accept(Cache&lt;Integer, String&gt; element) {
     *         return element.size() &gt; 10;
     *     }
     * });
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> cacheFilter(Filter<Cache<K, V>> filter) {
        return CollectionFilters.transformFilter(
                (Transformer) EVENT_TO_CACHE_TRANSFORMER, filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> cacheName(Filter<String> filter) {
        return CollectionFilters.transformFilter((Transformer) EVENT_TO_NAME_TRANSFORMER,
                filter);
    }

}
