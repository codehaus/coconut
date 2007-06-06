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
     * The returned object is a immutable snapshot that only reflects the current state of
     * the cache at the calling time.
     * 
     * @return the current hit statistics
     * @throws UnsupportedOperationException
     *             if gathering of statistics is not supported by this cache.
     */
    CacheHitStat getHitStat();

// /**
// * Returns the number of retrievels from the cache where the element was
// * already contained in the cache at the time of retrievel.
// * <p>
// * This number is equivalent to that returned by
// * {@link org.coconut.cache.Cache#getHitStat()}.
// *
// * @return the number of hits
// * @see #getNumberOfMisses
// * @see #resetHitStat
// */
// long getNumberOfHits();
//
// /**
// * Returns the number of retrievels from the cache where the element was
// * <tt>not</tt> already contained in the cache at the time of retrievel.
// * <p>
// * This number is equivalent to that returned by
// * {@link org.coconut.cache.Cache#getHitStat()}.
// *
// * @return the number of cache misses.
// */
// long getNumberOfMisses();
//
// /**
// * Return the ratio between hits and misses. This method will return
// * <tt> {@value java.lang.Double#NaN}</tt> if both the number of misses and
// * hits are equal to zero.
// *
// * @return the ratio between hits and misses.
// */
// double getHitRatio();
//
// /**
// * Resets the hit ratio. This sets the number of cache hits and cache misses
// * to zero for the cache.
// * <p>
// * This method is equivalent to calling
// * {@link org.coconut.cache.Cache#resetStatistics()}.
// */
// void resetStatistics();
}
