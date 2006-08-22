/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer;

import java.util.List;

import org.coconut.cache.policy.ReplacementPolicy;


/**
 * This is the main interface for coconut cache analyzer.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheAnalyzer<K, V> {

    /**
     * Adds the key.
     */
    boolean addIfMissing(K key);

    /**
     * Add the given policy to this analyzer.
     * 
     * @param policy
     *            the given policy to add
     * @return a <code>CachePolicyMonitor</code> for monitoring the policy
     */
    CachePolicyMonitor addMonitor(ReplacementPolicy policy);

    /**
     * Add the given policy to this analyzer.
     * 
     * @param policy
     *            the given policy to add
     * @param maxSize
     *            the maximum number of entries that will be accepted by the
     *            policy
     * @return a <code>CachePolicyMonitor</code> for monitoring the policy
     */
    CachePolicyMonitor addMonitor(ReplacementPolicy policy, int maxSize);
    
    /**
     * Clear all entries in every monitor. 
     * Questions: do we reset hit-counting??
     */
    void clear();

    /**
     * Returns the monitors that are currently registered in the analyzer. The
     * list will be ordered in such a way that the monitor which has is first in
     * the list is the one with the highest hit-ratio (the best) and the last
     * monitor in the list is the one with lowest hit-ratio. No ordered is
     * defined between monitors with an equal hit-ratio.
     * 
     * @return
     */
    List<CachePolicyMonitor> getMonitors();

    void refresh(K key);

    /**
     * Removes the given monitor for the analyzer.
     * @param monitor the monitor to remove
     * @return <tt>true</tt> if the monitor was succesfully removed
     */
    boolean removeMonitor(CachePolicyMonitor monitor);

    void remove(K key);
}
