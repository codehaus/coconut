/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.management.ManagedGroup;
import org.coconut.test.MockTestCase;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheServiceTest {

	@Test
	public void testServiceConstructor() {
		Cache<?,?> c = MockTestCase.mockDummy(Cache.class);
		CacheConfiguration<?,?> conf = CacheConfiguration.create();
		UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(c,
				null,conf);
		assertEquals(ServiceStatus.NOTRUNNING, m.getCurrentState());
		assertFalse(m.isRunning());
	}

	@Test
	public void testServiceStartNoServices() {
		Cache<?,?> c = MockTestCase.mockDummy(Cache.class);
		CacheConfiguration<?,?> conf = CacheConfiguration.create();
		UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(c,
		        null,conf);
		m.prestart();
		assertEquals(ServiceStatus.RUNNING, m.getCurrentState());
		assertEquals(0, m.getPublicServices().size());
	}

	@Test
	public void testServiceStartDummyService() {
		Cache<?,?> c = MockTestCase.mockDummy(Cache.class);
		CacheConfiguration<?,?> conf = CacheConfiguration.create();
		InternalCacheServiceManager m = new UnsynchronizedCacheServiceManager(c,
		        null,conf);
		m.registerService(CacheManagementService.class, MyManagementService.class);
		m.prestart();
		assertEquals(ServiceStatus.RUNNING, m.getCurrentState());
		assertEquals(2, m.getPublicServices().size());
		assertTrue(m.hasPublicService(Integer.class));
		assertTrue(m.hasPublicService(String.class));
		assertEquals(5, m.getPublicService(Integer.class));
		assertEquals("jjj", m.getPublicService(String.class));
	}

	public static class MyManagementService extends AbstractCacheService implements
			CacheManagementService {

		/**
         * @param name
         */
		public MyManagementService() {
			super("myService");
		}

		/**
		 * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
		 */
		public ManagedGroup getRoot() {
			return null;
		}

        @Override
        public void start(CacheConfiguration<?, ?> configuration,
                Map<Class<?>, Object> serviceMap) {
            serviceMap.put(Integer.class, 5);
            serviceMap.put(String.class, "jjj");
        }
	}
}
