package org.coconut.cache.analyzer;

import junit.framework.TestCase;

import org.coconut.cache.analyzer.defaults.DefaultCacheAnalyzer;
import org.coconut.cache.policy.paging.LRUPolicy;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CachePolicyMonitorTest extends TestCase {

    public void testMultipleLRUPolicies() {
        CacheAnalyzer<Integer, Integer> ca = new DefaultCacheAnalyzer<Integer, Integer>();
        CachePolicyMonitor<?> mon2 = ca.addMonitor(new LRUPolicy(), 1);
        CachePolicyMonitor<?> mon3 = ca.addMonitor(new LRUPolicy(), 2);

        ca.addIfMissing(0);
        ca.addIfMissing(1);
        assertEquals(0, mon2.getHitStatistics().getNumberOfMisses());
        assertEquals(0, mon3.getHitStatistics().getNumberOfMisses());
                
        ca.addIfMissing(0);
        assertEquals(1, mon2.getHitStatistics().getNumberOfMisses());
        assertEquals(0, mon3.getHitStatistics().getNumberOfMisses());
    }
}
