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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests the {@link CacheConfiguration} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CacheConfigurationTest {

    /** The default instanceof a CacheConfiguration. */
    private CacheConfiguration<Number, Collection> conf;

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
        ExtendConfiguration conf = new ExtendConfiguration();
        SimpleService s1 = new SimpleService();
        assertFalse(conf.getAllConfigurations().contains(s1));
        conf.addConfiguration(s1);
        assertSame(s1, conf.getConfiguration(SimpleService.class));
        assertTrue(conf.getAllConfigurations().contains(s1));
        try {
            conf.getConfiguration(SimpleService2.class);
            throw new AssertionError("Should fail");
        } catch (IllegalArgumentException ok) {/** ok */
        }
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
            conf.addConfiguration(new SimpleService());
        } catch (Throwable t) {
            throw new AssertionFailedError("Should not throw " + t.getMessage());
        }
        conf.addConfiguration(new SimpleService());
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
        conf.setProperty("a", "A");
        conf.setProperty("b", "B");
        assertEquals("A", conf.getProperty("a"));
        assertEquals("B", conf.getProperty("b"));
        assertNull(conf.getProperty("c"));
        assertNull(conf.getProperty("c", null));
        assertEquals("A", conf.getProperties().get("a"));
        assertEquals("B", conf.getProperties().get("b"));
        assertNull(conf.getProperties().get("c"));
        assertEquals("B", conf.getProperty("b", "C"));
        assertEquals("B", conf.getProperty("b", null));
        assertEquals("C", conf.getProperty("c", "C"));
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
        conf.setProperty(null, "A");
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

    /**
     * Tests the {@link CacheConfiguration#toString()} throws an
     * {@link IllegalStateException} when it cannot persist a configuration.
     */
    @Test(expected = IllegalStateException.class)
    public void toStringISE() {
        conf.setName("foo");
        conf.addConfiguration(new SimpleServiceAE());
        conf.toString();
    }

    /**
     * Tests that we can create a specific cache instance from a configuration file
     * {@link CacheConfiguration#loadCacheFrom(java.io.InputStream)} method.
     * 
     * @throws Exception
     *             some exception while constructing the cache
     */
    @Test
    public void loadCacheFrom() throws Exception {
        conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, "org.coconut.cache.DummyCache");
        conf.setName("foo");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XmlConfigurator().write(conf, os);

        Cache c = CacheConfiguration.loadCacheFrom(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", c.getName());
    }

    /**
     * Tests that we can create a specific cache instance from a configuration via the
     * {@link CacheConfiguration#newCacheInstance(Class)} method.
     * 
     * @throws Exception
     *             some exception while constructing the cache
     */
    @Test
    public void newInstance() throws Exception {
        Cache c = conf.setName("foo").newCacheInstance(DummyCache.class);
        assertTrue(c instanceof DummyCache);
        assertEquals("foo", c.getName());
    }

    /**
     * Tests the {@link CacheConfiguration#create(String)} method.
     */
    @Test
    public void createWithName() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create("foo");
        assertEquals("foo", conf.getName());
    }

    /**
     * Tests the default service are available.
     */
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

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link NullPointerException} when invoked with a null argument.
     * 
     * @throws Exception
     *             some exception while constructing the cache
     */
    @Test(expected = NullPointerException.class)
    public void newInstanceNPE() throws Exception {
        conf.newCacheInstance(null);
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link IllegalArgumentException} when invoked with a class that does not have a
     * constructor taking a single {@link CacheConfiguration} argument.
     * 
     * @throws Exception
     *             some exception while constructing the cache
     */
    @Test(expected = IllegalArgumentException.class)
    public void newInstanceNoConstructor() throws Exception {
        conf.newCacheInstance(MockTestCase.mockDummy(Cache.class).getClass());
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link IllegalArgumentException} when invoked with an abstract class.
     * 
     * @throws Exception
     *             some exception while constructing the cache
     */
    @Test(expected = IllegalArgumentException.class)
    public void newInstanceAbstractClass() throws Exception {
        conf.newCacheInstance(DummyCache.CannotInstantiateAbstractCache.class);
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link RuntimeException} when invoked with class that throws an RuntimeException in
     * the constructor.
     * 
     * @throws Throwable
     *             some exception while constructing the cache
     */
    @Test(expected = ArithmeticException.class)
    public void newInstanceConstructorRuntimeThrows() throws Throwable {
        conf.newCacheInstance(DummyCache.ConstructorRuntimeThrowingCache.class);
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link Error} when invoked with class that throws an Error in the constructor.
     * 
     * @throws Throwable
     *             some exception while constructing the cache
     */
    @Test(expected = AbstractMethodError.class)
    public void newInstanceConstructorErrorThrows() throws Throwable {
        try {
            conf.newCacheInstance(DummyCache.ConstructorErrorThrowingCache.class);
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link IllegalArgumentException} when invoked with class that throws an
     * {@link Exception} in the constructor.
     * 
     * @throws Throwable
     *             some exception while constructing the cache
     */
    @Test(expected = IOException.class)
    public void newInstanceConstructorExceptionThrows() throws Throwable {
        try {
            conf.newCacheInstance(DummyCache.ConstructorExceptionThrowingCache.class);
            throw new AssertionError("should fail");
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    /**
     * Tests that {@link CacheConfiguration#newCacheInstance(Class)} throws a
     * {@link IllegalArgumentException} when invoked with a class where the constructor
     * taking a single {@link CacheConfiguration} argument is private.
     * 
     * @throws Throwable
     *             some exception while constructing the cache
     */
    @Test(expected = IllegalAccessException.class)
    public void newInstancePrivateConstructorIAE() throws Throwable {
        try {
            conf.newCacheInstance(DummyCache.PrivateConstructorCache.class);
        } catch (IllegalArgumentException e) {
            throw e.getCause();
        }
    }

    /**
     * Tests that {@link CacheConfiguration#CacheConfiguration(Collection)} throws a
     * {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void cacheConfigurationNPE() {
        new CacheConfiguration(null);
    }

    /**
     * Tests that {@link CacheConfiguration#CacheConfiguration(Collection)} throws a
     * {@link NullPointerException} when invoked with a collection containing a null.
     */
    @Test(expected = NullPointerException.class)
    public void cacheConfigurationColWithNPE() {
        new CacheConfiguration(Arrays.asList(SimpleService.class, null));
    }

    /**
     * Tests that {@link CacheConfiguration#CacheConfiguration(Collection)} throws a
     * {@link IllegalArgumentException} when trying to register the same service twice.
     */
    @Test(expected = IllegalArgumentException.class)
    public void cacheConfigurationTwoServicesIAE() {
        new CacheConfiguration(Arrays.asList(SimpleService.class, SimpleService.class));
    }

    /**
     * Tests {@link CacheConfiguration#CacheConfiguration(Collection)}.
     */
    @Test
    public void cacheConfiguration() {
        ExtendConfiguration conf = new ExtendConfiguration(Arrays.asList(SimpleService.class,
                SimpleService2.class));
        assertEquals(1, getInstancesOfType(conf.getAllConfigurations(), SimpleService.class).size());
        assertEquals(1, getInstancesOfType(conf.getAllConfigurations(), SimpleService2.class)
                .size());
        assertEquals(0, getInstancesOfType(conf.getAllConfigurations(), SimpleServiceAE.class)
                .size());
        assertNotNull(conf.getConfiguration(SimpleService.class));
        assertNotNull(conf.getConfiguration(SimpleService2.class));
        try {
            conf.getConfiguration(SimpleServiceAE.class);
            throw new AssertionError("Should fail");
        } catch (IllegalArgumentException ok) {/** ok */
        }
    }

    private <K> Collection<K> getInstancesOfType(Collection<?> col, Class<K> type) {
        ArrayList<K> list = new ArrayList<K>();
        for (Object o : col) {
            if (o.getClass().equals(type)) {
                list.add((K) o);
            }
        }
        return list;
    }

    /**
     * An extension of CacheConfiguration that exposes {@link #getConfiguration(Class)} as
     * a public method.
     */
    public static class ExtendConfiguration<K, V> extends CacheConfiguration<K, V> {

        /** Create a new ExtendConfiguration. */
        public ExtendConfiguration() {}

        /**
         * Create a new ExtendConfiguration.
         * 
         * @param additionalConfigurationTypes
         *            service types to instantiate
         */
        public ExtendConfiguration(
                Collection<Class<? extends AbstractCacheServiceConfiguration>> additionalConfigurationTypes) {
            super(additionalConfigurationTypes);
        }

        /** {@inheritDoc} */
        @Override
        public <T extends AbstractCacheServiceConfiguration> T getConfiguration(Class<T> c) {
            return super.getConfiguration(c);
        }
    }

    /**
     * A simple implementation of AbstractCacheServiceConfiguration.
     */
    public static class SimpleService extends AbstractCacheServiceConfiguration {
        /** Creates a new SimpleService with the name 'foo'. */
        public SimpleService() {
            super("foo");
        }
    }

    /**
     * A simple implementation of AbstractCacheServiceConfiguration.
     */
    public static class SimpleService2 extends AbstractCacheServiceConfiguration {
        /** Creates a new SimpleService2 with the name 'foo1'. */
        public SimpleService2() {
            super("foo1");
        }
    }

    /**
     * A simple implementation of AbstractCacheServiceConfiguration that throws an
     * exception when it is being persisted.
     */
    public static class SimpleServiceAE extends AbstractCacheServiceConfiguration {
        /** Creates a new SimpleServiceAE with the name 'foo2'. */
        public SimpleServiceAE() {
            super("foo2");
        }

        /** {@inheritDoc} */
        @Override
        protected void toXML(Document doc, Element parent) throws Exception {
            throw new ArithmeticException();
        }
    }
}
