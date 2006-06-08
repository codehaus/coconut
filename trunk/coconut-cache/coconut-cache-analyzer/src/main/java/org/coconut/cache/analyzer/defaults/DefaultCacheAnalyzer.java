package org.coconut.cache.analyzer.defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.analyzer.CacheAnalyzer;
import org.coconut.cache.analyzer.CachePolicyMonitor;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author kni TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultCacheAnalyzer<K, V> implements CacheAnalyzer<K, V> {
    Map<K, MonitorElement> elements = new HashMap<K, MonitorElement>();

    private ArrayList<DefaultCachePolicyMonitor<MonitorElement>> monitors = new ArrayList<DefaultCachePolicyMonitor<MonitorElement>>();

    public DefaultCacheAnalyzer() {
    }

    public boolean addIfMissing(K key) {
        MonitorElement e = elements.get(key);
        boolean isNew = (e == null);
        if (isNew) {
            e = new MonitorElement(monitors.size());
            elements.put(key, e);
        }

        for (int i = 0; i < monitors.size(); i++) {
            DefaultCachePolicyMonitor<MonitorElement> a = monitors.get(i);
            if (e.entries[i] < 1) // not already added
            {
                int entry = a.add(e);
                if (entry > -1) {
                    e.entries[i] = entry;
                    if (!isNew)
                        a.miss();
                    MonitorElement monitor = a.checkSize();
                    if (monitor != null)
                        monitor.entries[i] = 0;
                }
            } else {
                a.refresh(e.entries[i]);
                a.hit();
            }
        }
        return isNew;
    }

    public CachePolicyMonitor addMonitor(ReplacementPolicy policy) {
        return addMonitor(policy, Integer.MAX_VALUE);
    }

    /**
     * @see org.coconut.cache.analyzer.oldpolicy.OldCacheAnalyzer#addMonitor(org.coconut.cache.analyzer.ReplacementPolicy)
     */
    public CachePolicyMonitor addMonitor(ReplacementPolicy policy, int mazSize) {
        DefaultCachePolicyMonitor<MonitorElement> monitor = new DefaultCachePolicyMonitor<MonitorElement>(
                this, policy);
        monitor.setMaxNumberOfElements(mazSize);
        monitors.add(monitor);
        monitor.index = monitors.size() - 1;
        // TODO add one item to ElementEntries
        if (!elements.isEmpty()) {
            for (MonitorElement element : elements.values()) {
                element.extend();
            }
        }
        return monitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CacheAnalyzer#clear()
     */
    public void clear() {
        for (MonitorElement e : elements.values()) {
            e.clear();
        }
        for (DefaultCachePolicyMonitor<MonitorElement> monitor : monitors) {
            while (monitor.getPolicy().evictNext() != null) {
            }
        }
        // ? clear hit ratio??
    }

    /**
     * @see org.coconut.cache.analyzer.oldpolicy.OldCacheAnalyzer#getMonitoredPolicies()
     */
    public List<CachePolicyMonitor> getMonitors() {
        return new ArrayList<CachePolicyMonitor>(monitors);
    }

    public void refresh(K key) {
        MonitorElement e = elements.get(key);
        if (e != null) {
            for (int i = 0; i < monitors.size(); i++) {
                DefaultCachePolicyMonitor<MonitorElement> a = monitors.get(i);
                if (e.entries[i] > 0) // not already added
                {
                    a.refresh(e.entries[i]);
                    a.hit();
                } else {
                    a.miss();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CacheAnalyzer#remove(K)
     */
    public void remove(K key) {
        MonitorElement e = elements.get(key);
        if (e != null) {
            for (int i = 0; i < e.entries.length; i++) {
                int index = e.entries[i];
                e.remove(index);
                e.entries[i] = 0;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CacheAnalyzer#removePolicy(coconut.cache.analyzer.CachePolicyAdapter)
     */
    public boolean removeMonitor(CachePolicyMonitor adapter) {
        int index = monitors.indexOf(adapter);
        if (index == -1)
            return false;
        else {
            for (MonitorElement e : elements.values()) {
                e.remove(index);
            }
            int lastIndex = monitors.size() - 1;
            if (index != lastIndex) {
                DefaultCachePolicyMonitor<MonitorElement> element = monitors
                        .get(lastIndex);
                element.index = index;
                monitors.set(index, element);
            }
            monitors.remove(monitors.size() - 1);
            return true;
        }
    }
}
