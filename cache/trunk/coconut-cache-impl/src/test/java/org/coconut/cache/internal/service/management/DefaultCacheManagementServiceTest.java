package org.coconut.cache.internal.service.management;

import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultCacheManagementServiceTest {
    Mockery context = new JUnit4Mockery();

    @Test(expected = NullPointerException.class)
    public void constructorNPE1() {
        new DefaultCacheManagementService(null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPE2() {
        new DefaultCacheManagementService(new CacheManagementConfiguration(), null);
    }

    @Test
    public void notEnabled() {
        DefaultCacheManagementService dcms = new DefaultCacheManagementService(
                new CacheManagementConfiguration(), "MyCache");
        assertFalse(dcms.isEnabled());
        assertEquals(CacheManagementConfiguration.SERVICE_NAME, dcms.getName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void notEnabledUOE() {
        new DefaultCacheManagementService(new CacheManagementConfiguration(), "MyCache")
                .getRoot();
    }
}
