/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.integration;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_REMOVED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.tck.service.event.AbstractEventTestBundle;
import org.coconut.test.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusStatistics extends AbstractEventTestBundle {

    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.event();
        conf.expiration();
        c = newCache(conf);
    }

//    public void testStatisticsReset() throws Exception {
//        assertNotNull(subscribe(CacheEventFilters.CACHE_RESET_STATISTICS_FILTER));
//        c2.put(1, "B"); // sequenceid=1
//        c2.get(0);
//        c2.get(1);
//        c2.getService(CacheStatisticsService.class).resetStatistics();
//        CacheEvent.CacheStatisticsReset<?, ?> cleared = consumeItem(c2,
//                CacheEvent.CacheStatisticsReset.class);
//        assertEquals(0.5, cleared.getPreviousHitStat().getHitRatio(), 0.00001);
//        assertEquals(1, cleared.getPreviousHitStat().getNumberOfHits());
//        assertEquals(1, cleared.getPreviousHitStat().getNumberOfMisses());
//    }
}
