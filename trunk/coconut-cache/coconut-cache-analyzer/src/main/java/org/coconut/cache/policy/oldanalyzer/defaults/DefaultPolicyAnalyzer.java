/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.oldanalyzer.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.oldanalyzer.PolicyAnalyzer;
import org.coconut.cache.policy.oldanalyzer.PolicyMonitor;

public class DefaultPolicyAnalyzer<K> implements PolicyAnalyzer<K> {

    private ArrayList<DefaultPolicyMonitor<K>> monitors = new ArrayList<DefaultPolicyMonitor<K>>();

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyAnalyzer#add(org.coconut.cache.policy.ReplacementPolicy,
     *      int)
     */
    public PolicyMonitor<K> add(ReplacementPolicy<K> cp, int maxSize) {
        return add(cp, maxSize, getName(cp, maxSize));
    }

    private String getName(ReplacementPolicy<K> cp, int maxSize) {
        return cp + "[maxSize = " + maxSize + "]";
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyAnalyzer#getMonitoredPolicies()
     */
    public Collection getMonitoredPolicies() {
        return Collections.unmodifiableList(monitors);
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyAnalyzer#remove(org.coconut.cache.policy.analyzer.defaults.DefaultPolicyMonitor)
     */
    public boolean remove(PolicyMonitor monitor) {
        return monitors.remove(monitor);
    }

    /**
     * {@inheritDoc}
     */
    public void touch(K key) {
        for (DefaultPolicyMonitor<K> monitor : monitors) {
            monitor.touch(key);
        }
    }

    public PolicyMonitor<K> add(ReplacementPolicy<K> cp, int maxSize, String name) {
        if (cp == null) {
            throw new NullPointerException("cp is null");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException(
                    "maxSize must be a positive number (>0)");
        }

        DefaultPolicyMonitor<K> monitor = new DefaultPolicyMonitor<K>(cp,
                maxSize, name);
        monitors.add(monitor);
        return monitor;
    }

    public void remove(K key) {
        // TODO Auto-generated method stub
        
    }

}
