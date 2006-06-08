package org.coconut.cache.policy.oldanalyzer;

import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * A policy monitor exists for each expiration policy that is monitored in a
 * policy analyzer.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface PolicyMonitor<K> {

    /**
     * Returns the policy that is being monitored.
     */
    ReplacementPolicy getPolicy();

    /**
     * Returns the maximum size of the elements that this policy is monitoring.
     * If all elements have size=1 this is the total number of elements.
     */
    int getMaxSize();

    /**
     * Removes all elements from this monitor and clears the statistics.
     */
    void clear();

    HitStat getHitStat();

    /**
     * Resets the hit statistics.
     */
    void resetStatistics();

    /**
     * A name that can be used to distinguish between different monitors.
     */
    String getName();

    long getSize();

    /**
     * Returns the current number of elements in the monitor.
     */
    int getNumberOfElements();
}