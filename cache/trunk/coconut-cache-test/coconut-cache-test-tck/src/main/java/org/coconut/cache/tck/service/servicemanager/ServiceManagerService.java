/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ServiceManagerService extends AbstractCacheTCKTest {

    @Test
    public void testUnknownService() {
        init();
        assertFalse(services().hasService(Object.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonConfiguredIAE() {
        init();
        c.getService(Object.class);
    }

    @Test
    public void serviceManagerAvailable() {
        init();
        CacheServiceManagerService s = c.getService(CacheServiceManagerService.class);
        assertSame(s, s.getAllServices().get(CacheServiceManagerService.class));
        assertSame(s, s.getService(CacheServiceManagerService.class));
    }

    @Test(expected = NullPointerException.class)
    public void serviceManagerGetNPE() {
        init();
        c.getService(null);
    }

    @Test
    public void testServiceAvailable() {
    // TODO fix
    // assertNotNull(c.getService(CacheServiceManagerService.class));
    }
}
