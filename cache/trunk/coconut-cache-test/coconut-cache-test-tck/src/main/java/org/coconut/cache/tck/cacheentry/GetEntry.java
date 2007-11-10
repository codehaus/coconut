/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests {@link Cache#containsKey}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class GetEntry extends AbstractCacheTCKTest {

    @Test
    public void getEntry() {
        c = newCache(5);
        assertNull(c.getEntry(6));
        assertEquals(M1.getValue(), c.getEntry(M1.getKey()).getValue());
        assertEquals(M1.getKey(), c.getEntry(M1.getKey()).getKey());
        assertEquals(M5.getValue(), c.getEntry(M5.getKey()).getValue());
        assertEquals(M5.getKey(), c.getEntry(M5.getKey()).getKey());
    }


    /**
     * {@link Cache#get} lazy starts the cache.
     */
    @Test
    public void getLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.getEntry(M6.getKey());
        checkLazystart();
    }

    /**
     * get(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void getNPE() {
        c = newCache(5);
        c.getEntry(null);
    }

    /**
     * {@link Cache#getEntry(Object)} should not fail when cache is shutdown.
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void getShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.getEntry(M1.getKey());
    }

}
