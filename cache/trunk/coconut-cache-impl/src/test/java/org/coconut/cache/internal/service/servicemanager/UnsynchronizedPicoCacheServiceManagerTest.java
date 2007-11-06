package org.coconut.cache.internal.service.servicemanager;

import static org.junit.Assert.assertFalse;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class UnsynchronizedPicoCacheServiceManagerTest {
    Mockery context = new JUnit4Mockery();

    Cache<?, ?> cache;

    private InternalCacheSupport<?, ?> helper;

    @Before
    public void before() {
        helper = context.mock(InternalCacheSupport.class);
        cache = context.mock(Cache.class);
    }

    @Test
    public void nothing() {
        
    }
    //@Test
    public void testConstructor() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(
                cache, helper, conf);
        assertFalse(m.isStarted());
        assertFalse(m.isShutdown());
        assertFalse(m.isTerminated());
    }
    
//    //@Test
//    public void testShutdown() {
//        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
//        UnsynchronizedCacheServiceManager m = new UnsynchronizedCacheServiceManager(
//                cache, helper, conf);
//        assertFalse(m.isStarted());
//        assertFalse(m.isShutdown());
//        assertFalse(m.isTerminated());
//    }
    
}