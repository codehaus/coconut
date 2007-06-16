package org.coconut.cache.tck.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

public class ExpirationService extends AbstractCacheTCKTestBundle {

    @Before
    public void setup() {
        c = newCache();
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
    public void testSetDefaultTimeToLiveIAE() {
        expiration().setDefaultTimeToLive(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testSetDefaultTimeToLiveNPE() {
        expiration().setDefaultTimeToLive(123, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDefaultTimeToLiveMsIAE() {
        expiration().setDefaultTimeToLive(-1, TimeUnit.NANOSECONDS);
    }
}
