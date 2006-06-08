package org.coconut.cache.analyzer.defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.analyzer.CachePolicyMonitor;
import org.coconut.cache.analyzer.management.CachePolicyMonitorMBean;
import org.coconut.cache.policy.ReplacementPolicy;

public class SimpleAnalyzer  {

    Set elements;

    List<Monitor> monitors = new ArrayList<Monitor>();

    public boolean addIfMissing(Object key) {
        if (!elements.contains(key)) {
            elements.add(key);
            return true;
        }
        return false;
    }

    public CachePolicyMonitor addMonitor(ReplacementPolicy policy) {
        return addMonitor(policy, Integer.MAX_VALUE);
    }

    public CachePolicyMonitor addMonitor(ReplacementPolicy policy, int maxSize) {
        Monitor monitor = new Monitor(policy);
        monitor.maxSize = maxSize;
        monitors.add(monitor);
        return monitor;
    }

    public void clear() {
    }

    public List getMonitors() {
        return monitors;
    }

    public void refresh(Object key) {
        for (Monitor m : monitors) {
            int i = m.map.get(key);
            m.policy.touch(i);
            while (m.size > m.maxSize) {
                m.policy.evictNext();
            }
        }
    }

    public boolean removeMonitor(CachePolicyMonitor monitor) {
        // TODO Auto-generated method stub
        return false;
    }

    public void remove(Object key) {

    }

    public class Monitor implements CachePolicyMonitor {
        private final ReplacementPolicy policy;

        int size;

        int maxSize;

        private final Map<?, Integer> map = new HashMap();

        public ReplacementPolicy getPolicy() {
            return policy;
        }

        public HitStat getHitStatistics() {
            return null;
        }

        public HitStat getRollingHitStatistics() {
            return null;
        }

        public void resetStatistics() {

        }

        public CachePolicyMonitorMBean getMBean() {
            return null;
        }

        public boolean containsElement(Object key) {
            return false;
        }

        public Monitor(ReplacementPolicy policy) {
            this.policy = policy;
        }

    }
}
