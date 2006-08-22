/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eviction;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Test a cache that does no eviction of elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoEviction extends CacheTestBundle {
    @Test
    public void testNoEvict() {
        for (int i = 0; i < 10; i++) {
            c0.put(i, Integer.toString(i), 1, TimeUnit.NANOSECONDS);
        }
        c0.evict();
        assertEquals(10, c0.size());
    }
}
