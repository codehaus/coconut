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
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.service.event.AbstractEventTestBundle;
import org.coconut.test.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusExpiration extends AbstractEventTestBundle {

    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.event();
        conf.expiration();
        c = newCache(conf);
    }

    @Test
    public void itemRemovedExpired() throws Exception {
        c = newCache(newConf().setClock(clock));
        subscribe(CACHEENTRY_REMOVED_FILTER);
        c.getService(CacheExpirationService.class).put(M1.getKey(), M1.getValue(), 1,
                TimeUnit.MILLISECONDS);
        c.getService(CacheExpirationService.class).putAll(CollectionUtils.asMap(M2, M3),
                1, TimeUnit.MILLISECONDS);

        clock.incrementTimestamp(2);
        c.evict();
        assertEquals(2, c.size());

        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertTrue(r.hasExpired());

        clock.incrementTimestamp(2);
        c.evict();

        r = consumeItem(ItemRemoved.class, M2);
        assertTrue(r.hasExpired());
        r = consumeItem(ItemRemoved.class, M3);
        assertTrue(r.hasExpired());
    }
}
