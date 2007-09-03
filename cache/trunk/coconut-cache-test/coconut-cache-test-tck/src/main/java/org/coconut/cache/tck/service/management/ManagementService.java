/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.management;

import javax.management.MBeanServerFactory;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.management.ManagedGroup;
import org.junit.Test;

public class ManagementService extends AbstractCacheTCKTest {

    @Test
    public void testNonConfigured() {
        c = newCache();
        assertFalse(services().hasService(CacheManagementService.class));
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
        assertTrue(services().hasService(CacheManagementService.class));
        assertTrue(c.getService(CacheManagementService.class) instanceof CacheManagementService);
    }

    @Test
    public void testGetRoot() {
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(
                MBeanServerFactory.createMBeanServer()));
        assertNotNull(management().getRoot());
        ManagedGroup mg = management().getRoot();
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
