/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static org.junit.Assert.assertSame;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.test.MockTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link CacheServices} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheServicesTest {
    private Mockery context = new JUnit4Mockery();

    private Cache<Integer, String> cache;

    @Before
    public void setupCache() {
        cache = context.mock(Cache.class);
    }

    @Test
    public void eventService() {
        final CacheEventService service = MockTestCase.mockDummy(CacheEventService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheEventService.class);
                will(returnValue(service));
            }
        });
        CacheEventService<Integer, String> ces = CacheServices.event(cache);
        assertSame(service, ces);
    }

    @Test
    public void evictionService() {
        final CacheEvictionService service = MockTestCase
                .mockDummy(CacheEvictionService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheEvictionService.class);
                will(returnValue(service));
            }
        });
        CacheEvictionService<Integer, String> ces = CacheServices.eviction(cache);
        assertSame(service, ces);
    }

    @Test
    public void expirationService() {
        final CacheExpirationService service = MockTestCase
                .mockDummy(CacheExpirationService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheExpirationService.class);
                will(returnValue(service));
            }
        });
        CacheExpirationService<Integer, String> ces = CacheServices.expiration(cache);
        assertSame(service, ces);
    }

    @Test
    public void loadingService() {
        final CacheLoadingService service = MockTestCase
                .mockDummy(CacheLoadingService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheLoadingService.class);
                will(returnValue(service));
            }
        });
        CacheLoadingService<Integer, String> ces = CacheServices.loading(cache);
        assertSame(service, ces);
    }

    @Test
    public void managementService() {
        final CacheManagementService service = MockTestCase
                .mockDummy(CacheManagementService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheManagementService.class);
                will(returnValue(service));
            }
        });
        CacheManagementService ces = CacheServices.management(cache);
        assertSame(service, ces);
    }

    @Test
    public void serviceManagerService() {
        final CacheServiceManagerService service = MockTestCase
                .mockDummy(CacheServiceManagerService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheServiceManagerService.class);
                will(returnValue(service));
            }
        });
        CacheServiceManagerService ces = CacheServices.servicemanager(cache);
        assertSame(service, ces);
    }

    @Test
    public void statisticsService() {
        final CacheStatisticsService service = MockTestCase
                .mockDummy(CacheStatisticsService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheStatisticsService.class);
                will(returnValue(service));
            }
        });
        CacheStatisticsService ces = CacheServices.statistics(cache);
        assertSame(service, ces);
    }
}
