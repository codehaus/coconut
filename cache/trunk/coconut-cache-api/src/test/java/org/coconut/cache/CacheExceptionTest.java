/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheExceptionTest {

    @Test
    public void testCacheException1() {
        CacheException e = new CacheException();
        assertNull(e.getCause());
        assertNull(e.getMessage());
    }

    @Test
    public void testCacheException2() {
        CacheException e = new CacheException("a");
        assertNull(e.getCause());
        assertEquals("a", e.getMessage());
    }

    @Test
    public void testCacheException3() {
        NullPointerException npe = new NullPointerException();
        CacheException e = new CacheException(npe);
        assertSame(npe, e.getCause());
        assertNotNull(e.getMessage());
    }

    @Test
    public void testCacheException4() {
        NullPointerException npe = new NullPointerException();
        CacheException e = new CacheException("a", npe);
        assertSame(npe, e.getCause());
        assertEquals("a", e.getMessage());
    }

}
