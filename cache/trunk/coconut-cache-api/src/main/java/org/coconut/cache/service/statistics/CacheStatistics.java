/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import java.io.Serializable;

import net.jcip.annotations.Immutable;

import org.coconut.cache.spi.Resources;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheStatistics {

    public static final CacheHitStat STAT00 = new ImmutableHitStat(0, 0);

    /**
     * Creates new a new HitStat object with the same number of hits and misses as the
     * specified HitStat.
     * 
     * @param copyFrom
     *            the HitStat to copy from
     * @return
     */
    public static CacheHitStat newImmutableHitStat(CacheHitStat copyFrom) {
        return new ImmutableHitStat(copyFrom);
    }

    public static CacheHitStat newImmutableHitStat(long hits, long misses) {
        return new ImmutableHitStat(hits, misses);
    }

    /**
     * The default (immutable) implementation of a <code>HitStat<code>.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     */
    @Immutable
    static final class ImmutableHitStat implements CacheHitStat, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 2775783950714414347L;

        /** The number of cache hits */
        private final long hits;

        /** The number of cache misses */
        private final long misses;

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(CacheHitStat hitstat) {
            this(hitstat.getNumberOfHits(), hitstat.getNumberOfMisses());
        }

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(long hits, long misses) {
            if (hits < 0) {
                throw new IllegalArgumentException("hits must be 0 or greater");
            } else if (misses < 0) {
                throw new IllegalArgumentException("misses must be 0 or greater");
            }
            this.misses = misses;
            this.hits = hits;
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
        public float getHitRatio() {
            final long sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return ((float) hits) / sum;
        }

        /** {@inheritDoc} */
        public long getNumberOfHits() {
            return hits;
        }

        /**
         * {@inheritDoc}
         */
        public long getNumberOfMisses() {
            return misses;
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
            return Resources.lookup(CacheHitStat.class, "toString", getHitRatio(), hits,
                    misses);
        }
    }
}
