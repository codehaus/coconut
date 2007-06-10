package org.coconut.cache.tck.service.servicemanager;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class ServiceManagerOnCache extends AbstractCacheTCKTestBundle {

    @Test
    public void testUnknownService() {
        c = newCache();
        assertFalse(c.hasService(Object.class));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNonConfiguredIAE() {
        c = newCache();
        c.getService(Object.class);
    }
}
