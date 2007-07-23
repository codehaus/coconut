/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheStatisticsMXBean {

    /**
     * Returns the number of retrievels from the cache where the element was already
     * contained in the cache at the time of retrievel.
     * <p>
     * This number is equivalent to that returned by
     * {@link org.coconut.cache.Cache#getHitStat()}.
     * 
     * @return the number of hits
     * @see #getNumberOfMisses
     * @see #resetHitStat
     */
    long getNumberOfHits();

    /**
     * Returns the number of retrievels from the cache where the element was <tt>not</tt>
     * already contained in the cache at the time of retrievel.
     * <p>
     * This number is equivalent to that returned by
     * {@link org.coconut.cache.Cache#getHitStat()}.
     * 
     * @return the number of cache misses.
     */
    long getNumberOfMisses();

    /**
     * Return the ratio between hits and misses. This method will return
     * <tt> {@value java.lang.Double#NaN}</tt> if both the number of misses and hits are
     * equal to zero.
     * 
     * @return the ratio between hits and misses.
     */
    double getHitRatio();

    /**
     * Resets the hit ratio. This sets the number of cache hits and cache misses to zero
     * for the cache.
     * <p>
     * This method is equivalent to calling
     * {@link org.coconut.cache.Cache#resetStatistics()}.
     */
    void resetStatistics();
}