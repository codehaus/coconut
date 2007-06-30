package org.coconut.cache.tck.service.management;

import javax.management.MBeanServerFactory;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.management.ManagedGroup;
import org.junit.Test;

public class ManagementService extends AbstractCacheTCKTestBundle {

    @Test
    public void testNonConfigured() {
        c = newCache();
        assertFalse(c.hasService(CacheManagementService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonConfiguredIAE() {
        c = newCache();
        c.getService(CacheManagementService.class);
    }

    @Test
    public void testConfigured() {
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(
                MBeanServerFactory.createMBeanServer()));
        assertTrue(c.hasService(CacheManagementService.class));
        assertTrue(c.getService(CacheManagementService.class) instanceof CacheManagementService);
    }

    @Test
    public void testGetRoot() {
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(
                MBeanServerFactory.createMBeanServer()));
        CacheManagementService cms = c.getService(CacheManagementService.class);
        assertNotNull(cms.getRoot());
        ManagedGroup mg = cms.getRoot();
        assertNull(mg.getParent());
        assertEquals(3, mg.getChildren().size());
        assertNotNull(findChild(mg, CacheEvictionConfiguration.SERVICE_NAME));
        assertNotNull(findChild(mg, CacheExpirationConfiguration.SERVICE_NAME));
        assertNotNull(findChild(mg, CacheMXBean.MANAGED_SERVICE_NAME));
    }
    
    private static ManagedGroup findChild(ManagedGroup mg, String name) {
        for (ManagedGroup m : mg.getChildren()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}
