package org.coconut.cache.tck.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationService extends AbstractCacheTCKTestBundle {

    @Before
    public void setup() {
        c = newCache();
    }

    @Test
    public void expirationServiceAvailable() {
        assertTrue(c.hasService(CacheExpirationService.class));
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
}
