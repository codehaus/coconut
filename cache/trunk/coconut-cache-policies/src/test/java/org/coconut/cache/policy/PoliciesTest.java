/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.policy.paging.ClockPolicy;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.cache.policy.paging.LFUPolicy;
import org.coconut.cache.policy.paging.LIFOPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.paging.MRUPolicy;
import org.coconut.cache.policy.paging.RandomPolicy;
import org.coconut.filter.Filters;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Revision$
 */
public class PoliciesTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(PoliciesTest.class);
    }

    @Test
    public void assertInstances() {
        assertTrue(Policies.newClock() instanceof ClockPolicy);
        assertTrue(Policies.newFIFO() instanceof FIFOPolicy);
        assertTrue(Policies.newLFU() instanceof LFUPolicy);
        assertTrue(Policies.newLIFO() instanceof LIFOPolicy);
        assertTrue(Policies.newLRU() instanceof LRUPolicy);
        assertTrue(Policies.newMRU() instanceof MRUPolicy);
        assertTrue(Policies.newRandom() instanceof RandomPolicy);
    }
    

    public void testAcceptors() {
        HashMap hm = new HashMap();
        hm.put(1, 2);
        hm.put(3, 4);
        ReplacementPolicy rp = Policies.filteredMapKeyPolicy(Policies.newClock(),
                Filters.equal(1));
        assertTrue(rp.add(hm.entrySet().toArray()[0]) > 0);
        assertTrue(rp.add(hm.entrySet().toArray()[1]) < 0);
        
        ReplacementPolicy rp1 =Policies.filteredMapValuePolicy(Policies.newClock(),
                Filters.equal(2));
        assertTrue(rp1.add(hm.entrySet().toArray()[0]) > 0);
        assertTrue(rp1.add(hm.entrySet().toArray()[1]) < 0);

    }
}
