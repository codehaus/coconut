package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.internal.service.servicemanager.UnsynchronizedPicoCacheServiceManager;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    
    
    
}
