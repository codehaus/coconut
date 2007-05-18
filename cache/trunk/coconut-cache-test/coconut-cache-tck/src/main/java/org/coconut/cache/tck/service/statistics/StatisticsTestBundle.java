/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.statistics;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Before;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StatisticsTestBundle extends CacheTestBundle {
    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> noStatsCache;

    protected CacheStatisticsService service;

    @Before
    public void setupLoading() {
        noStatsCache = c;
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        c = newCache(cc.getConfiguration(CacheStatisticsConfiguration.class).c());
        service = c.getService(CacheStatisticsService.class);
    }

    public void assertNoLoadingService() {
        assertNotNull(c.getService(CacheStatisticsService.class));
        assertNull(noStatsCache.getService(CacheStatisticsService.class));
    }
}
