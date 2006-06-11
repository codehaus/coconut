/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache;

import static org.coconut.core.Transformers.mapEntryToKey;
import static org.coconut.core.Transformers.mapEntryToValue;

import java.util.Collection;
import java.util.Map;

import org.coconut.core.Transformer;
import org.coconut.core.Transformers;
import org.coconut.filter.ComparisonFilters;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.filter.Filters.TransformerFilter;

/**
 * Factory and utility methods for for creating different types of filters for
 * cache events and cache queries.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class CacheFilters {

    private final static Transformer<CacheEvent, Cache> EVENT_TO_CACHE_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getCache");

    private final static Transformer<CacheEvent, String> EVENT_TO_NAME_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getName");

    // private final static Transformer<CacheItemEvent, CacheEntry>
    // EVENT_TO_ENTRY_TRANSFORMER = Transformers
    // .transform(CacheItemEvent.class, "getEntry");

    /**
     * Returns a Filter that filters {@link org.coconut.cache.CacheEvent}s
     * originating from a particular cache.
     */
    public static <K, V> Filter<CacheEvent<K, V>> cacheEqualsFilter(Cache<K, V> cache) {
        return cacheFilter(ComparisonFilters.equal(cache));
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
        return new Filters.TransformerFilter(EVENT_TO_CACHE_TRANSFORMER, filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> cacheName(Filter<String> filter) {
        return new Filters.TransformerFilter(EVENT_TO_NAME_TRANSFORMER, filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> keyFilter(Filter<K> filter) {
        return new TransformerFilter(mapEntryToKey(), filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> valueFilter(Filter<V> filter) {
        return new TransformerFilter(mapEntryToValue(), filter);
    }

    /**
     * Returns a Filter that only accepts event regarding a particular key.
     * 
     * @param key
     *            the key that is accepted
     * @return a filter that only accepts event regarding a particular key.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> keyEqualsFilter(K key) {
        return (Filter) keyFilter(ComparisonFilters.equal(key));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> valueEqualsFilter(V value) {
        return (Filter) valueFilter(ComparisonFilters.equal(value));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyKeyEquals(final K... keys) {
        return (Filter) keyFilter(ComparisonFilters.anyEquals(keys));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyKeyEqualsCol(final Collection<? extends K> keys) {
        return (Filter) keyFilter(ComparisonFilters.anyEquals(keys.toArray()));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * one of the specified values.
     * 
     * @param values the values that are accepted by the filter
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyValueEquals(final V... values) {
        return (Filter) valueFilter(ComparisonFilters.anyEquals(values));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> CacheQuery<K, V> queryByKey(Cache<K, V> c, Filter<K> filter) {
        return c.query((Filter) keyFilter(filter));
    }

    /**
     * Used for easily querying a cache about certain values.
     * 
     * @param cache
     *            the cache to query
     * @param filter
     *            the filter to apply to the value
     * @return a cache query for the given cache and filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> CacheQuery<K, V> queryByValue(Cache<K, V> cache, Filter<V> filter) {
        return cache.query((Filter) valueFilter(filter));
    }
}
