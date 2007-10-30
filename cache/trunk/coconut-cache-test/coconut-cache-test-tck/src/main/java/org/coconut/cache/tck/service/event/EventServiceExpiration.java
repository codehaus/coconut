/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.*;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_UPDATED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEntryEvent.*;
import org.coconut.cache.tck.service.event.AbstractEventTestBundle;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.test.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventServiceExpiration extends AbstractEventTestBundle {

    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.event();
        conf.expiration();
        c = newCache(conf);
    }

    @Test
    public void itemRemovedExpired() throws Exception {
        c = newCache(newConf().setClock(clock).event().setEnabled(true));
        subscribe(CACHEENTRY_REMOVED_FILTER);
        expiration().put(M1.getKey(), M1.getValue(), 1, TimeUnit.MILLISECONDS);
        expiration().putAll(CollectionUtils.asMap(M2, M3), 2, TimeUnit.MILLISECONDS);

        clock.incrementTimestamp();
        expiration().purgeExpired();
        assertEquals(2, c.size());

        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertTrue(r.hasExpired());

        clock.incrementTimestamp(1);
        expiration().purgeExpired();

        Collection<ItemRemoved> removed = consumeItems(ItemRemoved.class, M3, M2);
        for (ItemRemoved i : removed) {
            assertTrue(i.hasExpired());
        }

    }

    public void itemUpdatedExpiredWithLoading() throws Exception {
        c = newCache(newConf().setClock(clock).loading().setLoader(
                new IntegerToStringLoader()).c().event().setEnabled(true));
        expiration().put(M1.getKey(), M1.getValue(), 1, TimeUnit.MILLISECONDS);
        expiration().putAll(CollectionUtils.asMap(M2, M3), 3, TimeUnit.MILLISECONDS);
        subscribe(CACHEENTRYEVENT_FILTER);
        clock.incrementTimestamp(2);
        assertEquals(3, c.size());

        c.getAll(Arrays.asList(M1.getKey(), M2.getKey(), M3.getKey()));
        ItemUpdated<?, ?> r = consumeItem(ItemUpdated.class, M1);
        assertTrue(r.hasExpired());
     //   assertTrue(r.isLoaded());

        clock.incrementTimestamp(2);
        c.getAll(Arrays.asList(M1.getKey(), M2.getKey(), M3.getKey()));

        Collection<ItemUpdated> removed = consumeItems(ItemUpdated.class, M2, M3);
        for (ItemUpdated i : removed) {
            assertTrue(i.hasExpired());
   //         assertTrue(i.isLoaded());
        }

    }
}
