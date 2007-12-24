/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * Tests whether or not the cache loading service is available at runtime.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class LoadingService extends AbstractCacheTCKTest {

    /**
     * Tests that if a cache has no configured cache loader there is no
     * {@link CacheLoadingService} available.
     */
    @Test
    public void noLoadingServiceIfNoLoaderConfigured() {
        setCache();
        assertFalse(services().hasService(CacheLoadingService.class));
    }

    /**
     * Tests that if a cache has no configured cache loader there is no
     * {@link CacheLoadingService} available.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getLoadingServiceFailIfNoLoaderConfigured() {
        setCache();
        c.getService(CacheLoadingService.class);
    }

    /**
     * Tests that a cache that has a configured cache loader. Will have a
     * {@link CacheLoadingService} available.
     */
    @Test
    public void hasAndGetLoadingService() {
        c = newCache(newConf().loading().setLoader(TestUtil.dummy(CacheLoader.class)));
        assertTrue(services().hasService(CacheLoadingService.class));
        // check that it doesn't fail with a classcast exception
        assertNotNull(c.getService(CacheLoadingService.class));
        assertTrue(services().getAllServices().containsKey(CacheLoadingService.class));
        assertTrue(services().getAllServices().get(CacheLoadingService.class) instanceof CacheLoadingService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultTimeToLiveIAE() {
        c = newCache(newConf().loading().setLoader(TestUtil.dummy(CacheLoader.class)));
        loading().setDefaultTimeToRefresh(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void setDefaultTimeToLiveNPE() {
        c = newCache(newConf().loading().setLoader(TestUtil.dummy(CacheLoader.class)));
        loading().setDefaultTimeToRefresh(123, null);
    }

    @Test
    public void setGetDefaultTimeToLive() {
        c = newCache(newConf().loading().setLoader(TestUtil.dummy(CacheLoader.class)));
        assertEquals(TimeToLiveAttribute.FOREVER, loading().getDefaultTimeToRefresh(
                TimeUnit.NANOSECONDS));
        assertEquals(TimeToLiveAttribute.FOREVER, loading().getDefaultTimeToRefresh(
                TimeUnit.SECONDS));

        loading().setDefaultTimeToRefresh(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, loading().getDefaultTimeToRefresh(
                TimeUnit.NANOSECONDS));
        assertEquals(2l, loading().getDefaultTimeToRefresh(TimeUnit.SECONDS));

        setCache(newConf().loading().setLoader(TestUtil.dummy(CacheLoader.class))
                .setDefaultTimeToRefresh(5, TimeUnit.SECONDS));
        assertEquals(5 * 1000 * 1000 * 1000l, loading().getDefaultTimeToRefresh(
                TimeUnit.NANOSECONDS));
        assertEquals(5l, loading().getDefaultTimeToRefresh(TimeUnit.SECONDS));
    }
}
