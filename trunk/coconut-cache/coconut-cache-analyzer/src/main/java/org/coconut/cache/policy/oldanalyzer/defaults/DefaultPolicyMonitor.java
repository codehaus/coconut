package org.coconut.cache.policy.oldanalyzer.defaults;

import java.util.HashMap;

import org.coconut.cache.Caches;
import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.oldanalyzer.PolicyMonitor;

public class DefaultPolicyMonitor<K> implements PolicyMonitor<K> {

    private final ReplacementPolicy<K> cp;

    private final int maxSize;

    private final String name;

    private final HashMap<K, Integer> mappings = new HashMap<K, Integer>();

    private int hits;

    private int misses;

    public DefaultPolicyMonitor(ReplacementPolicy<K> cp, int maxSize, String name) {
        this.maxSize = maxSize;
        this.cp = cp;
        this.name = name;
    }

    void touch(K key) {
        Integer i = mappings.get(key);

        if (i == null) {
            misses++;
            int index = cp.add(key);
            if (index >= 0) {
                cp.touch(index);
                mappings.put(key, index);
            }
        } else {
            hits++;
            cp.touch(i);
        }
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getPolicy()
     */
    public ReplacementPolicy getPolicy() {
        return cp;
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getMaxSize()
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#clear()
     */
    public void clear() {
        resetStatistics();
        mappings.clear();
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getHitStat()
     */
    public HitStat getHitStat() {
        return Caches.newImmutableHitStat(hits, misses);
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#resetStatistics()
     */
    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getNumberOfElements()
     */
    public int getNumberOfElements() {
        return mappings.size();
    }

    /**
     * @see org.coconut.cache.policy.analyzer.PolicyMonitor#getSize()
     */
    public long getSize() {
        return mappings.size();
    }
}
