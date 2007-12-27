/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M3;

import org.coconut.cache.test.util.CacheEntryFilter;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.cache.test.util.lifecycle.LifecyclePredicate;
import org.junit.Before;
import org.junit.Test;

public class RefreshFilter extends AbstractLoadingTestBundle {

    /**
     * Tests a custom expiration filter with get.
     */
    @Test
    public void refreshFilterGet() {
        CacheEntryFilter f = new CacheEntryFilter();
        init(conf.loading().setLoader(loader).setRefreshFilter(f));

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
        CacheEntryFilter f = new CacheEntryFilter();
        init(conf.loading().setLoader(loader).setRefreshFilter(f));

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
    public void filterLifecycle() {
        LifecyclePredicate filter = new LifecyclePredicate();
        init(newConf().loading().setRefreshFilter(filter).setLoader(new IntegerToStringLoader()));

        filter.assertInitializedButNotStarted();
        loading().load(M1.getKey());// lazy start
        awaitAllLoads();
        filter.assertInStartedPhase();
        filter.shutdownAndAssert(c);
    }
}
