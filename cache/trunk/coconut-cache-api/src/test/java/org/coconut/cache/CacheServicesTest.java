package org.coconut.cache;

import static org.junit.Assert.assertEquals;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.test.MockTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheServicesTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void testFoo() {
        final Cache<Integer, String> cache = context.mock(Cache.class);
        final CacheEventService dummyEvent = MockTestCase
                .mockDummy(CacheEventService.class);
        final CacheEvictionService dummyEviction = MockTestCase
                .mockDummy(CacheEvictionService.class);
        final CacheExpirationService dummyExpiration = MockTestCase
                .mockDummy(CacheExpirationService.class);
        final CacheLoadingService dummyLoading = MockTestCase
                .mockDummy(CacheLoadingService.class);
        final CacheManagementService dummyManagement = MockTestCase
                .mockDummy(CacheManagementService.class);
        final CacheServiceManagerService dummyServiceManager = MockTestCase
        .mockDummy(CacheServiceManagerService.class);
        context.checking(new Expectations() {
            {
                one(cache).getService(CacheEventService.class);
                will(returnValue(dummyEvent));
                one(cache).getService(CacheEvictionService.class);
                will(returnValue(dummyEviction));
                one(cache).getService(CacheExpirationService.class);
                will(returnValue(dummyExpiration));
                one(cache).getService(CacheLoadingService.class);
                will(returnValue(dummyLoading));
                one(cache).getService(CacheManagementService.class);
                will(returnValue(dummyManagement));
                one(cache).getService(CacheServiceManagerService.class);
                will(returnValue(dummyServiceManager));
            }
        });

        CacheEventService<Integer, String> ces = CacheServices.event(cache);
        assertEquals(dummyEvent, ces);

        CacheEvictionService<Integer, String> cevs = CacheServices.eviction(cache);
        assertEquals(dummyEviction, cevs);

        CacheExpirationService<Integer, String> cexs = CacheServices.expiration(cache);
        assertEquals(dummyExpiration, cexs);

        CacheLoadingService<Integer, String> cls = CacheServices.loading(cache);
        assertEquals(dummyLoading, cls);

        CacheManagementService cms = CacheServices.management(cache);
        assertEquals(dummyManagement, cms);

        CacheServiceManagerService csms = CacheServices.servicemanager(cache);
        assertEquals(dummyServiceManager, csms);
    }
}
