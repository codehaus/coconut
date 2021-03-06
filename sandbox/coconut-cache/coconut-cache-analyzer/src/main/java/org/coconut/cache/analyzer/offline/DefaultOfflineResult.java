/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer.offline;

import org.coconut.cache.Caches;
import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.spi.CacheUtil;

public class DefaultOfflineResult implements OfflineResult {

    private HitStat total;

    private int stepWidth;

    private long[] hits;

    private long[] misses;

    /**
     * Creates a new DefaultResult
     * <p>
     * <tt>Important</tt> this constructor does not copy the hits and misses
     * arrays, so any change to them will be reflected in results from this
     * class.
     * 
     * @param hits
     * @param misses
     * @param stepWidth
     * @param total
     */
    public DefaultOfflineResult(long[] hits, long[] misses, int stepWidth,
            HitStat total) {
        if (hits == null) {
            throw new NullPointerException("hits is null");
        } else if (misses == null) {
            throw new NullPointerException("misses is null");
        } else if (total == null) {
            throw new NullPointerException("total is null");
        } else if (hits.length != misses.length) {
            throw new IllegalArgumentException(
                    "length of hits and misses does not match, hits.length = "
                            + hits.length + ", misses.length = "
                            + misses.length);
        } else if (stepWidth < 1) {
            throw new IllegalArgumentException(
                    "stepWidth must be bigger then 0, was " + stepWidth);
        }
        this.hits = hits;
        this.misses = misses;
        this.stepWidth = stepWidth;
        this.total = CacheUtil.newImmutableHitStat(total);
    }

    public HitStat getTotal() {
        return total;
    }

    public int getStepWidth() {
        return stepWidth;
    }

    public int getSteps() {
        return hits.length;
    }

    public HitStat getResult(int step) {
        if (step < 0 || step >= hits.length) {
            throw new IllegalArgumentException("step must be between 0 and "
                    + (hits.length - 1) + ", was " + step);
        }
        return CacheUtil.newImmutableHitStat(hits[step], misses[step]);
    }
}
