/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;
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

	@Test
	public void testClock() {
		assertEquals(Clock.DEFAULT_CLOCK, conf.getClock());
		Clock c = new Clock.DeterministicClock();
		assertEquals(conf, conf.setClock(c));
		assertEquals(c, conf.getClock());
	}

	@Test(expected = NullPointerException.class)
	public void testClockNPE() {
		conf.setClock(null);
	}

	@Test
	public void testSetDefaultLogger() {
		assertNull(conf.getDefaultLog());
		Logger log = Loggers.nullLog();
		assertEquals(conf, conf.setDefaultLog(log));
		assertSame(log, conf.getDefaultLog());
		conf.setDefaultLog(null);
		assertNull(conf.getDefaultLog());
	}

	@Test
	public void testName() {
		assertNull(conf.getName());
		assertEquals(conf, conf.setName("foo"));
		assertEquals("foo", conf.getName());
		conf.setName(null);
		assertNull(conf.getName());
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
		new XmlConfigurator().write(conf, os);

		Cache c = CacheConfiguration.createCache(new ByteArrayInputStream(os
				.toByteArray()));
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

		Cache c = conf.newInstance(DummyCache.class);
		assertTrue(c instanceof DummyCache);
		assertEquals("foo", ((DummyCache) c).getName());
		assertFalse(((DummyCache) c).isStarted);

		// c = conf.newInstanceAndStart(DummyCache.class);
		// assertTrue(c instanceof DummyCache);
		// assertEquals("foo", ((DummyCache) c).getName());
		// assertTrue(((DummyCache) c).isStarted);

	}

	@Test(expected = NullPointerException.class)
	public void testNewInstanceNPE() throws Exception {
		conf.newInstance(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewInstanceNoConstructor() throws Exception {
		conf.newInstance(MockTestCase.mockDummy(Cache.class).getClass());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewInstanceAbstractClass() throws Exception {
		conf.newInstance(ACache.class);
	}

	@Test(expected = InvocationTargetException.class)
	public void testNewInstanceConstructorThrows() throws Throwable {
		try {
			conf.newInstance(DummyCache2.class);
		} catch (IllegalArgumentException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IllegalAccessException.class)
	public void testNewInstanceConstructorThrows2() throws Throwable {
		try {
			conf.newInstance(DummyCache3.class);
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

	public static abstract class ACache extends DummyCache {

		/**
         * @param configuration
         */
		public ACache(CacheConfiguration configuration) {
			super(configuration);
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

	public static class DummyCache3 extends DummyCache {

		/**
         * @param configuration
         */
		private DummyCache3(CacheConfiguration configuration) {
			super(configuration);
		}
	}
}
