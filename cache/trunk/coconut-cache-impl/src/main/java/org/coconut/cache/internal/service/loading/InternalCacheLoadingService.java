/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public interface InternalCacheLoadingService<K, V> extends CacheLoadingService<K, V> {

    Filter<CacheEntry<K,V>> getRefreshFilter();
    
    /**
     * 
     * @param entry
     * @return
     */
    AbstractCacheEntry<K,V> loadBlocking(K key);
}
