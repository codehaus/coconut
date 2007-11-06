/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.service.event.CacheEvent.CacheCleared;
import org.coconut.core.Mapper;
import org.coconut.core.Transformers;
import org.coconut.predicate.CollectionPredicates;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;

/**
 * Factory and utility methods for for creating different types of filters for cache
 * events and entries.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: CacheFilters.java 265 2007-02-06 23:06:04Z kasper $
 */
public final class CacheEventFilters {

    /** A filter that only accepts instances of CacheCleared events. */
    public static final Predicate<?> CACHE_CLEARED_FILTER = Predicates
            .isType(CacheCleared.class);

    /**
     * A filter that only accepts all instance events (events that are not instances of
     * {@link CacheEntryEvent}).
     */
    @SuppressWarnings("unchecked")
    public static final Predicate<?> CACHE_INSTANCE_FILTER = Predicates.not(Predicates
            .isType(CacheEntryEvent.class));

    /**
     * A {@link org.coconut.predicate.Predicate} that only accepts instances of ItemUpdated
     * events.
     */
    public final static Predicate<?> CACHEENTRY_ADDED_FILTER = Predicates
            .isType(ItemAdded.class);

    /**
     * A {@link org.coconut.predicate.Predicate} that only accepts instances of ItemUpdated
     * events.
     */
    public final static Predicate<?> CACHEENTRY_REMOVED_FILTER = Predicates
            .isType(ItemRemoved.class);

    /**
     * A {@link org.coconut.predicate.Predicate} that only accepts instances of ItemUpdated
     * events.
     */
    public final static Predicate<?> CACHEENTRY_UPDATED_FILTER = Predicates
            .isType(ItemUpdated.class);

    /**
     * A {@link org.coconut.predicate.Predicate} that will accept all instances of
     * CacheItemEvent.
     */
    public final static Predicate<?> CACHEENTRYEVENT_FILTER = Predicates
            .isType(CacheEntryEvent.class);

    /** A filter that only accepts instances of CacheEvent events. */

    public static final Predicate<?> CACHEEVENT_FILTER = Predicates.isType(CacheEvent.class);

    /** A transformer that extracts the cache from the specified {@link CacheEvent}. */
    private final static Mapper<CacheEvent, Cache<?, ?>> EVENT_TO_CACHE_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getCache");

    /**
     * A transformer that extracts the name of the cache from the specified
     * {@link CacheEvent}.
     */
    private final static Mapper<CacheEvent, String> EVENT_TO_NAME_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getName");

    /** Cannot instantiate. */
    private CacheEventFilters() {}

    /**
     * Returns a {@link Predicate} that only accepts {@link CacheEvent}s that originate from
     * the specified cache.
     * 
     * @param cache
     *            the cache that the cache events must originate from to be accepted
     * @return a Filter that only accepts cache events that originate from the specified
     *         cache
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> Predicate<CacheEvent<K, V>> cacheSameFilter(Cache<K, V> cache) {
        return cacheFilter(Predicates.same(cache));
    }

    /**
     * Returns a {@link Predicate} that accepts {@link CacheEvent}s depending on some
     * property regarding the originating cache. For example, the following Filter only
     * accepts Cache events where the size of the originating cache size is greater then
     * 10.
     * 
     * <pre>
     * Filter&lt;CacheEvent&lt;Integer, String&gt;&gt; filter = cacheFilter(new Filter&lt;Cache&lt;Integer, String&gt;&gt;()
     * {
     *     public boolean accept(Cache&lt;Integer, String&gt; cache) {
     *         return cache.size() &gt; 10;
     *     }
     * });
     * </pre>
     * 
     * @param filter
     *            the Filter to check the cache against
     * @return a filter that accepts cache events depending on some property regarding the
     *         originating cache
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<CacheEvent<K, V>> cacheFilter(Predicate<Cache<K, V>> filter) {
        return CollectionPredicates.transformFilter(
                (Mapper) EVENT_TO_CACHE_TRANSFORMER, filter);
    }

    /**
     * Returns a {@link Predicate} that only accepts {@link CacheEvent}s where
     * {@link CacheEvent#getName()} matches the specified filter.
     * 
     * @param filter
     *            the filter the name should be checked against
     * @return a Filter that only accepts cache events that originate from the specified
     *         cache
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<CacheEvent<K, V>> cacheName(Predicate<String> filter) {
        return CollectionPredicates.transformFilter((Mapper) EVENT_TO_NAME_TRANSFORMER,
                filter);
    }

}
