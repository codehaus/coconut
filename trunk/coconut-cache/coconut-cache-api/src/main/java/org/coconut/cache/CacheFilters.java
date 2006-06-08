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
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class CacheFilters {

    private final static Transformer<CacheEvent, Cache> EVENT_TO_CACHE_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getCache");

    private final static Transformer<CacheEvent, String> EVENT_TO_NAME_TRANSFORMER = Transformers
            .transform(CacheEvent.class, "getName");

//    private final static Transformer<CacheItemEvent, CacheEntry> EVENT_TO_ENTRY_TRANSFORMER = Transformers
//            .transform(CacheItemEvent.class, "getEntry");

    /**
     * Returns a Filter that filters {@link org.coconut.cache.CacheEvent}s
     * originating from a particular cache.
     * 
     */
    public static <K, V> Filter<CacheEvent<K, V>> cacheEqualsFilter(
            Cache<K, V> cache) {
        return cacheFilter(ComparisonFilters.equal(cache));
    }

    /**
     * Returns a Filter that filters {@link org.coconut.cache.CacheEvent}s depending
     * of some property/attribute regarding the originating cache. For example,
     * the following Filter filters only accepts Cache events where the size of
     * originating cache size is greater then 10.
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
    public static <K, V> Filter<CacheEvent<K, V>> cacheFilter(
            Filter<Cache<K, V>> filter) {
        return new Filters.TransformerFilter(EVENT_TO_CACHE_TRANSFORMER, filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> cacheName(
            Filter<String> filter) {
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
     * @param key the key that is accepted
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
    
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<CacheEvent<K, V>> anyValueEquals(
            final V... values) {
        return (Filter) valueFilter(ComparisonFilters.anyEquals(values));
    }

    // /**
    // * A CacheFilter filters {link coconut.cache.CacheEvent}s originating from
    // a
    // * particular cache.
    // *
    // * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
    // */
    // public final static class CacheFilter<K, V> implements
    // Filter<CacheEvent<K, V>> {
    //
    // private final Filter<Cache<K, V>> filter;
    //
    // /**
    // * Creates a new <code>CacheKeyedFilter</code>
    // *
    // * @param filter
    // * the filter we wish to compare against
    // */
    // public CacheFilter(final Filter<Cache<K, V>> filter) {
    // if (filter == null) {
    // throw new NullPointerException("filter is null");
    // }
    // this.filter = filter;
    // }
    //
    // /**
    // * Returns the key we are comparing against
    // *
    // * @return the key we are comparing against
    // */
    // public Filter<Cache<K, V>> getFilter() {
    // return filter;
    // }
    //
    // /**
    // * @see coconut.eventbus.Handler#accept(org.coconut.cache.CacheEvent)
    // */
    // public boolean accept(final CacheEvent<K, V> event) {
    // return filter.accept(event.getCache());
    // }
    // }
    //
    // /**
    // * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
    // */
    // public final static class EventNameFilter<K, V> implements
    // Filter<CacheEvent<K, V>> {
    //
    // private final Filter<String> filter;
    //
    // /**
    // * Creates a new <code>CacheKeyedFilter</code>
    // *
    // * @param filter
    // * the filter we wish to compare against
    // */
    // public EventNameFilter(final Filter<String> filter) {
    // if (filter == null) {
    // throw new NullPointerException("filter is null");
    // }
    // this.filter = filter;
    // }
    //
    // /**
    // * Returns the key we are comparing against
    // *
    // * @return the key we are comparing against
    // */
    // public Filter<String> getFilter() {
    // return filter;
    // }
    //
    // /**
    // * @see coconut.eventbus.Handler#accept(org.coconut.cache.CacheEvent)
    // */
    // public boolean accept(final CacheEvent<K, V> event) {
    // return filter.accept(event.getName());
    // }
    // }
}
