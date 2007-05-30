/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

/**
 * The class holds the hit statistics for a cache. Unless otherwise specified
 * implementations of this interface is immutable.
 */
public interface CacheHitStat {

    /**
     * Returns the ratio between cache hits and misses or {@link java.lang.Double#NaN} if
     * no hits or misses has been recorded.
     * 
     * @return the ratio between cache hits and misses or NaN if no hits or misses has
     *         been recorded
     */
    float getHitRatio();

    /**
     * Returns the number of succesfull hits for a cache. A request to a cache is a hit if
     * the value is already contained within the cache and no external cache backends must
     * be used to fetch the value.
     * 
     * @return the number of hits
     */
    long getNumberOfHits();

    /**
     * Returns the number of cache misses. A request is a miss if the value is not already
     * contained within the cache when it is requested and a cache backend must fetch the
     * value.
     * 
     * @return the number of cache misses.
     */
    long getNumberOfMisses();
}