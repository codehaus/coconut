package org.coconut.cache.tck.service.expiration;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class ExpirationServiceGeneral extends AbstractCacheTCKTestBundle {

    @Test
    public void assertNoLoadingService() {
        c = newCache();
        assertNotNull(c.getService(CacheExpirationService.class));

    }
}
