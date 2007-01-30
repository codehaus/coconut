/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.analyzer;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.oldanalyzer.PolicyAnalyzer;
import org.coconut.cache.policy.oldanalyzer.PolicyMonitor;
import org.coconut.cache.policy.oldanalyzer.defaults.DefaultPolicyAnalyzer;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

public class SimpleAnalyzerTest extends MockTestCase {

    PolicyAnalyzer<String> analyzer;

    ReplacementPolicy<String> cp1;

    ReplacementPolicy<String> cp2;

    Mock m1;

    Mock m2;

    @SuppressWarnings("unchecked")
    protected void setUp() {
        analyzer = new DefaultPolicyAnalyzer<String>();
        m1 = mock(ReplacementPolicy.class);
        cp1 = (ReplacementPolicy) m1.proxy();
        m2 = mock(ReplacementPolicy.class);
        cp2 = (ReplacementPolicy) m2.proxy();
    }

    public void testAdd() {
        PolicyMonitor<String> monitor = analyzer.add(cp1, 10);
        assertNotNull(monitor);
        assertSame(cp1, monitor.getPolicy());
        assertEquals(10, monitor.getMaxSize());

        assertEquals(1, analyzer.getMonitoredPolicies().size());
        assertTrue(analyzer.getMonitoredPolicies().contains(monitor));
    }

    public void testRemove() {
        PolicyMonitor monitor = analyzer.add(cp1, 10);

        assertTrue(analyzer.remove(monitor));
    }

    public void testTouch() {
        analyzer.add(cp1, 10);
        analyzer.add(cp2, 10);

        m1.expects(once()).method("add").with(eq("A")).will(returnValue(1));
        m1.expects(exactly(2)).method("touch").with(eq(1));
        m2.expects(once()).method("add").with(eq("A")).will(returnValue(1));
        m2.expects(exactly(2)).method("touch").with(eq(1));

        m1.expects(once()).method("add").with(eq("B")).will(returnValue(2));
        m1.expects(exactly(2)).method("touch").with(eq(2));
        m2.expects(once()).method("add").with(eq("B")).will(returnValue(2));
        m2.expects(exactly(2)).method("touch").with(eq(2));

        analyzer.touch("A");
        analyzer.touch("A");

        analyzer.touch("B");
        analyzer.touch("B");
    }

    public void testNumberOfElementsAndSizeForPaging() {
        PolicyMonitor<String> monitor = analyzer.add(new FIFOPolicy<String>(),
                10);
        assertEquals(0, monitor.getNumberOfElements());
        assertEquals(0, monitor.getSize());
        
        analyzer.touch("A");
        assertEquals(1, monitor.getNumberOfElements());
        assertEquals(1, monitor.getSize());
        
        analyzer.touch("B");
        assertEquals(2, monitor.getNumberOfElements());
        assertEquals(2, monitor.getSize());
        
        analyzer.touch("A");
        assertEquals(2, monitor.getNumberOfElements());
        assertEquals(2, monitor.getSize());
        
        analyzer.touch("C");
        assertEquals(3, monitor.getNumberOfElements());
        assertEquals(3, monitor.getSize());
        
        monitor.clear();
        assertEquals(0, monitor.getNumberOfElements());
        assertEquals(0, monitor.getSize());
    }

    public void testHitStat() {
        ReplacementPolicy<String> cp = Policies.newFIFO();
        PolicyMonitor<String> monitor = analyzer.add(cp, 10);

        analyzer.touch("A");
        assertEquals(0, monitor.getHitStat().getNumberOfHits());
        assertEquals(1, monitor.getHitStat().getNumberOfMisses());

        analyzer.touch("A");
        assertEquals(1, monitor.getHitStat().getNumberOfHits());
        assertEquals(1, monitor.getHitStat().getNumberOfMisses());

        analyzer.touch("B");
        assertEquals(1, monitor.getHitStat().getNumberOfHits());
        assertEquals(2, monitor.getHitStat().getNumberOfMisses());
    }

    public void testResetStatistics() {
        PolicyMonitor<String> monitor = analyzer.add(cp1, 10);
        m1.expects(exactly(2)).method("add").with(eq("A"))
                .will(returnValue(-1));

        analyzer.touch("A");
        analyzer.touch("A");
        assertEquals(0, monitor.getHitStat().getNumberOfHits());
        assertEquals(2, monitor.getHitStat().getNumberOfMisses());
    }

    public void testMonitorClear() {
        PolicyMonitor<String> monitor = analyzer.add(new FIFOPolicy<String>(),
                10);

        analyzer.touch("A");
        analyzer.touch("A");
        monitor.clear();
        analyzer.touch("A");
        assertEquals(0, monitor.getHitStat().getNumberOfHits());
        assertEquals(1, monitor.getHitStat().getNumberOfMisses());
    }

    public void testTouchNonExisting() {
        PolicyMonitor<String> monitor = analyzer.add(new FIFOPolicy<String>(),
                10);
        analyzer.touch("A");
        analyzer.touch("A");
        monitor.resetStatistics();
        assertEquals(0, monitor.getHitStat().getNumberOfHits());
        assertEquals(0, monitor.getHitStat().getNumberOfMisses());
    }
}
