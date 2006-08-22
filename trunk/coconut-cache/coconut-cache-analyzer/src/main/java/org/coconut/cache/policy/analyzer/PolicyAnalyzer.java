/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class PolicyAnalyzer<T> {

    private int elementCapacity;

    private long totalSize;

    private List<PolicyMonitor> monitors = new ArrayList<PolicyMonitor>();

    public PolicyMonitor addPolicy(Class<? extends ReplacementPolicy<T>> policy) {
        try {
            ReplacementPolicy<T> p = policy.newInstance();
            PolicyMonitor om = new PolicyMonitor(p);
            monitors.add(om);
            return om;
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot instantiate replacement policy", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot instantiate replacement policy", e);
        }
    }

    public List<PolicyMonitor> getMonitors() {
        return new ArrayList<PolicyMonitor>(monitors);
    }
    public PolicyMonitor getBestPolicy() {
        double currentBest = 0;
        PolicyMonitor<T> p = null;
        for (PolicyMonitor<T> pm : monitors) {
            double ratio = pm.getRatio();
            if (ratio > currentBest) {
                p = pm;
                currentBest = ratio;
            }
        }
        return p;
    }
}
