/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.InternalCache;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class UnsynchronizedCacheServiceManagerTest {
    Mockery context = new JUnit4Mockery();

    Cache<?, ?> cache;

    private InternalCache<?, ?> helper;

    @Before
    public void before() {
        helper = context.mock(InternalCache.class);
        cache = context.mock(Cache.class);
    }

    @Test
    public void nothing() {

    }

    // @Test
    public void testConstructor() {
// CacheConfiguration<?, ?> conf = CacheConfiguration.create();
// ServiceComposer sc = ServiceComposer.compose(cache, helper, conf,
// Collections.EMPTY_LIST);
// UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(cache, sc);
// assertFalse(m.isStarted());
// assertFalse(m.isShutdown());
// assertFalse(m.isTerminated());
    }

// //@Test
// public void testShutdown() {
// CacheConfiguration<?, ?> conf = CacheConfiguration.create();
// UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(
// cache, helper, conf);
// assertFalse(m.isStarted());
// assertFalse(m.isShutdown());
// assertFalse(m.isTerminated());
// }

}
