/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IllegalCacheConfigurationExceptionTest {
    @Test
    public void testCacheException1() {
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException();
        assertNull(e.getCause());
        assertNull(e.getMessage());
    }

    @Test
    public void testCacheException2() {
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException("a");
        assertNull(e.getCause());
        assertEquals("a", e.getMessage());
    }

    @Test
    public void testCacheException3() {
        NullPointerException npe = new NullPointerException();
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException(npe);
        assertSame(npe, e.getCause());
        assertNotNull(e.getMessage());
    }

    @Test
    public void testCacheException4() {
        NullPointerException npe = new NullPointerException();
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException(
                "a", npe);
        assertSame(npe, e.getCause());
        assertEquals("a", e.getMessage());
    }
}
