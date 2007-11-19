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
 * Tests the {@link IllegalCacheConfigurationException} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class IllegalCacheConfigurationExceptionTest {

    /**
     * Tests
     * {@link IllegalCacheConfigurationException#IllegalCacheConfigurationException()}.
     */
    @Test
    public void illegalCacheConfigurationException() {
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException();
        assertNull(e.getCause());
        assertNull(e.getMessage());
    }

    /**
     * Tests
     * {@link IllegalCacheConfigurationException#IllegalCacheConfigurationException(Throwable)}.
     */
    @Test
    public void illegalCacheConfigurationException1a() {
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException("a");
        assertNull(e.getCause());
        assertEquals("a", e.getMessage());
    }

    /**
     * Tests
     * {@link IllegalCacheConfigurationException#IllegalCacheConfigurationException(Throwable)}.
     */
    @Test
    public void illegalCacheConfigurationException1b() {
        NullPointerException npe = new NullPointerException();
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException(npe);
        assertSame(npe, e.getCause());
        assertNotNull(e.getMessage());
    }

    /**
     * Tests
     * {@link IllegalCacheConfigurationException#IllegalCacheConfigurationException(String, Throwable)}.
     */
    @Test
    public void illegalCacheConfigurationException2() {
        NullPointerException npe = new NullPointerException();
        IllegalCacheConfigurationException e = new IllegalCacheConfigurationException("a", npe);
        assertSame(npe, e.getCause());
        assertEquals("a", e.getMessage());
    }
}
