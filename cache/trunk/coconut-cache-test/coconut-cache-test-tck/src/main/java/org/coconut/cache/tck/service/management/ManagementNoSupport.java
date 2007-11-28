package org.coconut.cache.tck.service.management;

import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.IllegalCacheConfigurationException;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Test;

@RequireService(value = { CacheManagementService.class }, isAvailable = false)
public class ManagementNoSupport extends AbstractCacheTCKTest {
    @Test(expected = IllegalCacheConfigurationException.class)
    public void noManagementSupport() {
        newCache(newConf().management().setEnabled(true));
    }
}
