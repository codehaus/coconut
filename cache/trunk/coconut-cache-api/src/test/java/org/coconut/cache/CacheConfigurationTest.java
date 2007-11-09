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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import junit.framework.AssertionFailedError;

import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.core.Logger;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link CacheConfiguration} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CacheConfigurationTest {

    CacheConfiguration<Number, Collection> conf;

    /**
     * Setup the CacheConfiguration.
     */
    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
    }

    /**
     * Tests {@link CacheConfiguration#setClock(Clock)} and
     * {@link CacheConfiguration#getClock()}.
     */
    @Test
    public void clock() {
        assertSame(Clock.DEFAULT_CLOCK, conf.getClock());
        Clock c = new Clock.DeterministicClock();
        assertSame(conf, conf.setClock(c));
        assertSame(c, conf.getClock());
    }

    /**
     * Tests that a configuration service added through
     * {@link CacheConfiguration#addConfiguration(AbstractCacheServiceConfiguration)} is
     * available when calling {@link CacheConfiguration#getAllConfigurations()}.
     */
    @Test
    public void addConfiguration() {
        MyService s1 = new MyService();
        assertFalse(conf.getAllConfigurations().contains(s1));
        conf.addConfiguration(s1);
        assertTrue(conf.getAllConfigurations().contains(s1));
    }

    /**
     * Tests that
     * {@link CacheConfiguration#addConfiguration(AbstractCacheServiceConfiguration)}
     * throws a {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void addConfigurationNPE() {
        conf.addConfiguration(null);
    }

    /**
     * Tests that
     * {@link CacheConfiguration#addConfiguration(AbstractCacheServiceConfiguration)}
     * throws a {@link IllegalArgumentException} when we try to register a configuration
     * service that is registered as default.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addConfigurationIAE() {
        conf.addConfiguration(new CacheManagementConfiguration());
    }

    /**
     * Tests that
     * {@link CacheConfiguration#addConfiguration(AbstractCacheServiceConfiguration)}
     * throws a {@link IllegalArgumentException} when we try to register the same
     * configuration service twice.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addConfigurationIAE2() {
        try {
            conf.addConfiguration(new MyService());
        } catch (Throwable t) {
            throw new AssertionFailedError("Should not throw " + t.getMessage());
        }
        conf.addConfiguration(new MyService());
    }

    /**
     * Tests that {@link CacheConfiguration#setClock(Clock)} throws a
     * {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void clockNPE() {
        conf.setClock(null);
    }

    /**
     * Tests {@link CacheConfiguration#setDefaultLogger(Logger)} and
     * {@link CacheConfiguration#getDefaultLogger()}.
     */
    @Test
    public void defaultLogger() {
        assertNull(conf.getDefaultLogger());
        Logger log = MockTestCase.mockDummy(Logger.class);
        assertSame(conf, conf.setDefaultLogger(log));
        assertSame(log, conf.getDefaultLogger());
        conf.setDefaultLogger(null);
        assertNull(conf.getDefaultLogger());
    }

    /**
     * Tests {@link CacheConfiguration#setName(String)} and
     * {@link CacheConfiguration#getName()}.
     */
    @Test
    public void name() {
        assertNull(conf.getName());
        assertSame(conf, conf.setName("foo-123_"));
        assertEquals("foo-123_", conf.getName());
        conf.setName(null);
        assertNull(conf.getName());
    }

    /**
     * Tests that we cannot use the empty string as the name of a cache.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nameNoEmptyStringIAE() {
        conf.setName("");
    }

    /**
     * Tests that we cannot use an invalid String as the name of the cache.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nameInvalidIAE() {
        conf.setName("&asdad");
    }

    /**
     * Tests {@link CacheConfiguration#getProperties()},
     * {@link CacheConfiguration#getProperty(String)},
     * {@link CacheConfiguration#getProperty(String, Object)}and
     * {@link CacheConfiguration#setProperty(String, Object)}.
     */
    @Test
    public void properties() {
        conf.setProperty("a", 1);
        conf.setProperty("b", 2);
        assertEquals(1, conf.getProperty("a"));
        assertEquals(2, conf.getProperty("b"));
        assertNull(conf.getProperty("c"));
        assertNull(conf.getProperty("c", null));
        assertEquals(1, conf.getProperties().get("a"));
        assertEquals(2, conf.getProperties().get("b"));
        assertNull(conf.getProperties().get("c"));
        assertEquals(2, conf.getProperty("b", 3));
        assertEquals(2, conf.getProperty("b", null));
        assertEquals(3, conf.getProperty("c", 3));
        conf.setProperty("b", null);
        assertNull(conf.getProperty("b"));
    }

    /**
     * Tests that {@link CacheConfiguration#getProperty(String)} throws a
     * {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void propertiesGetNPE() {
        conf.getProperty(null);
    }

    /**
     * Tests that {@link CacheConfiguration#getProperty(String, Object)} throws a
     * {@link NullPointerException} when invoked with a null argument as the name of the
     * property.
     */
    @Test(expected = NullPointerException.class)
    public void propertiesGetNPE1() {
        conf.getProperty(null, "foo");
    }

    /**
     * Tests that {@link CacheConfiguration#setProperty(String, Object)} throws a
     * {@link NullPointerException} when invoked with a null argument as the name of the
     * property.
     */
    @Test(expected = NullPointerException.class)
    public void propertiesSetNPE() {
        conf.setProperty(null, 1);
    }

    /**
     * Tests the {@link CacheConfiguration#toString()} method.
     */
    @Test
    public void toString_() {
        conf.setName("foo");
        CacheConfiguration conf2 = CacheConfiguration.create();
        conf2.setName("foo");
        assertEquals(conf.toString(), conf2.toString());
        conf2.setName("foo1");
        assertFalse(conf.toString().equals(conf2.toString()));
    }

    @Test
    public void testCreateAndInstantiate() throws Exception {
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, "org.coconut.cache.DummyCache");
        conf.setName("foo");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XmlConfigurator().write(conf, os);

        Cache c = CacheConfiguration.loadCacheFrom(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertFalse(((DummyCache) c).isStarted);

        // c = CacheConfiguration.createInstantiateAndStart(new
        // ByteArrayInputStream(os
        // .toByteArray()));
        // assertTrue(c instanceof DummyCache);
        // assertEquals("foo", ((DummyCache) c).getName());
        // assertTrue(((DummyCache) c).isStarted);
    }

    @Test
    public void testNewInstance() throws Exception {
        conf.setName("foo");

        Cache c = conf.newCacheInstance(DummyCache.class);
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", ((DummyCache) c).getName());
        assertFalse(((DummyCache) c).isStarted);

        // c = conf.newInstanceAndStart(DummyCache.class);
        // assertTrue(c instanceof DummyCache);
        // assertEquals("foo", ((DummyCache) c).getName());
        // assertTrue(((DummyCache) c).isStarted);

    }

    @Test
    public void createWithName() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create("foo");
        assertEquals("foo", conf.getName());
    }

    @Test
    public void defaultService() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        assertNotNull(conf.event());
        assertNotNull(conf.eviction());
        assertNotNull(conf.exceptionHandling());
        assertNotNull(conf.expiration());
        assertNotNull(conf.loading());
        assertNotNull(conf.management());
        assertNotNull(conf.serviceManager());
        assertNotNull(conf.worker());

    }

    @Test(expected = NullPointerException.class)
    public void testNewInstanceNPE() throws Exception {
        conf.newCacheInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceNoConstructor() throws Exception {
        conf.newCacheInstance(MockTestCase.mockDummy(Cache.class).getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceAbstractClass() throws Exception {
        conf.newCacheInstance(CannotInstantiateAbstractCache.class);
    }

    @Test(expected = ArithmeticException.class)
    public void testNewInstanceConstructorThrows() throws Throwable {
        try {
            conf.newCacheInstance(ConstructorThrowingCache.class);
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IllegalAccessException.class)
    public void testNewInstanceConstructorThrows2() throws Throwable {
        try {
            conf.newCacheInstance(PrivateConstructorCache.class);
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    // @Test (expected = CacheException.class)
    // public void testtoStringError() throws Throwable {
    // Field f = CacheConfiguration.class.getDeclaredField("domain");
    // f.setAccessible(true);
    // f.set(conf, null);
    // conf.toString();
    // }

    public static abstract class CannotInstantiateAbstractCache extends DummyCache {

        /**
         * @param configuration
         */
        public CannotInstantiateAbstractCache(CacheConfiguration configuration) {
            super(configuration);
        }
    }

    public static class ConstructorThrowingCache extends DummyCache {

        /**
         * @param configuration
         */
        public ConstructorThrowingCache(CacheConfiguration configuration) {
            super(configuration);
            throw new ArithmeticException();
        }
    }

    public static final class PrivateConstructorCache extends DummyCache {

        /**
         * @param configuration
         */
        private PrivateConstructorCache(CacheConfiguration configuration) {
            super(configuration);
        }
    }

    public static class MyService extends AbstractCacheServiceConfiguration {
        public MyService() {
            super("foo");
        }
    }
}
