/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.eviction;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Test a cache that does no eviction of elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoEviction extends AbstractCacheTCKTest {
    @Test
    public void testNoEvict() {
        c = newCache();
        for (int i = 0; i < 10; i++) {
            put(i, Integer.toString(i));
        }
        evict();
        assertSize(10);
    }
}
