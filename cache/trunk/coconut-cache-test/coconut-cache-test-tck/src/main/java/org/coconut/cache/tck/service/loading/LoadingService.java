/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.test.MockTestCase;
import org.junit.Test;

/**
 * Tests whether or not the cache loading service is available.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class LoadingService extends AbstractCacheTCKTestBundle {

    /**
     * Tests that a cache that has no configured cache loader. Does not have
     * {@link CacheLoadingService} available.
     */
    @Test
    public void noLoadingServiceIfNoLoaderConfigured() {
        c = newCache();
        assertFalse(c.hasService(CacheLoadingService.class));
    }

    /**
     * Tests that a cache that has no configured cache loader. Does not have
     * {@link CacheLoadingService} available.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getLoadingServiceFailIfNoLoaderConfigured() {
        c = newCache();
        c.getService(CacheLoadingService.class);
    }

    /**
     * Tests that a cache that has a configured cache loader. Will have a
     * {@link CacheLoadingService} available.
     */
    @Test
    public void hasAndGetLoadingService() {
        c = newCache(newConf().loading().setLoader(
                MockTestCase.mockDummy(CacheLoader.class)));
        assertTrue(c.hasService(CacheLoadingService.class));
        // check that it doesn't fail with a classcast exception
        c.getService(CacheLoadingService.class);
    }
}
