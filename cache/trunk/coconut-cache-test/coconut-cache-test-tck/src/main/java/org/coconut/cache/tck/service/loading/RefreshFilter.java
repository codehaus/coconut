/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M3;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.test.util.CacheEntryFilter;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.cache.test.util.lifecycle.LifecycleFilter;
import org.coconut.cache.test.util.managed.ManagedFilter;
import org.junit.Before;
import org.junit.Test;

public class RefreshFilter extends AbstractLoadingTestBundle {

    private CacheEntryFilter f;

    @Before
    public void setUpCaches() {
        f = new CacheEntryFilter();
        c = newCache(newConf().loading().setLoader(loader).setRefreshFilter(f));
    }

    /**
     * Tests a custom expiration filter with get.
     */
    @Test
    public void refreshFilterGet() {
        assertGet(M1);
        assertEquals(loader.getNumberOfLoads(), 1);
        f.setAccept(true);// All entries should be refreshed
        loader.setBase(2);
        loading().loadAll();
        awaitAllLoads();
        assertEquals(loader.getNumberOfLoads(), 2);
        assertEquals(M3.getValue(), get(M1.getKey()));
    }

    /**
     * Tests that a cacheloader returning null for a value that should be refreshed, will
     * not override the existing value with <code>null</code>
     */
    @Test
    public void refreshFilterGetNullLoad() {
        assertGet(M1);
        assertEquals(loader.getNumberOfLoads(), 1);
        f.setAccept(true);// All entries should be refreshed
        loader.setBase(2);
        loader.setDoReturnNull(true);
        loading().loadAll();
        awaitAllLoads();
        assertEquals(loader.getNumberOfLoads(), 2);
        assertGet(M1);
    }

    @Test
    public void filterManagement() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.management().setEnabled(true);
        ManagedFilter filter = new ManagedFilter();
        c = newCache(cc.loading().setRefreshFilter(filter).setLoader(new IntegerToStringLoader())
                .c());
        prestart();
        assertNotNull(
                "The Filter extends ManagedObject, and its manage() method should have been invoked",
                filter.getManagedGroup());
    }

    @Test
    public void filterLifecycle() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        LifecycleFilter filter = new LifecycleFilter();
        c = newCache(cc.loading().setRefreshFilter(filter).setLoader(new IntegerToStringLoader())
                .c());

        filter.assertNotStarted();
        loading().load(M1.getKey());// lazy start
        awaitAllLoads();
        filter.assertInStartedPhase();
        filter.shutdownAndAssert(c);
    }
}
