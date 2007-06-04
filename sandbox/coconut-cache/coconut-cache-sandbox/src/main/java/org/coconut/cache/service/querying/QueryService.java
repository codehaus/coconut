/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.querying;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface QueryService<K,V> {

    /**
     * This method is used to make queries into the cache locating cache entries
     * that match a particular criteria. The returned {@link CacheQuery} can be
     * used for returning all the matching cache entries at once or just a small
     * subset (paging functionality) at a time.
     * <p>
     * NOTICE: querying a cache can be a very time consuming affair especially
     * if no usefull indexes are available at query time.
     * <p>
     * If a backend stored is configured <tt>and</tt> it supports querying it
     * will be used for querying otherwise only the local cache will be queried.
     * 
     * @param filter
     *            the filter used to identify which entries should be retrieved
     * @return a cache query that can be used to retrieve the matching entries
     */
    CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter);

}
