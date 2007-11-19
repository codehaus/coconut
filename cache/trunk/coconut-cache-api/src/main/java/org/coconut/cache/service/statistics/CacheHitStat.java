/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.spi.CacheSPI;

/**
 * An immutable class holding the hit statistics for a cache.
 * <p>
 * TODO: make sure this class is JMX compatible.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheHitStat {
    /** A CacheHitStat with 0 hits and 0 misses. */
    public static final CacheHitStat STAT00 = new CacheHitStat(0, 0);

    /** The number of cache hits. */
    private final long hits;

    /** The number of cache misses. */
    private final long misses;

    /**
     * Constructs a new HitStat.
     * 
     * @param hits
     *            the number of cache hits
     * @param misses
     *            the number of cache misses
     */
    public CacheHitStat(long hits, long misses) {
        if (hits < 0) {
            throw new IllegalArgumentException("hits must be 0 or greater");
        } else if (misses < 0) {
            throw new IllegalArgumentException("misses must be 0 or greater");
        }
        this.misses = misses;
        this.hits = hits;
    }

    /**
     * Returns the ratio between cache hits and misses or {@link java.lang.Double#NaN} if
     * no hits or misses has been recorded.
     * 
     * @return the ratio between cache hits and misses or NaN if no hits or misses has
     *         been recorded
     */
    public float getHitRatio() {
        final long sum = hits + misses;
        if (sum == 0) {
            return Float.NaN;
        }
        return ((float) hits) / sum;
    }

    /**
     * Returns the number of succesfull hits for a cache. A request to a cache is a hit if
     * the value is already contained within the cache and no external cache backends must
     * be used to fetch the value.
     * 
     * @return the number of hits
     */
    public long getNumberOfHits() {
        return hits;
    }

    /**
     * Returns the number of cache misses. A request is a miss if the value is not already
     * contained within the cache when it is requested and a cache backend must fetch the
     * value.
     * 
     * @return the number of cache misses.
     */
    public long getNumberOfMisses() {
        return misses;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CacheHitStat)) {
            return false;
        }
        CacheHitStat hs = (CacheHitStat) obj;
        return hs.getNumberOfHits() == hits && hs.getNumberOfMisses() == misses;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        long value = hits ^ misses;
        return (int) (value ^ (value >>> 32));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        // We probably can't use a resource bundle, if it needs to be JMX compatible
        // or we could check if the string was available, otherwise resort to a default
        // test.
        return CacheSPI.lookup(getClass(), "toString", getHitRatio(), hits, misses);
    }
}
