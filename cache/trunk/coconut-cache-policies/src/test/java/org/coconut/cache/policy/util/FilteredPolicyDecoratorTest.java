/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.util;

import java.util.HashSet;

import org.coconut.cache.policy.PolicyObject;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.filter.Filters;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ThresholdPolicyTest.java 38 2006-08-22 10:09:08Z kasper $
 */
@SuppressWarnings("unchecked")
public class FilteredPolicyDecoratorTest extends MockTestCase {

    public void testDecorator1() {
        Mock m = mock(ReplacementPolicy.class);

        FilteredPolicyDecorator fpd = new FilteredPolicyDecorator((ReplacementPolicy) m
                .proxy(), Filters.TRUE);

        assertEquals(Filters.TRUE, fpd.getFilter());
        m.expects(once()).method("add").with(eq("a")).will(returnValue(1));
        m.expects(once()).method("update").with(eq(2), eq("b")).will(returnValue(true));
        assertEquals(1, fpd.add("a"));
        assertTrue(fpd.update(2, "b"));

    }

    public void testDecorator2() {
        Mock m = mock(ReplacementPolicy.class);

        FilteredPolicyDecorator fpd = new FilteredPolicyDecorator((ReplacementPolicy) m
                .proxy(), Filters.falseFilter());

        // m.expects(once()).method("add").with(eq("a")).will(returnValue(1));
        m.expects(once()).method("remove").with(eq(3));
        assertEquals(-1, fpd.add("a"));
        assertFalse(fpd.update(3, "b"));
    }

    public void testNPE() {
        try {
            new FilteredPolicyDecorator(mockDummy(ReplacementPolicy.class), null);
            fail("should throw NullPointerException");
        } catch (NullPointerException ok) {

        }
    }

    public void testSizeRejectorIEA() {
        try {
            FilteredPolicyDecorator.sizeRejector(mockDummy(ReplacementPolicy.class), 0);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException ok) {
        }
    }

    public void testSizeRejector() {
        Mock m = mock(ReplacementPolicy.class);
        ReplacementPolicy rp = FilteredPolicyDecorator.sizeRejector((ReplacementPolicy) m
                .proxy(), 5);
        Mock m5 = mock(PolicyObject.class);
        m5.stubs().method("getSize").will(returnValue(5l));
        m.expects(once()).method("add").with(eq(m5.proxy())).will(returnValue(1));
        assertEquals(1, rp.add(m5.proxy()));
        
        Mock m6 = mock(PolicyObject.class);
        m6.stubs().method("getSize").will(returnValue(6l));
        assertEquals(-1, rp.add(m6.proxy()));
    }
    
    public void testCostRejector() {
        Mock m = mock(ReplacementPolicy.class);
        ReplacementPolicy rp = FilteredPolicyDecorator.costRejector((ReplacementPolicy) m
                .proxy(), 7.7);
        Mock m77 = mock(PolicyObject.class);
        m77.stubs().method("getCost").will(returnValue(7.7));
        m.expects(once()).method("add").with(eq(m77.proxy())).will(returnValue(1));
        assertEquals(1, rp.add(m77.proxy()));
        
        Mock m76 = mock(PolicyObject.class);
        m76.stubs().method("getCost").will(returnValue(7.6));
        assertEquals(-1, rp.add(m76.proxy()));
    }
}
