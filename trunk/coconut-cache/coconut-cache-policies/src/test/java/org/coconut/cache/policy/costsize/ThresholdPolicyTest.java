/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.policy.costsize;

import static org.coconut.cache.policy.util.CostSizeHolder.add;

import java.util.List;

import org.coconut.cache.policy.CostSizeObject;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ThresholdPolicyTest extends MockTestCase {

    /**
     * Test illegal specification of the initial threshold.
     */
    public void testIllegalThreshold() {
        try {
            new ThresholdPolicy(mockDummy(ReplacementPolicy.class), -1);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test illegal specification of the initial threshold.
     */
    public void testNPEConstructor() {
        try {
            new ThresholdPolicy(null, 5);
            fail();
        } catch (NullPointerException e) {
        }
    }

    public void testConstructor() {
        ReplacementPolicy<String> rp = mockDummy(ReplacementPolicy.class);
        ThresholdPolicy tp = new ThresholdPolicy(rp, 5);
        assertEquals(rp, tp.getPolicy());
        assertEquals(5, tp.getThreshold());
    }

    public void testConstructorDefaultLRU() {
        ThresholdPolicy tp = new ThresholdPolicy(5);
        assertTrue(tp.getPolicy() instanceof LRUPolicy);
        assertEquals(5, tp.getThreshold());
    }

    public void testDelegateFunctions() {
        Mock mock = mock(ReplacementPolicy.class);
        List l = mockDummy(List.class);
        ThresholdPolicy<CostSizeObject> tp = new ThresholdPolicy<CostSizeObject>(
                (ReplacementPolicy<CostSizeObject>) mock.proxy(), 5);
        mock.expects(once()).method("add").with(eq(add("A"))).will(returnValue(1));

        // mock.expects(once()).method("add").with(eq("B"),eq(1l),
        // eq(2f)).will(returnValue(2));

        // mock.expects(once()).method("add").with(eq("A")).will(returnValue(1));
        
        mock.expects(once()).method("evictNext").will(returnValue(add("C")));
        mock.expects(once()).method("peek").will(returnValue(add("D")));
        mock.expects(once()).method("peekAll").will(returnValue(l));
        mock.expects(once()).method("remove").with(eq(2)).will(returnValue(add("E")));
        mock.expects(once()).method("touch").with(eq(3));
        // m1.expects(exactly(2)).method("touch").with(eq(4));
        assertEquals(1, tp.add(add("A")));
        // assertEquals(2, tp.add("B", 1l, 2f));
        assertEquals(add("C"), tp.evictNext());
        assertEquals(add("D"), tp.peek());
        assertSame(l, tp.peekAll());
        assertEquals(add("E"), tp.remove(2));
        tp.touch(3);
    }

    public void testThreshold() {
        ThresholdPolicy tp = new ThresholdPolicy(5);
        assertEquals(5, tp.getThreshold());
        tp.setThreshold(10);
        assertEquals(10, tp.getThreshold());
    }

}
