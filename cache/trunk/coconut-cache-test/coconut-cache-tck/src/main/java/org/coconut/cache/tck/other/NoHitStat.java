/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.other;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * A test for caches that does not keep statistics for cache access.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoHitStat extends CacheTestBundle {

    @Test(expected = UnsupportedOperationException.class)
    public void testHitstatNotSupported() {
        c1.getHitStat();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testResetHitstatNotSupported() {
        c1.resetStatistics();
    }
    
    /* previously we just returned a 0,0 hitstat, keep this until we are
     * sure we want to throws UOE */
    // @Test
    // public void testGetHitStat() {
    // assertNotNull(c1.getHitStat());
    // assertEquals(-1, 0, 0, c1.getHitStat());
    //
    // c1.get(M2.getKey());
    // assertEquals(-1, 0, 0, c1.getHitStat());
    //
    // c1.get(M1.getKey());
    // assertEquals(-1, 0, 0, c1.getHitStat());
    // }
    //
    // @Test
    // public void testGetHitStatGetAll() {
    // c0.getAll(asList(0, 1, 2, 3));
    // assertEquals(-1, 0, 0, c0.getHitStat());
    //
    // c2.getAll(asList(0, 1, 2, 3));
    // assertEquals(-1, 0, 0, c2.getHitStat());
    //
    // c4.getAll(asList(0, 1, 2, 3));
    // assertEquals(-1, 0, 0, c4.getHitStat());
    // }

}
