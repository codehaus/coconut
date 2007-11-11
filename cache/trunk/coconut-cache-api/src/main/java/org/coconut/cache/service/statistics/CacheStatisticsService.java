/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;

/**
 * This is the main interface for controlling the statistics service of a cache at
 * runtime.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = c.getService(CacheEvictionService.class);
 * ces.trimToSize(10);
 * </pre>
 * 
 * Or by using {@link CacheServices}
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = CacheServices.eviction(c);
 * ces.setMaximumSize(10000);
 * </pre>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheStatisticsService {

    /**
     * Resets the hit ratio.
     * <p>
     * The number of hits returned by individual items {@link CacheEntry#getHits()} are
     * not affected by calls to this method.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics (read-only
     *             cache)
     */
    void resetStatistics();

    /**
     * Returns the current <tt>hit statistics</tt> for the cache (optional operation).
     * The returned object is an immutable snapshot that reflects the state of the cache
     * at the calling time.
     * 
     * @return the current hit statistics
     * @throws UnsupportedOperationException
     *             if gathering of statistics is not supported by this cache.
     */
    CacheHitStat getHitStat();
}
