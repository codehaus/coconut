/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_REMOVED_FILTER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.test.CollectionTestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EventServiceExpiration extends AbstractEventTestBundle {

    @Test
    public void itemRemovedExpired() throws Exception {
        init();
        subscribe(CACHEENTRY_REMOVED_FILTER);
        expiration().put(M1.getKey(), M1.getValue(), 1, TimeUnit.MILLISECONDS);
        expiration().putAll(CollectionTestUtil.asMap(M2, M3), 2, TimeUnit.MILLISECONDS);

        clock.incrementTimestamp();
        expiration().purgeExpired();
        assertSize(2);

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
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()));
        expiration().put(M1.getKey(), M1.getValue(), 1, TimeUnit.MILLISECONDS);
        expiration().putAll(CollectionTestUtil.asMap(M2, M3), 3, TimeUnit.MILLISECONDS);
        subscribe(CACHEENTRYEVENT_FILTER);
        clock.incrementTimestamp(2);
        assertSize(3);

        c.getAll(Arrays.asList(M1.getKey(), M2.getKey(), M3.getKey()));
        ItemUpdated<?, ?> r = consumeItem(ItemUpdated.class, M1);
        assertTrue(r.hasExpired());
        // assertTrue(r.isLoaded());

        clock.incrementTimestamp(2);
        c.getAll(Arrays.asList(M1.getKey(), M2.getKey(), M3.getKey()));

        Collection<ItemUpdated> removed = consumeItems(ItemUpdated.class, M2, M3);
        for (ItemUpdated i : removed) {
            assertTrue(i.hasExpired());
            // assertTrue(i.isLoaded());
        }

    }
}
