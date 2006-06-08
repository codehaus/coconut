/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.policy;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.policy.paging.ClockPolicy;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.cache.policy.paging.LFUPolicy;
import org.coconut.cache.policy.paging.LIFOPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.paging.MRUPolicy;
import org.coconut.cache.policy.paging.RandomPolicy;
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
}
