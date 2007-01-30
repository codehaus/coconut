/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * This test tests that the three required constructors are present, a no
 * argument constructor, a constructor taking a map and one taking a
 * CacheConfiguration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Constructors extends CacheTestBundle {

    @SuppressWarnings("unchecked")
    private Class<Cache> getClazz() {
        return (Class<Cache>) c0.getClass();
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

    @Test
    public void testMapArgumentConstructor() throws Exception {
        Constructor con = getClazz().getConstructor(Map.class);
        Cache c = (Cache) con.newInstance(new HashMap<Integer, String>(c1));
        assertEquals(1, c.size());
        assertEquals(M1.getValue(), c.peek(M1.getKey()));
    }

    @Test(expected = NullPointerException.class)
    public void testNullMapArgumentConstructor() throws Throwable {
        Constructor con = getClazz().getConstructor(Map.class);
        try {
            con.newInstance(new Object[] { null });
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCacheConfigurationArgumentConstructor() throws Exception {
        Constructor con = getClazz().getConstructor(CacheConfiguration.class);
        Cache c = (Cache) con.newInstance(CacheConfiguration.create()
                .setInitialMap(new HashMap<Integer, String>(c1)));
        assertEquals(1, c.size());
        assertEquals(M1.getValue(), c.peek(M1.getKey()));
    }

    @Test(expected = NullPointerException.class)
    public void testNullCacheConfigurationArgumentConstructor()
            throws Throwable {
        Constructor con = getClazz().getConstructor(CacheConfiguration.class);
        try {
            con.newInstance(new Object[] { null });
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }
}
