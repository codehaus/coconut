/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.oldanalyzer;

import java.util.Collection;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * This interface is used for monitoring and analyzing a number of policies for
 * finding the most performant policy.
 * <p>
 * Implementations of this interface is <tt>not</tt> expected to be
 * threadsafe.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface PolicyAnalyzer<K> {

    /**
     * Starts monitoring a cache policy with the specifed maximum size.
     * 
     * @param policy
     *            the policy to start monitoring.
     * @param maxSize
     *            the maximum number of elements the policy can contain
     * @return a policy monitor that keeps track of the performance of the
     *         specified cache policy
     */
    PolicyMonitor<K> add(ReplacementPolicy<K> policy, int maxSize);

    PolicyMonitor<K> add(ReplacementPolicy<K> policy, int maxSize, String name);

    /**
     * Returns all the cache policies that are being monitored.
     */
    Collection<? extends PolicyMonitor<K>> getMonitoredPolicies();

    /**
     * Removes the specified policy monitor.
     * 
     * @return <code>true</code> if this analyzer contained the specified
     *         monitor.
     */
    boolean remove(PolicyMonitor monitor);

    void touch(K key);

    // void touch(CacheEntry<K, ?> entry);

    void remove(K key);
}