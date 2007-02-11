/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.core.Transformer;
import org.coconut.core.util.Transformers;
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

    //TODO Think we should move these to the CollectionFilters
    //and add a isType(Map.Entry) to each of them 

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

}
