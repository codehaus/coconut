/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class CacheAnalyzerTest extends MockTestCase {

    ReplacementPolicy cp1;
    ReplacementPolicy cp2;
    Mock m1;
    Mock m2;
    CacheAnalyzer<Integer, Integer> ca;
    
    abstract CacheAnalyzer<Integer,Integer> newAnalyzer(); 
    public void testInitial() {
        assertTrue(ca.getMonitors().isEmpty());
    }
    public void testAddPolicies() {
        CachePolicyMonitor policy = ca.addMonitor(cp1);

        assertNotNull(policy);
        assertEquals(cp1, policy.getPolicy());
        assertEquals(1, ca.getMonitors().size());
        assertTrue(ca.getMonitors().contains(policy));

        CachePolicyMonitor policy2 = ca.addMonitor(cp2);

        assertNotNull(policy2);
        assertEquals(cp2, policy2.getPolicy());
        assertEquals(2, ca.getMonitors().size());
        assertTrue(ca.getMonitors().contains(policy2));
    }

    public void testRemovePolicies() {
        CachePolicyMonitor policy = ca.addMonitor(cp1);
        CachePolicyMonitor policy2 = ca.addMonitor(cp2);

        // remove
        assertTrue(ca.removeMonitor(policy));
        assertEquals(1, ca.getMonitors().size());
        assertFalse(ca.getMonitors().contains(policy));
        assertTrue(ca.removeMonitor(policy2));
        assertEquals(0, ca.getMonitors().size());
    }

    public void testRemoveNoneExistingPolicy() {
        ca.addMonitor(cp1);

        // remove none existing
        assertFalse(ca.removeMonitor((CachePolicyMonitor) mock(CachePolicyMonitor.class).proxy()));
        assertEquals(1,ca.getMonitors().size());
    }

    public void testAddIfMissing() {
        CachePolicyMonitor<?> policy = ca.addMonitor(cp1);

        m1.expects(exactly(2)).method("add").will(
            onConsecutiveCalls(returnValue(1), returnValue(2)));

        assertTrue(ca.addIfMissing(0));
        assertEquals(1, policy.getMBean().getNumberOfElements());

        assertTrue(ca.addIfMissing(1));
        assertEquals(2, policy.getMBean().getNumberOfElements());

    }
    
    public void testnotAddIfMissingRefresh() {
        CachePolicyMonitor policy = ca.addMonitor(cp1);

        m1.expects(exactly(2)).method("add").will(
            onConsecutiveCalls(returnValue(1), returnValue(2)));
        m1.expects(once()).method("refresh");
        m1.expects(once()).method("add").will(returnValue(3));

        ca.addIfMissing(0);
        ca.addIfMissing(1);

        assertFalse(ca.addIfMissing(0)); // this element should
        // result
        // in a refresh
        assertEquals(2, policy.getMBean().getNumberOfElements());

        assertTrue(ca.addIfMissing(2));
        assertEquals(3, policy.getMBean().getNumberOfElements());
    }

    public void tnotestRefresh() {
        m1.expects(once()).method("add").will(returnValue(1));
        m1.expects(exactly(2)).method("refresh");

        m2.expects(once()).method("add").will(returnValue(1));
        m2.expects(once()).method("refresh");

        ca.addMonitor(cp1);

        ca.addIfMissing(0);

        ca.addMonitor(cp2);

        ca.addIfMissing(0);
        ca.refresh(0);
    }
}