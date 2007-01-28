/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.test.MockTestCase.mockDummy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class CacheConfigurationTest {

    CacheConfiguration<Number, Collection> conf;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
    }

    @Test(expected = NullPointerException.class)
    public void testClock() {
        assertEquals(Clock.DEFAULT_CLOCK, conf.getClock());
        Clock c = new Clock.DeterministicClock();
        assertEquals(conf, conf.setClock(c));
        assertEquals(c, conf.getClock());

        conf.setClock(null);
    }

    @Test(expected = NullPointerException.class)
    public void testErrorHandler() {
        CacheErrorHandler def = conf.getErrorHandler();
        assertFalse(def.hasLogger());
        assertEquals(CacheErrorHandler.class, def.getClass());
        def = new CacheErrorHandler();
        assertEquals(conf, conf.setErrorHandler(def));
        assertSame(def, conf.getErrorHandler());

        conf.setErrorHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInitialMap() {
        Map<Number, Collection> map = mockDummy(Map.class);
        assertNull(conf.getInitialMap());

        assertEquals(conf, conf.setInitialMap(map));

        assertEquals(map, conf.getInitialMap());

        // narrow bounds
        Map<Integer, List> map2 = mockDummy(Map.class);

        assertEquals(conf, conf.setInitialMap(map2));
        assertEquals(map2, conf.getInitialMap());

        conf.setInitialMap(null);
    }

    @Test(expected = NullPointerException.class)
    public void testName() {
        assertNotNull(conf.getName());
        assertEquals(conf, conf.setName("foo"));
        assertSame("foo", conf.getName());
        conf.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameEmptyString() {
        conf.setName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameInvalid() {
        conf.setName("&asdad");
    }

    @Test(expected = NullPointerException.class)
    public void testProperties() {
        conf.setProperty("a", 1);
        conf.setProperty("b", 2);
        assertEquals(1, conf.getProperty("a"));
        assertEquals(2, conf.getProperty("b"));
        assertNull(conf.getProperty("c"));
        assertEquals(2, conf.getProperty("b", 3));
        assertEquals(3, conf.getProperty("c", 3));
        conf.getProperty(null);
    }

    @Test(expected = NullPointerException.class)
    public void testProperties2() {
        conf.setProperty(null, 1);
    }

    @Test
    public void testProperties3() {
        conf.setProperty("a", 1);
        conf.setProperty("b", 2);
        assertEquals(1, conf.getProperties().get("a"));
    }

    @Test
    public void testToString() {
        conf.setName("foo");
        CacheConfiguration conf2 = CacheConfiguration.create();
        conf2.setName("foo");
        assertEquals(conf.toString(), conf2.toString());
        conf2.setName("foo1");
        assertFalse(conf.toString().equals(conf2.toString()));
    }

    @Test
    public void testCreateAndInstantiate() throws Exception {
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE,
                "org.coconut.cache.DummyCache");
        conf.setName("foo");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XmlConfigurator.getInstance().to(conf, os);

        Cache c = CacheConfiguration.createAndInstantiate(new ByteArrayInputStream(os
                .toByteArray()));
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertFalse(((DummyCache) c).isStarted);

        c = CacheConfiguration.createInstantiateAndStart(new ByteArrayInputStream(os
                .toByteArray()));
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertTrue(((DummyCache) c).isStarted);
    }

    @Test(expected = NullPointerException.class)
    public void testNewInstance() throws Exception {
        conf.setName("foo");

        Cache c = conf.newInstance(DummyCache.class);
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertFalse(((DummyCache) c).isStarted);

        c = conf.newInstanceAndStart(DummyCache.class);
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertTrue(((DummyCache) c).isStarted);

        conf.newInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceNoConstructor() throws Exception {
        conf.newInstance(MockTestCase.mockDummy(Cache.class).getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceAbstractClass() throws Exception {
        conf.newInstance(AbstractCache.class);
    }

    @Test(expected = InvocationTargetException.class)
    public void testNewInstanceConstructorThrows() throws Throwable {
        try {
            conf.newInstance(DummyCache2.class);
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    public static class DummyCache2 extends DummyCache {

        /**
         * @param configuration
         */
        public DummyCache2(CacheConfiguration configuration) {
            super(configuration);
            throw new ArithmeticException();
        }

    }
}
