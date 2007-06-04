/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static org.coconut.filter.CollectionFilters.keyFilter;
import static org.coconut.filter.CollectionFilters.valueFilter;

import org.coconut.cache.service.querying.CacheQuery;
import org.coconut.cache.service.querying.QueryService;
import org.coconut.filter.Filter;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheFilters2 {

    @SuppressWarnings("unchecked")
    public static <K, V> CacheQuery<K, V> queryByKey(Cache2<K, V> c, Filter<K> filter) {
        return c.getService(QueryService.class).query((Filter) keyFilter(filter));
    }

    //
    // /**
    // * Used for easily querying a cache about certain values.
    // *
    // * @param cache
    // * the cache to query
    // * @param filter
    // * the filter to apply to the value
    // * @return a cache query for the given cache and filter
    // */
    @SuppressWarnings("unchecked")
    public static <K, V> CacheQuery<K, V> queryByValue(Cache2<K, V> cache,
            Filter<V> filter) {
        return cache.getService(QueryService.class).query((Filter) valueFilter(filter));
    }
}
