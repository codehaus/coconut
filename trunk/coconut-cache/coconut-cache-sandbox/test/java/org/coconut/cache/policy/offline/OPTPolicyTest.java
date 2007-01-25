/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.offline;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

public class OPTPolicyTest {

    @Test
    public void testSimple() {
        OPTPolicy<Integer> policy = new OPTPolicy<Integer>(2);
        policy.access(1, 2, 3, 2, 3, 1, 3, 2, 3, 1, 3, 1, 2);
        assertNull(policy.evictAt(0));
        assertNull(policy.evictAt(1));
        assertEquals(1, policy.evictAt(2).intValue());
        assertNull(policy.evictAt(3));
        assertNull(policy.evictAt(4));
        assertEquals(1, policy.evictAt(5).intValue());
        assertNull(policy.evictAt(6));
        assertNull(policy.evictAt(7));
        assertNull(policy.evictAt(8));
        assertEquals(2, policy.evictAt(9).intValue());
        assertNull(policy.evictAt(10));
        assertNull(policy.evictAt(11));
    }

}
