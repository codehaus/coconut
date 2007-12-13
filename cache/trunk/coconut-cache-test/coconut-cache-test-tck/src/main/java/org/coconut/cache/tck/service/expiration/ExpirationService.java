/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionTestUtil.M1_TO_M5_MAP;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheException;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationService extends AbstractCacheTCKTest {

    @Test
    public void expirationServiceAvailable() {
        assertTrue(services().hasService(CacheExpirationService.class));
        assertNotNull(c.getService(CacheExpirationService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultTimeToLiveIAE() {
        expiration().setDefaultTimeToLive(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void setDefaultTimeToLiveNPE() {
        expiration().setDefaultTimeToLive(123, null);
    }

    @Test
    public void setGetDefaultTimeToLive() {
        assertEquals(CacheExpirationService.NEVER_EXPIRE, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, expiration().getDefaultTimeToLive(
                TimeUnit.SECONDS));

        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
        assertEquals(2l, expiration().getDefaultTimeToLive(TimeUnit.SECONDS));

        setCache(newConf().expiration().setDefaultTimeToLive(5, TimeUnit.SECONDS));
        assertEquals(5 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
        assertEquals(5l, expiration().getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    /**
     * {@link CacheExpirationService#setDefaultTimeToLive(long, TimeUnit) and CacheExpirationService#getDefaultTimeToLive(TimeUnit) should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void setGetShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail while shutdown
        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        // should not fail while terminated
        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
    }

    @Before
    public void setup() {
        c = newCache();
    }

    @Test(expected = CacheException.class)
    public void testIllegalPutAllCall() {
        c = newCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                CacheExpirationService ces = c.getService(CacheExpirationService.class);
                ces.putAll(M1_TO_M5_MAP, 10, TimeUnit.SECONDS);// should fail
            }
        }));
        prestart();
    }

    @Test(expected = CacheException.class)
    public void testIllegalPutCall() {
        c = newCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                CacheExpirationService ces = c.getService(CacheExpirationService.class);
                ces.put(1, "foo", 10, TimeUnit.SECONDS);// should fail
            }
        }));
        prestart();
    }
}
