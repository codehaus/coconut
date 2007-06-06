/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.integration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.tck.other.CacheEntryBundle;
import org.coconut.cache.tck.service.loading.AbstractLoadingTestBundle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingCacheEntry extends CacheEntryBundle {

    /**
     * Tests that timestamp is set for creation date when loading values
     */
    @Test
    public void testCreationDateFromLoader() {
        CacheConfiguration<Integer, String> conf = CacheConfiguration.create();
        conf.setClock(clock);
        conf.loading().setLoader(
                AbstractLoadingTestBundle.DEFAULT_LOADER);
        c = newCache(conf);
        clock.setTimestamp(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getCreationTime());
    }

    @Test
    public void testLastUpdateFromLoader() {
        CacheConfiguration<Integer, String> conf = CacheConfiguration.create();
        conf.setClock(clock);
        conf.loading().setLoader(
                AbstractLoadingTestBundle.DEFAULT_LOADER);
        c = newCache(conf);
        clock.setTimestamp(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getLastUpdateTime());
    }

    @Test
    public void testAccessFromLoader() {
        CacheConfiguration<Integer, String> conf = CacheConfiguration.create();
        conf.setClock(clock);
        conf.loading().setLoader(
                AbstractLoadingTestBundle.DEFAULT_LOADER);
        c = newCache(conf);
        clock.setTimestamp(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getLastAccessTime());

        // tests that a never accessed entry returns 0
        c.getService(CacheLoadingService.class).load(M2.getKey());
        assertEquals(0l, getEntry(M2).getLastAccessTime());
    }
}
