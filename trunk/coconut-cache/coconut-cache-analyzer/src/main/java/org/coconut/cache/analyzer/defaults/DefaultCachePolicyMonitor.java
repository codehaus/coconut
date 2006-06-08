package org.coconut.cache.analyzer.defaults;

import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.analyzer.CachePolicyMonitor;
import org.coconut.cache.analyzer.management.CachePolicyMonitorMBean;
import org.coconut.cache.policy.ReplacementPolicy;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class DefaultCachePolicyMonitor<V> implements CachePolicyMonitor<V>, Comparable<DefaultCachePolicyMonitor> {
    private final DefaultCacheAnalyzer analyzer;
    private final ReplacementPolicy<MonitorElement> policy;

    private int size;
    private int maxsize;
    private long hits;
    private long misses;

    int index;

    /**
     * @param i
     * @param policy
     */
    public DefaultCachePolicyMonitor(DefaultCacheAnalyzer analyzer,
        ReplacementPolicy<MonitorElement> policy) {
        this.policy = policy;
        this.analyzer = analyzer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyAdapter#getPolicy()
     */
    public ReplacementPolicy getPolicy() {
        return policy;
    }
    
    int add(MonitorElement value) {
        int i = policy.add(value);
        if (i > 0)
            size++;
        return i;
    }
    
    void hit() {
        hits++;
    }
    
    void miss() {
        misses++;
    }
    
    MonitorElement checkSize() {
        if (size > maxsize)
            return policy.evictNext();
        else
            return null;
    }
    void refresh(int indexX) {
        policy.touch(indexX);
        hits++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CacheAnalyzerPolicy#getNumberOfElements()
     */
    public int getNumberOfElements() {
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#setMaxNumberOfElements(int)
     */
    public void setMaxNumberOfElements(int number) {
        maxsize = number;

    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#getMaxNumberOfElements()
     */
    public int getMaxNumberOfElements() {
        return maxsize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#getMBean()
     */
    public CachePolicyMonitorMBean getMBean() {
        return new CachePolicyMonitorMBean() {

            public long getStart() {
                // TODO Auto-generated method stub
                return 0;
            }

            public long getNumberOfHits() {
                // TODO Auto-generated method stub
                return 0;
            }

            public long getNumberOfMisses() {
                // TODO Auto-generated method stub
                return 0;
            }

            public float getHitRatio() {
                // TODO Auto-generated method stub
                return 0;
            }

            public void setMaxNumberOfElements(int maxSize) {
            // TODO Auto-generated method stub

            }

            public int getMaxNumberOfElements() {
                // TODO Auto-generated method stub
                return 0;
            }

            public int getNumberOfElements() {

                return size;
            }

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#getHitRatio()
     */
    public HitStat getHitStatistics() {
        final long h = hits;
        final long m = misses;
        return new HitStat() {
            public long getNumberOfHits() {
                return h;
            }
            public long getNumberOfMisses() {
                return m;
            }
            public float getHitRatio() {
                return ((float) m + h) / h;
            }

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#getRollingHitRatio()
     */
    public HitStat getRollingHitStatistics() {
        return new HitStat() {
            public long getNumberOfHits() {
                return hits;
            }
            public long getNumberOfMisses() {
                return misses;
            }
            public float getHitRatio() {
                final long h = getNumberOfHits();
                return ((float) getNumberOfMisses() + h) / h;
            }

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#resetHitRatio()
     */
    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.cache.analyzer.CachePolicyMonitor#hasElement(V)
     */
    public boolean containsElement(V key) {
        MonitorElement el = (MonitorElement) analyzer.elements.get(key);
        return el.entries[index] > 0;
    }

    public int compareTo(DefaultCachePolicyMonitor o) {
        return 0;
    }
}
