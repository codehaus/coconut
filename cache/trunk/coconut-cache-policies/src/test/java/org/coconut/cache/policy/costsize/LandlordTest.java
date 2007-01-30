/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.costsize;

import junit.framework.TestCase;

import org.coconut.cache.policy.util.CostSizeHolder;
import static org.coconut.cache.policy.util.CostSizeHolder.add;

public class LandlordTest extends TestCase {

    private int plenty = 1000;// magic number

    private int many = plenty * 10; // magic number

    private LandlordPolicy<CostSizeHolder> create() {
        return new LandlordPolicy<CostSizeHolder>(5);
    }

    public void testAdd() {
        LandlordPolicy<CostSizeHolder> l = create();
        l.add(add(1, 1, 1));
        assertEquals(1, l.size());
        l.add(add(2, 1, 1));
        assertEquals(2, l.size());
        l.add(add(3, 1, 1));
        assertEquals(3, l.size());
        // System.out.println(l.evictNext());
    }

    public void testAddMany() {
        LandlordPolicy<CostSizeHolder> l = create();
        for (int i = 0; i < many; i++) {
            l.add(add(i, 1, 1));
        }
        assertEquals(many, l.size());
    }


}
