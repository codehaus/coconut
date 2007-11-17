/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
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
    }

    /**
     * A default instance of CacheManagementConfiguration is not enabled. The getRoot()
     * method should throw an UnsupportedOperationException
     */
    @Test(expected = UnsupportedOperationException.class)
    public void notEnabledUOE() {
        new DefaultCacheManagementService(new CacheManagementConfiguration(), "MyCache")
                .getRoot();
    }
}
