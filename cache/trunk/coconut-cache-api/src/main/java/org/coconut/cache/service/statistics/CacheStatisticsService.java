/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheStatisticsService {
    // keep time
    // detailed statistics
    // get Long counters und so weiter...

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
