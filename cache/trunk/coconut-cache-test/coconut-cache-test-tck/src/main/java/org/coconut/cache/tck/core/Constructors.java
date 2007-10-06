/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * This test tests that the two required constructors are present, a no argument
 * constructor and one taking a CacheConfiguration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Constructors extends AbstractCacheTCKTest {

    @SuppressWarnings("unchecked")
    private Class<Cache> getClazz() {
        return (Class<Cache>) newCache().getClass();
    }

    /**
     * Tests that the cache has a no argument constructor.
     * 
     * @throws Exception
     *             the test failed for some unknown reason
     */
    @Test
    public void testNoArgumentConstructor() throws Exception {
        Constructor con = getClazz().getConstructor((Class[]) null);
        Cache c = (Cache) con.newInstance((Object[]) null);
        assertNotNull(c);
        assertTrue(c.isEmpty());
    }

    /**
     * Tests that the cache implementation has a single constructor taking a
     * CacheConfiguration.
     * 
     * @throws Throwable
     */
    @Test(expected = NullPointerException.class)
    public void testNullCacheConfigurationArgumentConstructor() throws Throwable {
        Constructor con = getClazz().getConstructor(CacheConfiguration.class);
        try {
            con.newInstance(new Object[] { null });
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }
}
