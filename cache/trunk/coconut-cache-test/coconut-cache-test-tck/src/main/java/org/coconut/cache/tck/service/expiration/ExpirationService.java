/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationService extends AbstractCacheTCKTest {

    @Before
    public void setup() {
        c = newCache();
    }

    @Test
    public void expirationServiceAvailable() {
        assertTrue(services().hasService(CacheExpirationService.class));
        assertNotNull(c.getService(CacheExpirationService.class));
    }

    @Test
    public void testSetGetDefaultTimeToLive() {
        assertEquals(CacheExpirationService.NEVER_EXPIRE, expiration()
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, expiration()
                .getDefaultTimeToLive(TimeUnit.SECONDS));

        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
        assertEquals(2l, expiration().getDefaultTimeToLive(TimeUnit.SECONDS));

        setCache(newConf().expiration().setDefaultTimeToLive(5, TimeUnit.SECONDS));
        assertEquals(5 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
        assertEquals(5l, expiration().getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultTimeToLiveIAE() {
        expiration().setDefaultTimeToLive(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void setDefaultTimeToLiveNPE() {
        expiration().setDefaultTimeToLive(123, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTimeToLive() {
        expiration().setDefaultTimeToLive(-1, TimeUnit.NANOSECONDS);
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

        //should not fail while shutdown
        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        //should not fail while terminated
        expiration().setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, expiration().getDefaultTimeToLive(
                TimeUnit.NANOSECONDS));
    }
}
