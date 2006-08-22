/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer;

import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.analyzer.management.CachePolicyMonitorMBean;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CachePolicyMonitor<K> {
    /**
     * Returns the policy that this monitor is keeping statistics for.
     */
    ReplacementPolicy getPolicy();

    /**
     * Returns the hit statictics for this monitor.
     */
    HitStat getHitStatistics();

    /**
     * Returns a HitStat for this monitor that is constantly being updated.
     */
    HitStat getRollingHitStatistics();

    /**
     * Resets the hit statistics. 
     */
    void resetStatistics();

    CachePolicyMonitorMBean getMBean();

    boolean containsElement(K key);
}
