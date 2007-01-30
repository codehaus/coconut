/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.coconut.core.util.Transformers.mapEntryToKey;
import static org.coconut.core.util.Transformers.mapEntryToValue;

import java.util.Collection;
import java.util.Map;

import org.coconut.core.Transformer;
import org.coconut.core.util.Transformers;
import org.coconut.filter.Filter;
import org.coconut.filter.CollectionFilters;
import org.coconut.filter.Filters;
import org.coconut.filter.CollectionFilters.TransformerFilter;

/**
 * Factory and utility methods for for creating different types of filters for
 * cache events and entries.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class CacheFilters {

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
        return new CollectionFilters.TransformerFilter(EVENT_TO_CACHE_TRANSFORMER, filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> cacheName(Filter<String> filter) {
        return new CollectionFilters.TransformerFilter(EVENT_TO_NAME_TRANSFORMER, filter);
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
        return (Filter) keyFilter(Filters.equal(key));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> valueEqualsFilter(V value) {
        return (Filter) valueFilter(Filters.equal(value));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyKeyEquals(final K... keys) {
        return (Filter) keyFilter(Filters.anyEquals(keys));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyKeyInCollection(
            final Collection<? extends K> keys) {
        return (Filter) keyFilter(Filters.anyEquals(keys.toArray()));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the specified values.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyValueEquals(final V... values) {
        return (Filter) valueFilter(Filters.anyEquals(values));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the values contained in the specified Collection.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyValueInCollection(
            final Collection<? extends V> values) {
        //TODO what about null values in the collection?
        return (Filter) valueFilter(Filters.anyEquals(values.toArray()));
    }
}