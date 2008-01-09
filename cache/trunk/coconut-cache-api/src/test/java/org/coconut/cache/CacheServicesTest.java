/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static org.junit.Assert.assertSame;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.test.TestUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link CacheServicesOld} class.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheServicesTest {

    /** The junit mockery. */
    private final Mockery context = new JUnit4Mockery();

    /** The default cache to test upon. */
    private Cache<Integer, String> cache;

    /**
     * Setup the cache mock.
     */
    @Before
    public void setupCache() {
        cache = context.mock(Cache.class);
    }

    /**
     * Tests {@link CacheServices#event()}.
     */
    @Test
    public void eventService() {
        final CacheEventService service = TestUtil.dummy(CacheEventService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheEventService.class);
                will(returnValue(service));
            }
        });
        CacheEventService<Integer, String> ces = cache.services().event();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServices#memoryStore()}.
     */
    @Test
    public void evictionService() {
        final MemoryStoreService service = TestUtil.dummy(MemoryStoreService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(MemoryStoreService.class);
                will(returnValue(service));
            }
        });
        MemoryStoreService<Integer, String> ces = cache.services().memoryStore();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServices#expiration()}.
     */
    @Test
    public void expirationService() {
        final CacheExpirationService service = TestUtil.dummy(CacheExpirationService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheExpirationService.class);
                will(returnValue(service));
            }
        });
        CacheExpirationService<Integer, String> ces = cache.services().expiration();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServices#loading()}.
     */
    @Test
    public void loadingService() {
        final CacheLoadingService service = TestUtil.dummy(CacheLoadingService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheLoadingService.class);
                will(returnValue(service));
            }
        });
        CacheLoadingService<Integer, String> ces = cache.services().loading();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServicesOld#management(Cache)}.
     */
    @Test
    public void managementService() {
        final CacheManagementService service = TestUtil.dummy(CacheManagementService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheManagementService.class);
                will(returnValue(service));
            }
        });
        CacheManagementService ces = cache.services().management();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServicesOld#servicemanager(Cache)}.
     */
    @Test
    public void serviceManagerService() {
        final CacheServiceManagerService service = TestUtil
                .dummy(CacheServiceManagerService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheServiceManagerService.class);
                will(returnValue(service));
            }
        });
        CacheServiceManagerService ces = cache.services().servicemanager();;
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServices#statistics()}.
     */
    @Test
    public void statisticsService() {
        final CacheStatisticsService service = TestUtil.dummy(CacheStatisticsService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheStatisticsService.class);
                will(returnValue(service));
            }
        });
        CacheStatisticsService ces = cache.services().statistics();
        assertSame(service, ces);
    }

    /**
     * Tests {@link CacheServices#worker()}.
     */
    @Test
    public void workerService() {
        final CacheWorkerService service = TestUtil.dummy(CacheWorkerService.class);
        context.checking(new Expectations() {
            {
                one(cache).services();
                will(returnValue(new CacheServices(cache)));
                one(cache).getService(CacheWorkerService.class);
                will(returnValue(service));
            }
        });
        CacheWorkerService ces = cache.services().worker();
        assertSame(service, ces);
    }
}
