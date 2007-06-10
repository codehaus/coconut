/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eviction;

import org.coconut.cache.tck.CommonCacheTestBundle;
import org.junit.Test;

/**
 * Test a cache that does no eviction of elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoEviction extends CommonCacheTestBundle {
    @Test
    public void testNoEvict() {
        for (int i = 0; i < 10; i++) {
            c0.put(i, Integer.toString(i));
        }
        c0.evict();
        assertEquals(10, c0.size());
    }
}
