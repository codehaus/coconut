/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheUtil {

    public static final Cache.HitStat STAT00 = new ImmutableHitStat(0, 0);

    public static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

    public static void checkMapForNulls(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException("map contains a null key");
            } else if (entry.getValue() == null) {
                throw new NullPointerException("map contains a null value for key = "
                        + entry.getKey());
            }
        }
    }

    /**
     * Creates new a new HitStat object with the same number of hits and misses
     * as the specified HitStat.
     * 
     * @param copyFrom
     *            the HitStat to copy from
     * @return
     */
    public static Cache.HitStat newImmutableHitStat(Cache.HitStat copyFrom) {
        return new ImmutableHitStat(copyFrom);
    }

    public static Cache.HitStat newImmutableHitStat(long hits, long misses) {
        return new ImmutableHitStat(hits, misses);
    }

    /**
     * The default (immutable) implementation of a <code>HitStat<code>.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     */
    static final class ImmutableHitStat implements Cache.HitStat, Serializable {

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
        ImmutableHitStat(Cache.HitStat hitstat) {
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
            if (obj == null || !(obj instanceof Cache.HitStat)) {
                return false;
            }
            Cache.HitStat hs = (Cache.HitStat) obj;
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
            return Ressources.lookup(Cache.HitStat.class, "toString", getHitRatio(),
                    hits, misses);
        }
    }
}
