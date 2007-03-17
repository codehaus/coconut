/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.integration;

import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import java.util.Arrays;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingStatistics extends CacheTestBundle {

    /**
     * Tests that loading of elements does not does not affect the cache
     * statistics gathered.
     * 
     * @throws Exception
     *             test could not complete properly
     */
    @Test
    public void load() throws Exception {
        CacheConfiguration<Integer, String> conf = super.newConf();
        conf.serviceLoading().setBackend(new IntegerToStringLoader());
        c = newCache(conf);
        CacheLoadingService<Integer, String> load = c
                .getService(CacheLoadingService.class);
        Future<?> f = load.load(M5.getKey());
        Future<?> fAll = load.loadAll(Arrays.asList(M2.getKey(), M4.getKey()));
        try {
            f.get();
            fAll.get();
        } catch (UnsupportedOperationException ignore) {
            // cache does not support loading, ignore
        }
        assertHitstat(Float.NaN, 0, 0,c
                .getService(CacheStatisticsService.class).getHitStat());
    }
}
